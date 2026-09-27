package com.example.data.repository

import com.example.data.local.GroceryDao
import com.example.data.local.InitialData
import com.example.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class GroceryRepository(private val dao: GroceryDao) {

  val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategories()
  val allCategoriesAdmin: Flow<List<CategoryEntity>> = dao.getAllCategoriesAdmin()
  val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
  val featuredProducts: Flow<List<ProductEntity>> = dao.getFeaturedProducts()
  val dealsOfTheDay: Flow<List<ProductEntity>> = dao.getDealsOfTheDay()
  val popularProducts: Flow<List<ProductEntity>> = dao.getPopularProducts()
  val lowStockProducts: Flow<List<ProductEntity>> = dao.getLowStockProducts()
  val cartItems: Flow<List<CartItemWithProduct>> = dao.getCartItemsWithProducts()
  val addresses: Flow<List<AddressEntity>> = dao.getAllAddresses()
  val defaultAddress: Flow<AddressEntity?> = dao.getDefaultAddress()
  val deliveryZones: Flow<List<DeliveryZoneEntity>> = dao.getAllDeliveryZones()
  val allOrders: Flow<List<OrderWithItems>> = dao.getAllOrdersWithItems()
  val activeOrders: Flow<List<OrderWithItems>> = dao.getActiveOrders()
  val pendingVerificationOrders: Flow<List<OrderWithItems>> = dao.getPendingVerificationOrders()
  val walletTransactions: Flow<List<WalletTransactionEntity>> = dao.getWalletTransactions()
  val coupons: Flow<List<CouponEntity>> = dao.getActiveCoupons()
  val banners: Flow<List<BannerEntity>> = dao.getAllBanners()
  val currentUser: Flow<UserEntity?> = dao.getUser()

  suspend fun ensureDataSeeded() = withContext(Dispatchers.IO) {
    val count = dao.getProductCount()
    if (count == 0) {
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
    }
  }

  fun getProductsByCategory(categoryId: String): Flow<List<ProductEntity>> =
    dao.getProductsByCategory(categoryId)

  fun searchProducts(query: String): Flow<List<ProductEntity>> =
    dao.searchProducts(query)

  fun getOrderWithItems(orderId: Long): Flow<OrderWithItems?> =
    dao.getOrderWithItems(orderId)

  // Cart operations
  suspend fun addToCart(productId: String) = withContext(Dispatchers.IO) {
    val existing = dao.getCartItemByProductId(productId)
    if (existing == null) {
      dao.insertCartItem(CartItemEntity(productId = productId, quantity = 1))
    } else {
      dao.updateCartItem(existing.copy(quantity = existing.quantity + 1))
    }
  }

  suspend fun decreaseCartQuantity(productId: String) = withContext(Dispatchers.IO) {
    val existing = dao.getCartItemByProductId(productId) ?: return@withContext
    if (existing.quantity <= 1) {
      dao.deleteCartItemByProductId(productId)
    } else {
      dao.updateCartItem(existing.copy(quantity = existing.quantity - 1))
    }
  }

  suspend fun removeFromCart(productId: String) = withContext(Dispatchers.IO) {
    dao.deleteCartItemByProductId(productId)
  }

  suspend fun clearCart() = withContext(Dispatchers.IO) {
    dao.clearCart()
  }

  // Address
  suspend fun addAddress(address: AddressEntity): Long = withContext(Dispatchers.IO) {
    if (address.isDefault) {
      dao.resetDefaultAddresses()
    }
    dao.insertAddress(address)
  }

  suspend fun setDefaultAddress(addressId: Long, currentAddress: AddressEntity) = withContext(Dispatchers.IO) {
    dao.resetDefaultAddresses()
    dao.updateAddress(currentAddress.copy(id = addressId, isDefault = true))
  }

  suspend fun deleteAddress(addressId: Long) = withContext(Dispatchers.IO) {
    dao.deleteAddress(addressId)
  }

  // Place Order
  suspend fun placeOrder(
    items: List<CartItemWithProduct>,
    address: AddressEntity,
    paymentMethod: PaymentMethod,
    upiRefNumber: String,
    paymentProofNote: String,
    appliedCoupon: CouponEntity?,
    useWalletBalance: Boolean
  ): Long = withContext(Dispatchers.IO) {
    val subtotal = items.sumOf { it.product.price * it.cartItem.quantity }
    val zone = dao.getDeliveryZone(address.area)
    val standardDeliveryFee = zone?.deliveryFee ?: 25.0
    val freeDeliveryThreshold = zone?.minOrderFreeDelivery ?: 199.0
    val deliveryFee = if (subtotal >= freeDeliveryThreshold) 0.0 else standardDeliveryFee

    var couponDiscount = 0.0
    if (appliedCoupon != null) {
      if (appliedCoupon.discountPercent > 0) {
        val calc = (subtotal * appliedCoupon.discountPercent) / 100.0
        couponDiscount = if (appliedCoupon.discountAmount > 0) minOf(calc, appliedCoupon.discountAmount) else calc
      } else {
        couponDiscount = appliedCoupon.discountAmount
      }
    }

    val amountAfterDiscount = (subtotal + deliveryFee - couponDiscount).coerceAtLeast(0.0)

    val user = dao.getUser().firstOrNull()
    var walletAmountUsed = 0.0
    if (useWalletBalance && user != null && user.walletBalance > 0) {
      walletAmountUsed = minOf(user.walletBalance, amountAfterDiscount)
      val newBalance = user.walletBalance - walletAmountUsed
      dao.updateWalletBalance(user.id, newBalance)
      dao.insertWalletTransaction(
        WalletTransactionEntity(
          amount = walletAmountUsed,
          isCredit = false,
          title = "Order Payment",
          description = "Debited for grocery order checkout",
          orderNumber = ""
        )
      )
    }

    val finalPayable = (amountAfterDiscount - walletAmountUsed).coerceAtLeast(0.0)

    // Calculate 2% cashback for prepaid UPI
    val cashbackEarned = if (paymentMethod == PaymentMethod.MANUAL_UPI) {
      ((finalPayable * 0.02) * 100).roundToInt() / 100.0
    } else 0.0

    val orderNum = "GTG-REW-${(1000..9999).random()}"

    val initialOrderStatus = when (paymentMethod) {
      PaymentMethod.COD -> OrderStatus.CONFIRMED
      PaymentMethod.MANUAL_UPI -> OrderStatus.PLACED // Awaiting Admin/Delivery partner manual verification
    }

    val initialPaymentStatus = when (paymentMethod) {
      PaymentMethod.COD -> PaymentStatus.COD_PENDING
      PaymentMethod.MANUAL_UPI -> PaymentStatus.PENDING_VERIFICATION
    }

    val addressText = "${address.recipientName} (${address.phone})\n${address.houseFlatNo}, ${address.street}, Near ${address.landmark}, ${address.area}, Rewa - ${address.pincode}"

    val orderEntity = OrderEntity(
      orderNumber = orderNum,
      customerName = address.recipientName,
      customerPhone = address.phone,
      deliveryAddressText = addressText,
      area = address.area,
      subtotal = subtotal,
      deliveryCharge = deliveryFee,
      discountAmount = couponDiscount,
      walletAmountUsed = walletAmountUsed,
      totalAmount = finalPayable,
      paymentMethod = paymentMethod,
      paymentStatus = initialPaymentStatus,
      upiRefNumber = upiRefNumber,
      paymentProofNote = paymentProofNote,
      orderStatus = initialOrderStatus,
      assignedDeliveryPartnerName = "Ramesh Sharma (Rewa Hub)",
      assignedDeliveryPartnerPhone = "+91 98261 44550",
      cashbackEarned = cashbackEarned
    )

    val orderId = dao.insertOrder(orderEntity)

    val orderItems = items.map {
      OrderItemEntity(
        orderId = orderId,
        productId = it.product.id,
        productName = it.product.name,
        packSize = it.product.packSize,
        unitPrice = it.product.price,
        quantity = it.cartItem.quantity,
        totalPrice = it.product.price * it.cartItem.quantity,
        imageUrl = it.product.imageUrl
      )
    }
    dao.insertOrderItems(orderItems)

    // Clear cart after placing order
    dao.clearCart()

    orderId
  }

  // Admin & Delivery Partner Actions
  suspend fun approveManualPayment(order: OrderEntity) = withContext(Dispatchers.IO) {
    dao.updatePaymentAndOrderStatus(
      orderId = order.id,
      paymentStatus = PaymentStatus.VERIFIED_APPROVED,
      orderStatus = OrderStatus.CONFIRMED
    )

    // Credit 2% cashback into user's wallet!
    if (order.cashbackEarned > 0) {
      val user = dao.getUser().firstOrNull()
      if (user != null) {
        val updatedBalance = user.walletBalance + order.cashbackEarned
        dao.updateWalletBalance(user.id, updatedBalance)
        dao.insertWalletTransaction(
          WalletTransactionEntity(
            amount = order.cashbackEarned,
            isCredit = true,
            title = "2% Prepaid UPI Cashback 🎉",
            description = "Approved cashback for Order #${order.orderNumber}",
            orderNumber = order.orderNumber
          )
        )
      }
    }
  }

  suspend fun rejectManualPayment(orderId: Long, reason: String) = withContext(Dispatchers.IO) {
    dao.cancelOrder(orderId, reason)
  }

  suspend fun updateOrderStatus(orderId: Long, status: OrderStatus) = withContext(Dispatchers.IO) {
    dao.updateOrderStatus(orderId, status)
  }

  suspend fun markOrderDelivered(order: OrderEntity, isCod: Boolean) = withContext(Dispatchers.IO) {
    val finalPaymentStatus = if (isCod) PaymentStatus.COD_COLLECTED else order.paymentStatus
    dao.updatePaymentAndOrderStatus(
      orderId = order.id,
      paymentStatus = finalPaymentStatus,
      orderStatus = OrderStatus.DELIVERED
    )
  }

  // Product management for Admin
  suspend fun addProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
    dao.insertProduct(product)
  }

  suspend fun updateProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
    dao.updateProduct(product)
  }

  suspend fun deleteProduct(productId: String) = withContext(Dispatchers.IO) {
    dao.deleteProduct(productId)
  }

  // Category management for Admin
  suspend fun addCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
    dao.insertCategory(category)
  }

  suspend fun updateCategory(category: CategoryEntity) = withContext(Dispatchers.IO) {
    dao.updateCategory(category)
  }

  suspend fun deleteCategory(categoryId: String) = withContext(Dispatchers.IO) {
    dao.deleteCategory(categoryId)
  }
}
