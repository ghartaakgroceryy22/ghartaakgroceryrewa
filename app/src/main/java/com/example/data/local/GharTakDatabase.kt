package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    UserEntity::class,
    CategoryEntity::class,
    ProductEntity::class,
    CartItemEntity::class,
    AddressEntity::class,
    DeliveryZoneEntity::class,
    OrderEntity::class,
    OrderItemEntity::class,
    WalletTransactionEntity::class,
    CouponEntity::class,
    BannerEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class GharTakDatabase : RoomDatabase() {

  abstract fun groceryDao(): GroceryDao

  companion object {
    @Volatile
    private var INSTANCE: GharTakDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): GharTakDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          GharTakDatabase::class.java,
          "ghartak_grocery_database"
        )
          .addCallback(DatabaseCallback(scope))
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.groceryDao())
          }
        }
      }

      suspend fun populateInitialData(dao: GroceryDao) {
        dao.insertCategories(InitialData.categories)
        dao.insertProducts(InitialData.products)
        dao.insertDeliveryZones(InitialData.deliveryZones)
        dao.insertBanners(InitialData.banners)
        dao.insertCoupons(InitialData.coupons)
        dao.insertUser(InitialData.demoUser)
        for (addr in InitialData.defaultAddresses) {
          dao.insertAddress(addr)
        }
        for (tx in InitialData.initialWalletTransactions) {
          dao.insertWalletTransaction(tx)
        }

        // Also seed 1 completed order and 1 active order for realistic initial state
        val sampleOrderId = dao.insertOrder(
          OrderEntity(
            orderNumber = "GTG-REW-1002",
            customerName = "Rajesh Patel",
            customerPhone = "+91 94251 88320",
            deliveryAddressText = "House No. 42-B, Near Circuit House Road, Civil Lines, Rewa - 486001",
            area = "Civil Lines",
            subtotal = 398.0,
            deliveryCharge = 0.0,
            discountAmount = 50.0,
            walletAmountUsed = 0.0,
            totalAmount = 348.0,
            paymentMethod = PaymentMethod.MANUAL_UPI,
            paymentStatus = PaymentStatus.VERIFIED_APPROVED,
            upiRefNumber = "UPI/2609/7841920",
            orderStatus = OrderStatus.DELIVERED,
            assignedDeliveryPartnerName = "Ramesh Sharma (Rewa Hub)",
            assignedDeliveryPartnerPhone = "+91 98261 44550",
            createdAt = System.currentTimeMillis() - 86400000L * 2,
            cashbackEarned = 7.0
          )
        )
        dao.insertOrderItems(
          listOf(
            OrderItemEntity(
              orderId = sampleOrderId,
              productId = "p_aashirvaad_atta",
              productName = "Aashirvaad Shudh Chakki Atta",
              packSize = "5 kg",
              unitPrice = 245.0,
              quantity = 1,
              totalPrice = 245.0,
              imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500&auto=format&fit=crop&q=80"
            ),
            OrderItemEntity(
              orderId = sampleOrderId,
              productId = "p_amul_gold",
              productName = "Amul Gold Full Cream Fresh Milk",
              packSize = "500 ml",
              unitPrice = 33.0,
              quantity = 2,
              totalPrice = 66.0,
              imageUrl = "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=500&auto=format&fit=crop&q=80"
            )
          )
        )
      }
    }
  }
}
