package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.models.UserRole
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.GharTakGroceryTheme
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary
import com.example.viewmodel.GroceryViewModel
import com.example.viewmodel.ScreenDestination

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      GharTakGroceryTheme {
        GharTakGroceryApp()
      }
    }
  }
}

@Composable
fun GharTakGroceryApp(
  viewModel: GroceryViewModel = viewModel()
) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val activeRole by viewModel.activeRole.collectAsState()
  val selectedLocation by viewModel.selectedLocation.collectAsState()
  val cartCount by viewModel.cartCount.collectAsState()
  val cartSubtotal by viewModel.cartSubtotal.collectAsState()
  val cartItems by viewModel.cartItems.collectAsState()
  val user by viewModel.currentUser.collectAsState()
  val deliveryZones by viewModel.deliveryZones.collectAsState()
  val userMessage by viewModel.userMessage.collectAsState()
  val selectedProduct by viewModel.selectedProduct.collectAsState()

  var showLocationModal by remember { mutableStateOf(false) }
  var showRoleDialog by remember { mutableStateOf(false) }

  val snackbarHostState = remember { SnackbarHostState() }

  // Listen for user messages and show snackbars
  LaunchedEffect(userMessage) {
    userMessage?.let { msg ->
      snackbarHostState.showSnackbar(
        message = msg,
        duration = SnackbarDuration.Short
      )
      viewModel.clearUserMessage()
    }
  }

  // Handle Android Back Navigation
  BackHandler(enabled = currentScreen !is ScreenDestination.Home) {
    when (currentScreen) {
      is ScreenDestination.CategoryBrowse -> viewModel.navigateTo(ScreenDestination.Home)
      is ScreenDestination.Search -> viewModel.navigateTo(ScreenDestination.Home)
      is ScreenDestination.Cart -> viewModel.navigateTo(ScreenDestination.Home)
      is ScreenDestination.Checkout -> viewModel.navigateTo(ScreenDestination.Cart)
      is ScreenDestination.OrderConfirmation -> viewModel.navigateTo(ScreenDestination.Home)
      is ScreenDestination.OrderTracking -> viewModel.navigateTo(ScreenDestination.OrdersHistory)
      is ScreenDestination.OrdersHistory -> viewModel.navigateTo(ScreenDestination.Home)
      is ScreenDestination.Wallet -> viewModel.navigateTo(ScreenDestination.Home)
      is ScreenDestination.AdminDashboard -> viewModel.switchRole(UserRole.CUSTOMER)
      is ScreenDestination.DeliveryPartner -> viewModel.switchRole(UserRole.CUSTOMER)
      else -> viewModel.navigateTo(ScreenDestination.Home)
    }
  }

  val cartMap = remember(cartItems) {
    cartItems.associate { it.cartItem.productId to it.cartItem.quantity }
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    contentWindowInsets = WindowInsets.statusBars,
    topBar = {
      // Show location header on Customer browsing screens
      if (activeRole == UserRole.CUSTOMER &&
        (currentScreen is ScreenDestination.Home ||
            currentScreen is ScreenDestination.CategoryBrowse ||
            currentScreen is ScreenDestination.OrdersHistory)
      ) {
        RewaLocationHeader(
          currentLocation = selectedLocation,
          currentRole = activeRole,
          onLocationClick = { showLocationModal = true },
          onRoleClick = { showRoleDialog = true },
          onWalletClick = { viewModel.navigateTo(ScreenDestination.Wallet) },
          walletBalance = user?.walletBalance ?: 0.0
        )
      }
    },
    bottomBar = {
      // Bottom Navigation Bar only shown for customer flow
      if (activeRole == UserRole.CUSTOMER &&
        (currentScreen is ScreenDestination.Home ||
            currentScreen is ScreenDestination.CategoryBrowse ||
            currentScreen is ScreenDestination.OrdersHistory)
      ) {
        Column(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) {
          // Floating Cart Bar above navigation if items in cart and on Home / Categories
          if (cartCount > 0 && (currentScreen is ScreenDestination.Home || currentScreen is ScreenDestination.CategoryBrowse)) {
            FloatingCartBar(
              itemCount = cartCount,
              totalPrice = cartSubtotal,
              onViewCartClick = { viewModel.navigateTo(ScreenDestination.Cart) }
            )
          }

          NavigationBar(
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
          ) {
            NavigationBarItem(
              selected = currentScreen is ScreenDestination.Home,
              onClick = { viewModel.navigateTo(ScreenDestination.Home) },
              icon = {
                Icon(
                  if (currentScreen is ScreenDestination.Home) Icons.Filled.Home else Icons.Outlined.Home,
                  contentDescription = "Home"
                )
              },
              label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GroceryGreenPrimary,
                selectedTextColor = GroceryGreenDark,
                indicatorColor = Color(0xFFD1FAE5)
              ),
              modifier = Modifier.testTag("nav_home")
            )

            NavigationBarItem(
              selected = currentScreen is ScreenDestination.CategoryBrowse,
              onClick = {
                viewModel.selectCategory(null)
                viewModel.navigateTo(ScreenDestination.CategoryBrowse)
              },
              icon = {
                Icon(
                  if (currentScreen is ScreenDestination.CategoryBrowse) Icons.Filled.Category else Icons.Outlined.Category,
                  contentDescription = "Categories"
                )
              },
              label = { Text("Categories", fontSize = 11.sp) },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GroceryGreenPrimary,
                selectedTextColor = GroceryGreenDark,
                indicatorColor = Color(0xFFD1FAE5)
              ),
              modifier = Modifier.testTag("nav_categories")
            )

            NavigationBarItem(
              selected = currentScreen is ScreenDestination.Search,
              onClick = { viewModel.navigateTo(ScreenDestination.Search) },
              icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
              label = { Text("Search", fontSize = 11.sp) },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GroceryGreenPrimary,
                selectedTextColor = GroceryGreenDark,
                indicatorColor = Color(0xFFD1FAE5)
              ),
              modifier = Modifier.testTag("nav_search")
            )

            NavigationBarItem(
              selected = currentScreen is ScreenDestination.Cart,
              onClick = { viewModel.navigateTo(ScreenDestination.Cart) },
              icon = {
                BadgedBox(
                  badge = {
                    if (cartCount > 0) {
                      Badge(containerColor = GroceryGreenPrimary) {
                        Text("$cartCount", color = Color.White)
                      }
                    }
                  }
                ) {
                  Icon(
                    if (currentScreen is ScreenDestination.Cart) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                    contentDescription = "Cart"
                  )
                }
              },
              label = { Text("Cart", fontSize = 11.sp) },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GroceryGreenPrimary,
                selectedTextColor = GroceryGreenDark,
                indicatorColor = Color(0xFFD1FAE5)
              ),
              modifier = Modifier.testTag("nav_cart")
            )

            NavigationBarItem(
              selected = currentScreen is ScreenDestination.OrdersHistory,
              onClick = { viewModel.navigateTo(ScreenDestination.OrdersHistory) },
              icon = {
                Icon(
                  if (currentScreen is ScreenDestination.OrdersHistory) Icons.Filled.ReceiptLong else Icons.Outlined.ReceiptLong,
                  contentDescription = "Orders"
                )
              },
              label = { Text("Orders", fontSize = 11.sp) },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GroceryGreenPrimary,
                selectedTextColor = GroceryGreenDark,
                indicatorColor = Color(0xFFD1FAE5)
              ),
              modifier = Modifier.testTag("nav_orders")
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
          val isForward = getScreenOrder(targetState) >= getScreenOrder(initialState)
          val enterAnimation = slideInHorizontally(
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioLowBouncy,
              stiffness = Spring.StiffnessMediumLow
            ),
            initialOffsetX = { fullWidth -> if (isForward) (fullWidth * 0.35f).toInt() else (-fullWidth * 0.35f).toInt() }
          ) + fadeIn(
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
          ) + scaleIn(
            initialScale = 0.94f,
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioMediumBouncy,
              stiffness = Spring.StiffnessMediumLow
            )
          )

          val exitAnimation = slideOutHorizontally(
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioNoBouncy,
              stiffness = Spring.StiffnessMedium
            ),
            targetOffsetX = { fullWidth -> if (isForward) (-fullWidth * 0.25f).toInt() else (fullWidth * 0.25f).toInt() }
          ) + fadeOut(
            animationSpec = spring(stiffness = Spring.StiffnessMedium)
          ) + scaleOut(
            targetScale = 0.96f,
            animationSpec = spring(
              dampingRatio = Spring.DampingRatioNoBouncy,
              stiffness = Spring.StiffnessMedium
            )
          )

          enterAnimation togetherWith exitAnimation
        },
        label = "SpringScreenTransition"
      ) { screen ->
        when (screen) {
          is ScreenDestination.Home -> {
            HomeScreen(
              viewModel = viewModel,
              onNavigateToCart = { viewModel.navigateTo(ScreenDestination.Cart) }
            )
          }

          is ScreenDestination.CategoryBrowse -> {
            CategoryBrowseScreen(
              viewModel = viewModel,
              onBack = { viewModel.navigateTo(ScreenDestination.Home) }
            )
          }

          is ScreenDestination.Search -> {
            SearchScreen(
              viewModel = viewModel,
              onBack = { viewModel.navigateTo(ScreenDestination.Home) }
            )
          }

          is ScreenDestination.Cart -> {
            CartScreen(
              viewModel = viewModel,
              onBack = { viewModel.navigateTo(ScreenDestination.Home) },
              onProceedToCheckout = { viewModel.navigateTo(ScreenDestination.Checkout) }
            )
          }

          is ScreenDestination.Checkout -> {
            CheckoutScreen(
              viewModel = viewModel,
              onBack = { viewModel.navigateTo(ScreenDestination.Cart) }
            )
          }

          is ScreenDestination.OrderConfirmation -> {
            OrderConfirmationScreen(
              orderId = screen.orderId,
              viewModel = viewModel,
              onTrackOrder = { viewModel.navigateTo(ScreenDestination.OrderTracking(screen.orderId)) },
              onGoHome = { viewModel.navigateTo(ScreenDestination.Home) }
            )
          }

          is ScreenDestination.OrderTracking -> {
            OrderTrackingScreen(
              orderId = screen.orderId,
              viewModel = viewModel,
              onBack = { viewModel.navigateTo(ScreenDestination.OrdersHistory) }
            )
          }

          is ScreenDestination.OrdersHistory -> {
            OrdersHistoryScreen(
              viewModel = viewModel,
              onBack = { viewModel.navigateTo(ScreenDestination.Home) },
              onTrackOrder = { id -> viewModel.navigateTo(ScreenDestination.OrderTracking(id)) }
            )
          }

          is ScreenDestination.Wallet -> {
            WalletScreen(
              viewModel = viewModel,
              onBack = { viewModel.navigateTo(ScreenDestination.Home) }
            )
          }

          is ScreenDestination.AdminDashboard -> {
            AdminDashboardScreen(
              viewModel = viewModel,
              onBack = { viewModel.switchRole(UserRole.CUSTOMER) }
            )
          }

          is ScreenDestination.DeliveryPartner -> {
            DeliveryPartnerScreen(
              viewModel = viewModel,
              onBack = { viewModel.switchRole(UserRole.CUSTOMER) }
            )
          }
        }
      }

      // Floating Quick Cart Bar (Zepto / Blinkit style on browse screens)
      if (cartCount > 0 && (currentScreen is ScreenDestination.Home || currentScreen is ScreenDestination.CategoryBrowse || currentScreen is ScreenDestination.Search)) {
        FloatingCartBar(
          itemCount = cartCount,
          totalPrice = cartSubtotal,
          onViewCartClick = { viewModel.navigateTo(ScreenDestination.Cart) },
          modifier = Modifier
            .align(androidx.compose.ui.Alignment.BottomCenter)
            .padding(bottom = 6.dp)
        )
      }
    }
  }

  // Rewa Delivery Location Selector Modal
  if (showLocationModal) {
    RewaLocationBottomSheet(
      zones = deliveryZones,
      currentLocation = selectedLocation,
      onSelectZone = { loc -> viewModel.selectLocation(loc) },
      onDismiss = { showLocationModal = false }
    )
  }

  // Role Switcher Dialog
  if (showRoleDialog) {
    RoleSwitcherDialog(
      currentRole = activeRole,
      onSelectRole = { role -> viewModel.switchRole(role) },
      onDismiss = { showRoleDialog = false }
    )
  }

  // Product Detail Bottom Sheet Modal
  selectedProduct?.let { product ->
    ProductDetailBottomSheet(
      product = product,
      quantityInCart = cartMap[product.id] ?: 0,
      onAddToCart = { viewModel.addToCart(product.id) },
      onIncreaseQuantity = { viewModel.addToCart(product.id) },
      onDecreaseQuantity = { viewModel.decreaseCartQuantity(product.id) },
      onDismiss = { viewModel.selectProduct(null) }
    )
  }
}

private fun getScreenOrder(screen: ScreenDestination): Int {
  return when (screen) {
    is ScreenDestination.Home -> 0
    is ScreenDestination.CategoryBrowse -> 1
    is ScreenDestination.Search -> 2
    is ScreenDestination.Cart -> 3
    is ScreenDestination.Checkout -> 4
    is ScreenDestination.OrderConfirmation -> 5
    is ScreenDestination.OrderTracking -> 6
    is ScreenDestination.OrdersHistory -> 7
    is ScreenDestination.Wallet -> 8
    is ScreenDestination.AdminDashboard -> 9
    is ScreenDestination.DeliveryPartner -> 10
  }
}

