package com.example.data.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Embedded
import androidx.room.Relation

enum class UserRole {
  CUSTOMER,
  DELIVERY_PARTNER,
  ADMIN
}

enum class OrderStatus(val label: String, val stepIndex: Int) {
  PLACED("Order Placed", 0),
  CONFIRMED("Confirmed", 1),
  PREPARING("Preparing Items", 2),
  READY_FOR_DELIVERY("Ready for Pickup", 3),
  OUT_FOR_DELIVERY("Out for Delivery", 4),
  DELIVERED("Delivered", 5),
  CANCELLED("Cancelled", -1)
}

enum class PaymentMethod(val title: String) {
  COD("Cash on Delivery"),
  MANUAL_UPI("Prepaid UPI & QR (2% Cashback)")
}

enum class PaymentStatus(val label: String) {
  PENDING_VERIFICATION("Payment Verification Pending"),
  VERIFIED_APPROVED("Payment Approved ✓"),
  REJECTED("Payment Rejected / Invalid"),
  COD_PENDING("COD Pending on Delivery"),
  COD_COLLECTED("COD Cash Collected ✓")
}

@Entity(tableName = "users")
data class UserEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val name: String,
  val email: String,
  val phone: String,
  val role: UserRole = UserRole.CUSTOMER,
  val walletBalance: Double = 50.0, // Welcome ₹50 reward
  val isVerified: Boolean = true,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
  @PrimaryKey val id: String,
  val name: String,
  val hindiName: String,
  val iconEmoji: String,
  val imageUrl: String = "",
  val displayOrder: Int = 0,
  val isActive: Boolean = true
)

@Entity(
  tableName = "products",
  indices = [Index("categoryId"), Index("name")]
)
data class ProductEntity(
  @PrimaryKey val id: String,
  val categoryId: String,
  val name: String,
  val hindiName: String,
  val brand: String,
  val description: String,
  val packSize: String, // e.g., "1 kg", "500 g", "1 L"
  val price: Double,
  val mrp: Double,
  val discountPercent: Int,
  val inStock: Boolean = true,
  val stockCount: Int = 50,
  val isFeatured: Boolean = false,
  val isPopular: Boolean = false,
  val isDealOfTheDay: Boolean = false,
  val imageUrl: String,
  val badgeText: String = ""
)

@Entity(
  tableName = "cart_items",
  foreignKeys = [
    ForeignKey(
      entity = ProductEntity::class,
      parentColumns = ["id"],
      childColumns = ["productId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index(value = ["productId"], unique = true)]
)
data class CartItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val productId: String,
  val quantity: Int = 1,
  val addedAt: Long = System.currentTimeMillis()
)

data class CartItemWithProduct(
  @Embedded val cartItem: CartItemEntity,
  @Relation(
    parentColumn = "productId",
    entityColumn = "id"
  )
  val product: ProductEntity
)

@Entity(tableName = "addresses")
data class AddressEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val recipientName: String,
  val phone: String,
  val houseFlatNo: String,
  val street: String,
  val area: String, // Rewa area, e.g. "Civil Lines", "Bodabag", "Urrahat"
  val landmark: String,
  val pincode: String = "486001",
  val addressType: String = "Home", // Home, Work, Other
  val isDefault: Boolean = false
)

@Entity(tableName = "delivery_zones")
data class DeliveryZoneEntity(
  @PrimaryKey val zoneName: String,
  val pincode: String,
  val isDeliverable: Boolean = true,
  val deliveryFee: Double = 25.0,
  val minOrderFreeDelivery: Double = 199.0,
  val estimatedMins: String = "15-25 mins"
)

@Entity(
  tableName = "orders",
  indices = [Index("orderNumber"), Index("orderStatus")]
)
data class OrderEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val orderNumber: String,
  val customerName: String,
  val customerPhone: String,
  val deliveryAddressText: String,
  val area: String,
  val subtotal: Double,
  val deliveryCharge: Double,
  val discountAmount: Double = 0.0,
  val walletAmountUsed: Double = 0.0,
  val totalAmount: Double,
  val paymentMethod: PaymentMethod,
  val paymentStatus: PaymentStatus,
  val upiRefNumber: String = "",
  val paymentProofNote: String = "",
  val orderStatus: OrderStatus = OrderStatus.PLACED,
  val cancellationReason: String = "",
  val assignedDeliveryPartnerName: String = "Ramesh Sharma (Rewa Hub)",
  val assignedDeliveryPartnerPhone: String = "+91 98261 44550",
  val createdAt: Long = System.currentTimeMillis(),
  val estimatedDeliveryTime: String = "20-30 Mins",
  val cashbackEarned: Double = 0.0
)

@Entity(
  tableName = "order_items",
  foreignKeys = [
    ForeignKey(
      entity = OrderEntity::class,
      parentColumns = ["id"],
      childColumns = ["orderId"],
      onDelete = ForeignKey.CASCADE
    )
  ],
  indices = [Index("orderId")]
)
data class OrderItemEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val orderId: Long,
  val productId: String,
  val productName: String,
  val packSize: String,
  val unitPrice: Double,
  val quantity: Int,
  val totalPrice: Double,
  val imageUrl: String
)

data class OrderWithItems(
  @Embedded val order: OrderEntity,
  @Relation(
    parentColumn = "id",
    entityColumn = "orderId"
  )
  val items: List<OrderItemEntity>
)

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val amount: Double,
  val isCredit: Boolean, // true = credit, false = debit
  val title: String,
  val description: String,
  val timestamp: Long = System.currentTimeMillis(),
  val orderNumber: String = ""
)

@Entity(tableName = "coupons")
data class CouponEntity(
  @PrimaryKey val code: String,
  val description: String,
  val discountPercent: Int = 0,
  val discountAmount: Double = 0.0,
  val minOrderAmount: Double = 149.0,
  val isActive: Boolean = true
)

@Entity(tableName = "promotional_banners")
data class BannerEntity(
  @PrimaryKey val id: String,
  val title: String,
  val subtitle: String,
  val tag: String,
  val discountText: String,
  val backgroundHex: String,
  val actionCategory: String
)
