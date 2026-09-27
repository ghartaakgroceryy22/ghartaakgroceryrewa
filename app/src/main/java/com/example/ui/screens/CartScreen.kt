package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.CartItemWithProduct
import com.example.data.models.CouponEntity
import com.example.ui.theme.*
import com.example.viewmodel.GroceryViewModel
import com.example.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
  viewModel: GroceryViewModel,
  onBack: () -> Unit,
  onProceedToCheckout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val cartItems by viewModel.cartItems.collectAsState()
  val subtotal by viewModel.cartSubtotal.collectAsState()
  val appliedCoupon by viewModel.appliedCoupon.collectAsState()
  val useWalletBalance by viewModel.useWalletBalance.collectAsState()
  val user by viewModel.currentUser.collectAsState()
  val coupons by viewModel.coupons.collectAsState()
  val selectedAddress by viewModel.selectedAddress.collectAsState()
  val deliveryZones by viewModel.deliveryZones.collectAsState()

  // Delivery fee calculation
  val currentZone = deliveryZones.find { it.zoneName == (selectedAddress?.area ?: "Civil Lines") }
  val minFreeDelivery = currentZone?.minOrderFreeDelivery ?: 199.0
  val standardDeliveryFee = currentZone?.deliveryFee ?: 25.0
  val deliveryFee = if (subtotal >= minFreeDelivery || subtotal == 0.0) 0.0 else standardDeliveryFee

  // Coupon discount calculation
  var couponDiscount = 0.0
  if (appliedCoupon != null) {
    if (appliedCoupon!!.discountPercent > 0) {
      val calc = (subtotal * appliedCoupon!!.discountPercent) / 100.0
      couponDiscount = if (appliedCoupon!!.discountAmount > 0) minOf(calc, appliedCoupon!!.discountAmount) else calc
    } else {
      couponDiscount = appliedCoupon!!.discountAmount
    }
  }

  val totalAfterDiscount = (subtotal + deliveryFee - couponDiscount).coerceAtLeast(0.0)

  // Wallet deduction calculation
  val walletBalance = user?.walletBalance ?: 0.0
  val walletDeduction = if (useWalletBalance && walletBalance > 0) {
    minOf(walletBalance, totalAfterDiscount)
  } else 0.0

  val finalPayable = (totalAfterDiscount - walletDeduction).coerceAtLeast(0.0)
  val potentialCashback = (finalPayable * 0.02)

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("My Cart", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(
              "${cartItems.sumOf { it.cartItem.quantity }} items • Rewa Delivery",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("cart_back_btn")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          if (cartItems.isNotEmpty()) {
            TextButton(
              onClick = { viewModel.clearCart() },
              modifier = Modifier.testTag("clear_cart_btn")
            ) {
              Text("Clear", color = GroceryDiscountRed, fontSize = 13.sp)
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    bottomBar = {
      if (cartItems.isNotEmpty()) {
        Surface(
          color = MaterialTheme.colorScheme.surface,
          shadowElevation = 10.dp,
          modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "₹${finalPayable.toInt()}",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GroceryGreenDark
              )
              Text(
                text = "Earn ₹${potentialCashback.toInt()} on UPI Pay",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GroceryAmber
              )
            }

            Button(
              onClick = onProceedToCheckout,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
              modifier = Modifier
                .height(48.dp)
                .testTag("proceed_checkout_btn")
            ) {
              Text("Proceed to Checkout", fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
            }
          }
        }
      }
    },
    modifier = modifier
  ) { padding ->
    if (cartItems.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .background(GroceryBackground),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "🛒", fontSize = 54.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Your Cart is Empty",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = GroceryTextPrimary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Explore fresh vegetables, dairy and snacks in Rewa",
            fontSize = 13.sp,
            color = GroceryTextSecondary
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onBack,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary)
          ) {
            Text("Start Shopping")
          }
        }
      }
    } else {
      val freeDeliveryProgress = (subtotal / minFreeDelivery).coerceIn(0.0, 1.0).toFloat()
      val animatedProgress by animateFloatAsState(
        targetValue = freeDeliveryProgress,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "FreeDeliveryProgress"
      )

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .background(GroceryBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Free delivery milestone progress bar (Zepto Style)
        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (subtotal >= minFreeDelivery) GroceryGreenContainer else Color(0xFFFFFBEB),
            border = BorderStroke(1.dp, if (subtotal >= minFreeDelivery) GroceryGreenLight else Color(0xFFFDE68A)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (subtotal >= minFreeDelivery) Icons.Default.CheckCircle else Icons.Default.LocalShipping,
                  contentDescription = null,
                  tint = if (subtotal >= minFreeDelivery) GroceryGreenDark else Color(0xFF92400E),
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = if (subtotal >= minFreeDelivery)
                    "🎉 FREE Doorstep Delivery Unlocked across Rewa!"
                  else
                    "Add ₹${(minFreeDelivery - subtotal).toInt()} more to get FREE Delivery!",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = if (subtotal >= minFreeDelivery) GroceryGreenDark else Color(0xFF92400E)
                )
              }

              if (subtotal < minFreeDelivery) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                  progress = { animatedProgress },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                  color = GroceryGreenPrimary,
                  trackColor = Color(0xFFFDE68A)
                )
              }
            }
          }
        }

        // Cart items card
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "Order Items (${cartItems.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = GroceryTextPrimary
              )
              Spacer(modifier = Modifier.height(10.dp))

              cartItems.forEachIndexed { index, itemWithProduct ->
                CartItemRow(
                  item = itemWithProduct,
                  onIncrease = { viewModel.addToCart(itemWithProduct.product.id) },
                  onDecrease = { viewModel.decreaseCartQuantity(itemWithProduct.product.id) },
                  onRemove = { viewModel.removeFromCart(itemWithProduct.product.id) }
                )
                if (index < cartItems.size - 1) {
                  HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = GroceryCardBorder
                  )
                }
              }
            }
          }
        }

        // Coupons Section
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.ConfirmationNumber,
                    contentDescription = null,
                    tint = GroceryAmber,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Apply Coupon Code",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                }
                if (appliedCoupon != null) {
                  TextButton(onClick = { viewModel.removeCoupon() }) {
                    Text("Remove", color = GroceryDiscountRed, fontSize = 12.sp)
                  }
                }
              }

              if (appliedCoupon != null) {
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = GroceryGreenContainer,
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                ) {
                  Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = GroceryGreenDark,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "'${appliedCoupon!!.code}' applied! Saved ₹${couponDiscount.toInt()}",
                      fontWeight = FontWeight.Bold,
                      color = GroceryGreenDark,
                      fontSize = 12.sp
                    )
                  }
                }
              } else {
                Spacer(modifier = Modifier.height(8.dp))
                coupons.forEach { cp ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = cp.code,
                        fontWeight = FontWeight.ExtraBold,
                        color = GroceryGreenPrimary,
                        fontSize = 13.sp
                      )
                      Text(
                        text = cp.description,
                        fontSize = 11.sp,
                        color = GroceryTextSecondary
                      )
                    }
                    Button(
                      onClick = { viewModel.applyCoupon(cp) },
                      shape = RoundedCornerShape(6.dp),
                      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                      colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenContainer, contentColor = GroceryGreenDark),
                      modifier = Modifier.height(28.dp)
                    ) {
                      Text("APPLY", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                  }
                }
              }
            }
          }
        }

        // Wallet Balance usage card
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .background(GroceryAmber.copy(alpha = 0.15f), CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = GroceryAmber,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "Ghar Tak Wallet Balance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                  Text(
                    text = "Available: ₹${walletBalance.toInt()} (${if (useWalletBalance) "Using ₹${walletDeduction.toInt()}" else "Tap switch to use"})",
                    fontSize = 11.sp,
                    color = GroceryTextSecondary
                  )
                }
              }

              Switch(
                checked = useWalletBalance && walletBalance > 0,
                onCheckedChange = { viewModel.toggleUseWalletBalance(it) },
                enabled = walletBalance > 0,
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = GroceryGreenPrimary
                ),
                modifier = Modifier.testTag("use_wallet_toggle")
              )
            }
          }
        }

        // 2% Cashback Notice
        item {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFEF9C3),
            border = BorderStroke(1.dp, Color(0xFFFDE047)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.CurrencyRupee,
                contentDescription = null,
                tint = Color(0xFF854D0E),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "2% Prepaid UPI Cashback Guarantee",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = Color(0xFF854D0E)
                )
                Text(
                  text = "Prepay via UPI & QR at checkout and get ₹${potentialCashback.toInt()} credited back into your wallet!",
                  fontSize = 11.sp,
                  color = Color(0xFF713F12)
                )
              }
            }
          }
        }

        // Bill Summary Card
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "Bill Details",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = GroceryTextPrimary
              )
              Spacer(modifier = Modifier.height(10.dp))

              BillRow(title = "Item Total", value = "₹${subtotal.toInt()}")
              BillRow(
                title = "Delivery Charge",
                value = if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}",
                isHighlighted = deliveryFee == 0.0
              )
              BillRow(title = "Handling & Packing", value = "FREE", isHighlighted = true)

              if (couponDiscount > 0) {
                BillRow(
                  title = "Coupon Savings (${appliedCoupon?.code})",
                  value = "-₹${couponDiscount.toInt()}",
                  isHighlighted = true
                )
              }

              if (walletDeduction > 0) {
                BillRow(
                  title = "Wallet Balance Applied",
                  value = "-₹${walletDeduction.toInt()}",
                  isHighlighted = true
                )
              }

              HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = GroceryCardBorder
              )

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "To Pay",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 16.sp,
                  color = GroceryTextPrimary
                )
                Text(
                  text = "₹${finalPayable.toInt()}",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 18.sp,
                  color = GroceryGreenDark
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun CartItemRow(
  item: CartItemWithProduct,
  onIncrease: () -> Unit,
  onDecrease: () -> Unit,
  onRemove: () -> Unit
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Image
    AsyncImage(
      model = item.product.imageUrl,
      contentDescription = item.product.name,
      contentScale = ContentScale.Crop,
      modifier = Modifier
        .size(54.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFFF1F5F9))
    )

    Spacer(modifier = Modifier.width(10.dp))

    // Details
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = item.product.name,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        maxLines = 1,
        color = GroceryTextPrimary
      )
      Text(
        text = item.product.packSize,
        fontSize = 11.sp,
        color = GroceryTextSecondary
      )
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "₹${(item.product.price * item.cartItem.quantity).toInt()}",
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          color = GroceryGreenDark
        )
        if (item.product.mrp > item.product.price) {
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "₹${(item.product.mrp * item.cartItem.quantity).toInt()}",
            fontSize = 11.sp,
            color = GroceryTextMuted,
            textDecoration = TextDecoration.LineThrough
          )
        }
      }
    }

    // Stepper
    Surface(
      shape = RoundedCornerShape(8.dp),
      color = GroceryGreenContainer,
      modifier = Modifier.height(30.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 2.dp)
      ) {
        IconButton(
          onClick = onDecrease,
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = if (item.cartItem.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
            contentDescription = "Decrease",
            tint = GroceryGreenDark,
            modifier = Modifier.size(13.dp)
          )
        }

        Text(
          text = "${item.cartItem.quantity}",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = GroceryGreenDark,
          modifier = Modifier.padding(horizontal = 4.dp)
        )

        IconButton(
          onClick = onIncrease,
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Increase",
            tint = GroceryGreenDark,
            modifier = Modifier.size(13.dp)
          )
        }
      }
    }
  }
}

@Composable
fun BillRow(
  title: String,
  value: String,
  isHighlighted: Boolean = false
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = title,
      fontSize = 13.sp,
      color = if (isHighlighted) GroceryGreenDark else GroceryTextSecondary,
      fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Normal
    )
    Text(
      text = value,
      fontSize = 13.sp,
      fontWeight = FontWeight.SemiBold,
      color = if (isHighlighted) GroceryGreenDark else GroceryTextPrimary
    )
  }
}
