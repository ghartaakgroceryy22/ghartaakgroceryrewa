package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.GharTakDatabase
import com.example.data.models.*
import com.example.data.repository.GroceryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenDestination {
  object Home : ScreenDestination()
  object CategoryBrowse : ScreenDestination()
  object Search : ScreenDestination()
  object Cart : ScreenDestination()
  object Checkout : ScreenDestination()
  data class OrderConfirmation(val orderId: Long) : ScreenDestination()
  data class OrderTracking(val orderId: Long) : ScreenDestination()
  object OrdersHistory : ScreenDestination()
  object Wallet : ScreenDestination()
  object AdminDashboard : ScreenDestination()
  object DeliveryPartner : ScreenDestination()
}

class GroceryViewModel(application: Application) : AndroidViewModel(application) {

  private val database = GharTakDatabase.getDatabase(application, viewModelScope)
  val repository = GroceryRepository(database.groceryDao())

  // Navigation State
  private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Home)
  val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

  // Active Role Switcher (Customer, Delivery Partner, Admin)
  private val _activeRole = MutableStateFlow(UserRole.CUSTOMER)
  val activeRole: StateFlow<UserRole> = _activeRole.asStateFlow()

  // Selected Rewa Location
  private val _selectedLocation = MutableStateFlow("Civil Lines, Rewa")
  val selectedLocation: StateFlow<String> = _selectedLocation.asStateFlow()

  // Selected Category filter
  private val _selectedCategory = MutableStateFlow<CategoryEntity?>(null)
  val selectedCategory: StateFlow<CategoryEntity?> = _selectedCategory.asStateFlow()

  // Search Query
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  // Selected Product for quick preview
  private val _selectedProduct = MutableStateFlow<ProductEntity?>(null)
  val selectedProduct: StateFlow<ProductEntity?> = _selectedProduct.asStateFlow()

  // Applied Coupon in Cart
  private val _appliedCoupon = MutableStateFlow<CouponEntity?>(null)
  val appliedCoupon: StateFlow<CouponEntity?> = _appliedCoupon.asStateFlow()

  // Use Wallet Balance toggle
  private val _useWalletBalance = MutableStateFlow(false)
  val useWalletBalance: StateFlow<Boolean> = _useWalletBalance.asStateFlow()

  // Selected Address for Checkout
  private val _selectedAddress = MutableStateFlow<AddressEntity?>(null)
  val selectedAddress: StateFlow<AddressEntity?> = _selectedAddress.asStateFlow()

  // Manual UPI checkout fields
  private val _upiRefNumber = MutableStateFlow("")
  val upiRefNumber: StateFlow<String> = _upiRefNumber.asStateFlow()

  private val _upiProofNote = MutableStateFlow("")
  val upiProofNote: StateFlow<String> = _upiProofNote.asStateFlow()

  private val _selectedPaymentMethod = MutableStateFlow(PaymentMethod.MANUAL_UPI)
  val selectedPaymentMethod: StateFlow<PaymentMethod> = _selectedPaymentMethod.asStateFlow()

  // Notification message for snackbars
  private val _userMessage = MutableStateFlow<String?>(null)
  val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

  // Reactive Flows from DB
  val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val banners: StateFlow<List<BannerEntity>> = repository.banners
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val featuredProducts: StateFlow<List<ProductEntity>> = repository.featuredProducts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val dealsOfTheDay: StateFlow<List<ProductEntity>> = repository.dealsOfTheDay
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val popularProducts: StateFlow<List<ProductEntity>> = repository.popularProducts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val cartItems: StateFlow<List<CartItemWithProduct>> = repository.cartItems
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val cartCount: StateFlow<Int> = cartItems.map { list ->
    list.sumOf { it.cartItem.quantity }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

  val cartSubtotal: StateFlow<Double> = cartItems.map { list ->
    list.sumOf { it.product.price * it.cartItem.quantity }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

  val addresses: StateFlow<List<AddressEntity>> = repository.addresses
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val deliveryZones: StateFlow<List<DeliveryZoneEntity>> = repository.deliveryZones
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val allOrders: StateFlow<List<OrderWithItems>> = repository.allOrders
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val activeOrders: StateFlow<List<OrderWithItems>> = repository.activeOrders
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val pendingVerificationOrders: StateFlow<List<OrderWithItems>> = repository.pendingVerificationOrders
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val currentUser: StateFlow<UserEntity?> = repository.currentUser
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val walletTransactions: StateFlow<List<WalletTransactionEntity>> = repository.walletTransactions
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val coupons: StateFlow<List<CouponEntity>> = repository.coupons
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val lowStockProducts: StateFlow<List<ProductEntity>> = repository.lowStockProducts
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val categoryFilteredProducts: StateFlow<List<ProductEntity>> = _selectedCategory.flatMapLatest { cat ->
    if (cat == null) repository.allProducts
    else repository.getProductsByCategory(cat.id)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  @OptIn(ExperimentalCoroutinesApi::class)
  val searchResults: StateFlow<List<ProductEntity>> = _searchQuery.flatMapLatest { query ->
    if (query.isBlank()) repository.popularProducts
    else repository.searchProducts(query)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  init {
    viewModelScope.launch {
      repository.ensureDataSeeded()
      // Initialize selected address with default
      repository.defaultAddress.collect { defaultAddr ->
        if (_selectedAddress.value == null && defaultAddr != null) {
          _selectedAddress.value = defaultAddr
        }
      }
    }
  }

  // Navigation handlers
  fun navigateTo(dest: ScreenDestination) {
    _currentScreen.value = dest
  }

  fun switchRole(role: UserRole) {
    _activeRole.value = role
    when (role) {
      UserRole.CUSTOMER -> _currentScreen.value = ScreenDestination.Home
      UserRole.ADMIN -> _currentScreen.value = ScreenDestination.AdminDashboard
      UserRole.DELIVERY_PARTNER -> _currentScreen.value = ScreenDestination.DeliveryPartner
    }
  }

  fun selectLocation(location: String) {
    _selectedLocation.value = location
    _userMessage.value = "Delivery address set to: $location"
  }

  fun selectCategory(category: CategoryEntity?) {
    _selectedCategory.value = category
    _currentScreen.value = ScreenDestination.CategoryBrowse
  }

  fun updateSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun selectProduct(product: ProductEntity?) {
    _selectedProduct.value = product
  }

  // Cart actions
  fun addToCart(productId: String) {
    viewModelScope.launch {
      repository.addToCart(productId)
      _userMessage.value = "Item added to cart"
    }
  }

  fun decreaseCartQuantity(productId: String) {
    viewModelScope.launch {
      repository.decreaseCartQuantity(productId)
    }
  }

  fun removeFromCart(productId: String) {
    viewModelScope.launch {
      repository.removeFromCart(productId)
    }
  }

  fun clearCart() {
    viewModelScope.launch {
      repository.clearCart()
    }
  }

  fun applyCoupon(coupon: CouponEntity) {
    _appliedCoupon.value = coupon
    _userMessage.value = "Coupon '${coupon.code}' applied!"
  }

  fun removeCoupon() {
    _appliedCoupon.value = null
  }

  fun toggleUseWalletBalance(use: Boolean) {
    _useWalletBalance.value = use
  }

  fun selectPaymentMethod(method: PaymentMethod) {
    _selectedPaymentMethod.value = method
  }

  fun updateUpiDetails(refNumber: String, proofNote: String) {
    _upiRefNumber.value = refNumber
    _upiProofNote.value = proofNote
  }

  fun selectAddress(address: AddressEntity) {
    _selectedAddress.value = address
  }

  fun addNewAddress(
    recipientName: String,
    phone: String,
    house: String,
    street: String,
    area: String,
    landmark: String,
    pincode: String,
    type: String
  ) {
    viewModelScope.launch {
      val newAddr = AddressEntity(
        recipientName = recipientName,
        phone = phone,
        houseFlatNo = house,
        street = street,
        area = area,
        landmark = landmark,
        pincode = pincode,
        addressType = type,
        isDefault = true
      )
      val id = repository.addAddress(newAddr)
      _selectedAddress.value = newAddr.copy(id = id)
      _userMessage.value = "Address saved for $area, Rewa"
    }
  }

  fun placeOrder(onSuccess: (Long) -> Unit) {
    val items = cartItems.value
    if (items.isEmpty()) {
      _userMessage.value = "Cart is empty"
      return
    }
    val addr = selectedAddress.value
    if (addr == null) {
      _userMessage.value = "Please select or add a delivery address"
      return
    }

    if (selectedPaymentMethod.value == PaymentMethod.MANUAL_UPI && _upiRefNumber.value.isBlank()) {
      _userMessage.value = "Please enter UPI Reference / UTR Number"
      return
    }

    viewModelScope.launch {
      val orderId = repository.placeOrder(
        items = items,
        address = addr,
        paymentMethod = selectedPaymentMethod.value,
        upiRefNumber = _upiRefNumber.value,
        paymentProofNote = _upiProofNote.value,
        appliedCoupon = _appliedCoupon.value,
        useWalletBalance = _useWalletBalance.value
      )
      // Reset checkout temp states
      _appliedCoupon.value = null
      _useWalletBalance.value = false
      _upiRefNumber.value = ""
      _upiProofNote.value = ""
      onSuccess(orderId)
    }
  }

  // Admin & Delivery Partner Actions
  fun approveManualPayment(order: OrderEntity) {
    viewModelScope.launch {
      repository.approveManualPayment(order)
      _userMessage.value = "Payment approved for #${order.orderNumber}. 2% Cashback credited to user wallet!"
    }
  }

  fun rejectManualPayment(orderId: Long, reason: String) {
    viewModelScope.launch {
      repository.rejectManualPayment(orderId, reason)
      _userMessage.value = "Order rejected & marked cancelled"
    }
  }

  fun updateOrderStatus(orderId: Long, status: OrderStatus) {
    viewModelScope.launch {
      repository.updateOrderStatus(orderId, status)
      _userMessage.value = "Order status updated to ${status.label}"
    }
  }

  fun deliveryPartnerDeliverOrder(order: OrderEntity, isCod: Boolean) {
    viewModelScope.launch {
      repository.markOrderDelivered(order, isCod)
      _userMessage.value = "Order #${order.orderNumber} marked Delivered successfully!"
    }
  }

  // Admin Product Management
  fun saveProduct(product: ProductEntity, isNew: Boolean) {
    viewModelScope.launch {
      if (isNew) {
        repository.addProduct(product)
        _userMessage.value = "Product '${product.name}' added"
      } else {
        repository.updateProduct(product)
        _userMessage.value = "Product '${product.name}' updated"
      }
    }
  }

  fun deleteProduct(productId: String) {
    viewModelScope.launch {
      repository.deleteProduct(productId)
      _userMessage.value = "Product removed from catalog"
    }
  }

  fun clearUserMessage() {
    _userMessage.value = null
  }
}
