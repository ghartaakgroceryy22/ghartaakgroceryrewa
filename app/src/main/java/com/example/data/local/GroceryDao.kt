package com.example.data.local

import androidx.room.*
import com.example.data.models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryDao {

  // Categories
  @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY displayOrder ASC")
  fun getAllCategories(): Flow<List<CategoryEntity>>

  @Query("SELECT * FROM categories ORDER BY displayOrder ASC")
  fun getAllCategoriesAdmin(): Flow<List<CategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CategoryEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategories(categories: List<CategoryEntity>)

  @Update
  suspend fun updateCategory(category: CategoryEntity)

  @Query("DELETE FROM categories WHERE id = :id")
  suspend fun deleteCategory(id: String)

  // Products
  @Query("SELECT * FROM products ORDER BY isFeatured DESC, name ASC")
  fun getAllProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE categoryId = :catId ORDER BY isPopular DESC, name ASC")
  fun getProductsByCategory(catId: String): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE isFeatured = 1 OR isDealOfTheDay = 1 LIMIT 12")
  fun getFeaturedProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE isDealOfTheDay = 1")
  fun getDealsOfTheDay(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE isPopular = 1")
  fun getPopularProducts(): Flow<List<ProductEntity>>

  @Query("SELECT * FROM products WHERE id = :id")
  suspend fun getProductById(id: String): ProductEntity?

  @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' OR hindiName LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%'")
  fun searchProducts(query: String): Flow<List<ProductEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProduct(product: ProductEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProducts(products: List<ProductEntity>)

  @Update
  suspend fun updateProduct(product: ProductEntity)

  @Query("DELETE FROM products WHERE id = :id")
  suspend fun deleteProduct(id: String)

  @Query("SELECT COUNT(*) FROM products")
  suspend fun getProductCount(): Int

  @Query("SELECT * FROM products WHERE stockCount <= 10")
  fun getLowStockProducts(): Flow<List<ProductEntity>>

  // Cart
  @Transaction
  @Query("SELECT * FROM cart_items ORDER BY addedAt DESC")
  fun getCartItemsWithProducts(): Flow<List<CartItemWithProduct>>

  @Query("SELECT * FROM cart_items WHERE productId = :productId LIMIT 1")
  suspend fun getCartItemByProductId(productId: String): CartItemEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCartItem(cartItem: CartItemEntity)

  @Update
  suspend fun updateCartItem(cartItem: CartItemEntity)

  @Query("DELETE FROM cart_items WHERE productId = :productId")
  suspend fun deleteCartItemByProductId(productId: String)

  @Query("DELETE FROM cart_items")
  suspend fun clearCart()

  // Addresses
  @Query("SELECT * FROM addresses ORDER BY isDefault DESC, id DESC")
  fun getAllAddresses(): Flow<List<AddressEntity>>

  @Query("SELECT * FROM addresses WHERE isDefault = 1 LIMIT 1")
  fun getDefaultAddress(): Flow<AddressEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAddress(address: AddressEntity): Long

  @Update
  suspend fun updateAddress(address: AddressEntity)

  @Query("UPDATE addresses SET isDefault = 0")
  suspend fun resetDefaultAddresses()

  @Query("DELETE FROM addresses WHERE id = :id")
  suspend fun deleteAddress(id: Long)

  // Delivery Zones
  @Query("SELECT * FROM delivery_zones")
  fun getAllDeliveryZones(): Flow<List<DeliveryZoneEntity>>

  @Query("SELECT * FROM delivery_zones WHERE zoneName = :zone LIMIT 1")
  suspend fun getDeliveryZone(zone: String): DeliveryZoneEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDeliveryZones(zones: List<DeliveryZoneEntity>)

  // Orders
  @Transaction
  @Query("SELECT * FROM orders ORDER BY createdAt DESC")
  fun getAllOrdersWithItems(): Flow<List<OrderWithItems>>

  @Transaction
  @Query("SELECT * FROM orders WHERE id = :orderId")
  fun getOrderWithItems(orderId: Long): Flow<OrderWithItems?>

  @Transaction
  @Query("SELECT * FROM orders WHERE orderStatus != 'DELIVERED' AND orderStatus != 'CANCELLED' ORDER BY createdAt DESC")
  fun getActiveOrders(): Flow<List<OrderWithItems>>

  @Transaction
  @Query("SELECT * FROM orders WHERE paymentStatus = 'PENDING_VERIFICATION' ORDER BY createdAt DESC")
  fun getPendingVerificationOrders(): Flow<List<OrderWithItems>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: OrderEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrderItems(items: List<OrderItemEntity>)

  @Update
  suspend fun updateOrder(order: OrderEntity)

  @Query("UPDATE orders SET orderStatus = :status WHERE id = :orderId")
  suspend fun updateOrderStatus(orderId: Long, status: OrderStatus)

  @Query("UPDATE orders SET paymentStatus = :paymentStatus, orderStatus = :orderStatus WHERE id = :orderId")
  suspend fun updatePaymentAndOrderStatus(orderId: Long, paymentStatus: PaymentStatus, orderStatus: OrderStatus)

  @Query("UPDATE orders SET orderStatus = 'CANCELLED', cancellationReason = :reason, paymentStatus = 'REJECTED' WHERE id = :orderId")
  suspend fun cancelOrder(orderId: Long, reason: String)

  // Wallet
  @Query("SELECT * FROM wallet_transactions ORDER BY timestamp DESC")
  fun getWalletTransactions(): Flow<List<WalletTransactionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWalletTransaction(tx: WalletTransactionEntity)

  // Coupons
  @Query("SELECT * FROM coupons WHERE isActive = 1")
  fun getActiveCoupons(): Flow<List<CouponEntity>>

  @Query("SELECT * FROM coupons WHERE code = :code AND isActive = 1 LIMIT 1")
  suspend fun getCoupon(code: String): CouponEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCoupons(coupons: List<CouponEntity>)

  // Banners
  @Query("SELECT * FROM promotional_banners")
  fun getAllBanners(): Flow<List<BannerEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBanners(banners: List<BannerEntity>)

  // User
  @Query("SELECT * FROM users LIMIT 1")
  fun getUser(): Flow<UserEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertUser(user: UserEntity)

  @Query("UPDATE users SET walletBalance = :balance WHERE id = :userId")
  suspend fun updateWalletBalance(userId: Long, balance: Double)
}
