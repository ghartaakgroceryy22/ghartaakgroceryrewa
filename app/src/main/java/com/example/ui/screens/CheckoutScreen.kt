package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.*
import com.example.ui.components.PaymentConfirmationBottomSheet
import com.example.ui.theme.*
import com.example.viewmodel.GroceryViewModel
import com.example.viewmodel.ScreenDestination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
  viewModel: GroceryViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val addresses by viewModel.addresses.collectAsState()
  val selectedAddress by viewModel.selectedAddress.collectAsState()
  val cartItems by viewModel.cartItems.collectAsState()
  val subtotal by viewModel.cartSubtotal.collectAsState()
  val appliedCoupon by viewModel.appliedCoupon.collectAsState()
  val useWalletBalance by viewModel.useWalletBalance.collectAsState()
  val user by viewModel.currentUser.collectAsState()
  val selectedPaymentMethod by viewModel.selectedPaymentMethod.collectAsState()
  val deliveryZones by viewModel.deliveryZones.collectAsState()

  var showAddAddressDialog by remember { mutableStateOf(false) }
  var showPaymentSheet by remember { mutableStateOf(false) }
  var copiedUpiId by remember { mutableStateOf(false) }
  var expandedItemsSummary by remember { mutableStateOf(false) }

  // Manual UPI inputs
  var utrInput by remember { mutableStateOf("") }
  var proofNoteInput by remember { mutableStateOf("Paid via UPI App (GPay/PhonePe)") }

  // Delivery fee calculation
  val currentZone = deliveryZones.find { it.zoneName == (selectedAddress?.area ?: "Civil Lines") }
  val minFreeDelivery = currentZone?.minOrderFreeDelivery ?: 199.0
  val standardDeliveryFee = currentZone?.deliveryFee ?: 25.0
  val deliveryFee = if (subtotal >= minFreeDelivery || subtotal == 0.0) 0.0 else standardDeliveryFee

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
  val walletBalance = user?.walletBalance ?: 0.0
  val walletDeduction = if (useWalletBalance && walletBalance > 0) {
    minOf(walletBalance, totalAfterDiscount)
  } else 0.0

  val finalPayable = (totalAfterDiscount - walletDeduction).coerceAtLeast(0.0)
  val cashbackAmount = (finalPayable * 0.02)
  val totalSavings = (couponDiscount + walletDeduction + (if (deliveryFee == 0.0 && subtotal > 0) standardDeliveryFee else 0.0))

  // Infinite pulsing glow for Place Order CTA and Cashback badge
  val infiniteTransition = rememberInfiniteTransition(label = "CheckoutPulse")
  val ctaPulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.02f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "CtaPulseScale"
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Checkout",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = GroceryTextPrimary
              )
              Spacer(modifier = Modifier.width(8.dp))
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = GroceryGreenContainer
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = GroceryGreenDark,
                    modifier = Modifier.size(12.dp)
                  )
                  Spacer(modifier = Modifier.width(3.dp))
                  Text(
                    text = "100% SECURE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GroceryGreenDark
                  )
                }
              }
            }
            Text(
              text = "Rewa Dark Store • 15-20 Mins Delivery",
              fontSize = 11.sp,
              color = GroceryTextSecondary
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("checkout_back_btn")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    bottomBar = {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        border = BorderStroke(1.dp, GroceryCardBorder),
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "To Pay: ₹${finalPayable.toInt()}",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GroceryGreenDark
              )
              if (selectedPaymentMethod == PaymentMethod.MANUAL_UPI) {
                Text(
                  text = "🎉 +₹${cashbackAmount.toInt()} Cashback into Wallet!",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = GroceryAmber
                )
              } else {
                Text(
                  text = "Cash on Delivery",
                  fontSize = 11.sp,
                  color = GroceryTextSecondary
                )
              }
            }

            Button(
              onClick = {
                if (selectedPaymentMethod == PaymentMethod.MANUAL_UPI) {
                  showPaymentSheet = true
                } else {
                  viewModel.updateUpiDetails("", "Cash on Delivery")
                  viewModel.placeOrder { orderId ->
                    viewModel.navigateTo(ScreenDestination.OrderConfirmation(orderId))
                  }
                }
              },
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedPaymentMethod == PaymentMethod.MANUAL_UPI) GroceryGreenPrimary else GroceryGreenDark
              ),
              modifier = Modifier
                .height(50.dp)
                .scale(ctaPulseScale)
                .testTag("place_order_btn")
            ) {
              Icon(
                imageVector = if (selectedPaymentMethod == PaymentMethod.MANUAL_UPI) Icons.Default.QrCode2 else Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (selectedPaymentMethod == PaymentMethod.MANUAL_UPI) "Proceed to Pay →" else "Place COD Order",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 14.sp
              )
            }
          }
        }
      }
    },
    modifier = modifier
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(GroceryBackground),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Animated 3-Step Journey Timeline (Zepto Style)
      item {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surface,
          shadowElevation = 1.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            TimelineStep(number = "1", label = "Address", isCompleted = true, isActive = false)
            HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 6.dp), color = GroceryGreenPrimary)
            TimelineStep(number = "2", label = "Payment", isCompleted = false, isActive = true)
            HorizontalDivider(modifier = Modifier.weight(1f).padding(horizontal = 6.dp), color = GroceryCardBorder)
            TimelineStep(number = "3", label = "Delivery", isCompleted = false, isActive = false)
          }
        }
      }

      // 2. Delivery Address Card (Zepto / Blinkit Rich Location Pill)
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, GroceryGreenLight.copy(alpha = 0.5f)),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .background(GroceryGreenContainer, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.TwoWheeler,
                    contentDescription = null,
                    tint = GroceryGreenDark,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "DELIVERING IN 15-20 MINS",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.ExtraBold,
                      color = GroceryGreenDark,
                      letterSpacing = 0.5.sp
                    )
                  }
                  Text(
                    text = "To: ${selectedAddress?.recipientName ?: "Customer"} (${selectedAddress?.addressType ?: "Home"})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GroceryTextPrimary
                  )
                }
              }

              OutlinedButton(
                onClick = { showAddAddressDialog = true },
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, GroceryGreenPrimary),
                modifier = Modifier
                  .height(30.dp)
                  .testTag("add_new_address_btn")
              ) {
                Text("+ New", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = GroceryGreenPrimary)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Address Details Box
            selectedAddress?.let { addr ->
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = GroceryGreenContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(10.dp)) {
                  Text(
                    text = "${addr.houseFlatNo}, ${addr.street}, Near ${addr.landmark}",
                    fontSize = 12.sp,
                    color = GroceryTextPrimary,
                    fontWeight = FontWeight.Medium
                  )
                  Text(
                    text = "Area: ${addr.area}, Rewa - ${addr.pincode} • Phone: ${addr.phone}",
                    fontSize = 11.sp,
                    color = GroceryGreenDark,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }

            // Quick switcher if multiple addresses exist
            if (addresses.size > 1) {
              Spacer(modifier = Modifier.height(8.dp))
              Text("Switch Saved Rewa Address:", fontSize = 10.sp, color = GroceryTextSecondary, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(4.dp))
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(addresses) { addr ->
                  val isSelected = selectedAddress?.id == addr.id
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) GroceryGreenPrimary else Color(0xFFF1F5F9),
                    border = BorderStroke(1.dp, if (isSelected) GroceryGreenPrimary else GroceryCardBorder),
                    modifier = Modifier.clickable { viewModel.selectAddress(addr) }
                  ) {
                    Text(
                      text = "${addr.area} (${addr.addressType})",
                      fontSize = 11.sp,
                      fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                      color = if (isSelected) Color.White else GroceryTextPrimary,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 3. Ghar Tak Wallet Redemption Quick Switch (If wallet balance > 0)
      if (walletBalance > 0) {
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, GroceryAmber.copy(alpha = 0.4f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .background(GroceryAmberContainer, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Color(0xFFB45309),
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "Ghar Tak Wallet",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GroceryTextPrimary
                  )
                  Text(
                    text = "Balance: ₹${walletBalance.toInt()} • Save ₹${walletDeduction.toInt()} on this order",
                    fontSize = 11.sp,
                    color = if (useWalletBalance) GroceryGreenDark else GroceryTextSecondary,
                    fontWeight = if (useWalletBalance) FontWeight.Bold else FontWeight.Normal
                  )
                }
              }

              Switch(
                checked = useWalletBalance,
                onCheckedChange = { viewModel.toggleUseWalletBalance(it) },
                colors = SwitchDefaults.colors(
                  checkedThumbColor = Color.White,
                  checkedTrackColor = GroceryGreenPrimary
                ),
                modifier = Modifier.testTag("checkout_wallet_toggle")
              )
            }
          }
        }
      }

      // 4. Payment Method Section (Zepto / Blinkit Vibrant High-Contrast Cards)
      item {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Select Payment Method",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 15.sp,
              color = GroceryTextPrimary
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = GroceryAmberContainer
            ) {
              Text(
                text = "⚡ 2% CASHBACK ON UPI",
                color = Color(0xFF92400E),
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          // Option A: MANUAL UPI & QR (RECOMMENDED)
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (selectedPaymentMethod == PaymentMethod.MANUAL_UPI) Color(0xFFFBFDFA) else MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
              width = if (selectedPaymentMethod == PaymentMethod.MANUAL_UPI) 2.dp else 1.dp,
              color = if (selectedPaymentMethod == PaymentMethod.MANUAL_UPI) GroceryGreenPrimary else GroceryCardBorder
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (selectedPaymentMethod == PaymentMethod.MANUAL_UPI) 3.dp else 1.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.selectPaymentMethod(PaymentMethod.MANUAL_UPI) }
              .testTag("pay_method_upi")
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
              ) {
                RadioButton(
                  selected = selectedPaymentMethod == PaymentMethod.MANUAL_UPI,
                  onClick = { viewModel.selectPaymentMethod(PaymentMethod.MANUAL_UPI) },
                  colors = RadioButtonDefaults.colors(selectedColor = GroceryGreenPrimary)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = "UPI & QR Code",
                      fontWeight = FontWeight.ExtraBold,
                      fontSize = 15.sp,
                      color = GroceryTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                      shape = RoundedCornerShape(4.dp),
                      color = GroceryDiscountRed
                    ) {
                      Text(
                        text = "⭐ RECOMMENDED",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                      )
                    }
                  }
                  Text(
                    text = "GPay • PhonePe • Paytm • BHIM • Cred",
                    fontSize = 11.sp,
                    color = GroceryTextSecondary,
                    fontWeight = FontWeight.Medium
                  )
                }
              }

              // Sleek UPI Preview & Bottom Sheet Trigger
              AnimatedVisibility(
                visible = selectedPaymentMethod == PaymentMethod.MANUAL_UPI,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GroceryAmberContainer,
                    border = BorderStroke(1.dp, GroceryAmber.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Text("💰", fontSize = 16.sp)
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "Earn ₹${cashbackAmount.toInt()} Instant Cashback into your Ghar Tak Wallet!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF78350F)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  OutlinedButton(
                    onClick = { showPaymentSheet = true },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.5.dp, GroceryGreenPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GroceryGreenPrimary),
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("open_payment_sheet_btn")
                  ) {
                    Icon(
                      imageVector = Icons.Default.QrCodeScanner,
                      contentDescription = null,
                      modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "Open Payment Sheet & Upload Screenshot →",
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp
                    )
                  }
                }
              }
            }
          }

          // Option B: CASH ON DELIVERY (COD)
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (selectedPaymentMethod == PaymentMethod.COD) Color(0xFFFBFDFA) else MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
              width = if (selectedPaymentMethod == PaymentMethod.COD) 2.dp else 1.dp,
              color = if (selectedPaymentMethod == PaymentMethod.COD) GroceryGreenPrimary else GroceryCardBorder
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = if (selectedPaymentMethod == PaymentMethod.COD) 3.dp else 1.dp),
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.selectPaymentMethod(PaymentMethod.COD) }
              .testTag("pay_method_cod")
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = selectedPaymentMethod == PaymentMethod.COD,
                onClick = { viewModel.selectPaymentMethod(PaymentMethod.COD) },
                colors = RadioButtonDefaults.colors(selectedColor = GroceryGreenPrimary)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "Cash on Delivery (COD)",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 15.sp,
                  color = GroceryTextPrimary
                )
                Text(
                  text = "Pay cash or scan rider's QR code on arrival at your doorstep",
                  fontSize = 11.sp,
                  color = GroceryTextSecondary
                )
              }
            }
          }
        }
      }

      // 5. Expandable Items in Order Preview
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, GroceryCardBorder),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedItemsSummary = !expandedItemsSummary },
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.ShoppingBag,
                  contentDescription = null,
                  tint = GroceryGreenPrimary,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Items in Order (${cartItems.sumOf { it.cartItem.quantity }})",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
              }
              Icon(
                imageVector = if (expandedItemsSummary) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = GroceryTextSecondary
              )
            }

            AnimatedVisibility(visible = expandedItemsSummary) {
              Column(modifier = Modifier.padding(top = 10.dp)) {
                cartItems.forEach { item ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.weight(1f)
                    ) {
                      AsyncImage(
                        model = item.product.imageUrl,
                        contentDescription = item.product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                          .size(32.dp)
                          .clip(RoundedCornerShape(6.dp))
                          .background(Color(0xFFF1F5F9))
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Column {
                        Text(
                          text = item.product.name,
                          fontSize = 12.sp,
                          fontWeight = FontWeight.Medium,
                          maxLines = 1
                        )
                        Text(
                          text = "${item.cartItem.quantity} x ${item.product.packSize}",
                          fontSize = 10.sp,
                          color = GroceryTextSecondary
                        )
                      }
                    }

                    Text(
                      text = "₹${(item.product.price * item.cartItem.quantity).toInt()}",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 6. Itemized Bill Details & Total Savings Ribbon (Zepto/Blinkit Style)
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          border = BorderStroke(1.dp, GroceryCardBorder),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "Bill Summary",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 15.sp,
              color = GroceryTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))

            BillLine(title = "Item Total", value = "₹${subtotal.toInt()}")
            BillLine(
              title = "Delivery Fee",
              value = if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}",
              isHighlighted = deliveryFee == 0.0,
              originalValue = if (deliveryFee == 0.0 && subtotal > 0) "₹25" else null
            )
            BillLine(title = "Handling & Bag Charge", value = "FREE", isHighlighted = true)

            if (couponDiscount > 0) {
              BillLine(
                title = "Coupon Discount (${appliedCoupon?.code})",
                value = "-₹${couponDiscount.toInt()}",
                isHighlighted = true
              )
            }

            if (walletDeduction > 0) {
              BillLine(
                title = "Wallet Balance Applied",
                value = "-₹${walletDeduction.toInt()}",
                isHighlighted = true
              )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = GroceryCardBorder)

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Total Payable Amount",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = GroceryTextPrimary
              )
              Text(
                text = "₹${finalPayable.toInt()}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = GroceryGreenDark
              )
            }

            // Total Savings Celebration Ribbon
            if (totalSavings > 0) {
              Spacer(modifier = Modifier.height(12.dp))
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = GroceryGreenContainer,
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("🎉", fontSize = 14.sp)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Total Savings on this order: ₹${totalSavings.toInt()}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = GroceryGreenDark
                  )
                }
              }
            }
          }
        }
      }

      // 7. Cancellation Policy Note
      item {
        Text(
          text = "Orders are prepared within 5 minutes at Rewa dark store. Cancellation is permitted before item packing begins.",
          fontSize = 11.sp,
          color = GroceryTextMuted,
          textAlign = TextAlign.Center,
          modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
        )
      }
    }
  }

  // Add New Rewa Address Dialog
  if (showAddAddressDialog) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var house by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("Civil Lines") }
    var landmark by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("486001") }
    var type by remember { mutableStateOf("Home") }

    AlertDialog(
      onDismissRequest = { showAddAddressDialog = false },
      title = { Text("Add Delivery Address in Rewa", fontWeight = FontWeight.ExtraBold) },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Recipient Name *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("10-Digit Mobile Number *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = house,
            onValueChange = { house = it },
            label = { Text("House / Flat / Plot No. *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = street,
            onValueChange = { street = it },
            label = { Text("Street / Road Name *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          // Rewa Locality Selector
          Text("Rewa Locality / Ward *", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          var expandedArea by remember { mutableStateOf(false) }
          Box {
            OutlinedButton(
              onClick = { expandedArea = true },
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(area, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.weight(1f))
              Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(
              expanded = expandedArea,
              onDismissRequest = { expandedArea = false }
            ) {
              deliveryZones.forEach { zone ->
                DropdownMenuItem(
                  text = { Text("${zone.zoneName} (${zone.pincode})") },
                  onClick = {
                    area = zone.zoneName
                    pincode = zone.pincode
                    expandedArea = false
                  }
                )
              }
            }
          }

          OutlinedTextField(
            value = landmark,
            onValueChange = { landmark = it },
            label = { Text("Nearby Landmark") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = pincode,
            onValueChange = { pincode = it },
            label = { Text("Pincode *") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (name.isNotBlank() && phone.isNotBlank() && house.isNotBlank()) {
              viewModel.addNewAddress(name, phone, house, street, area, landmark, pincode, type)
              showAddAddressDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary)
        ) {
          Text("Save & Deliver Here", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddAddressDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  // Modern Payment Confirmation & Screenshot Upload Bottom Sheet
  if (showPaymentSheet) {
    PaymentConfirmationBottomSheet(
      amountPayable = finalPayable,
      cashbackAmount = cashbackAmount,
      onDismiss = { showPaymentSheet = false },
      onConfirmPayment = { utr, proofUri ->
        showPaymentSheet = false
        viewModel.updateUpiDetails(utr, proofUri)
        viewModel.placeOrder { orderId ->
          viewModel.navigateTo(ScreenDestination.OrderConfirmation(orderId))
        }
      }
    )
  }
}

@Composable
fun TimelineStep(
  number: String,
  label: String,
  isCompleted: Boolean,
  isActive: Boolean
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .background(
          color = when {
            isCompleted -> GroceryGreenPrimary
            isActive -> GroceryLime
            else -> GroceryCardBorder
          },
          shape = CircleShape
        ),
      contentAlignment = Alignment.Center
    ) {
      if (isCompleted) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(14.dp)
        )
      } else {
        Text(
          text = number,
          fontSize = 11.sp,
          fontWeight = FontWeight.ExtraBold,
          color = if (isActive) Color(0xFF78350F) else GroceryTextSecondary
        )
      }
    }
    Spacer(modifier = Modifier.width(6.dp))
    Text(
      text = label,
      fontSize = 12.sp,
      fontWeight = if (isActive || isCompleted) FontWeight.Bold else FontWeight.Normal,
      color = if (isActive || isCompleted) GroceryTextPrimary else GroceryTextSecondary
    )
  }
}

@Composable
fun BillLine(
  title: String,
  value: String,
  isHighlighted: Boolean = false,
  originalValue: String? = null
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      fontSize = 13.sp,
      color = if (isHighlighted) GroceryGreenDark else GroceryTextSecondary,
      fontWeight = if (isHighlighted) FontWeight.SemiBold else FontWeight.Normal
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
      if (originalValue != null) {
        Text(
          text = originalValue,
          fontSize = 12.sp,
          color = GroceryTextMuted,
          textDecoration = TextDecoration.LineThrough
        )
        Spacer(modifier = Modifier.width(6.dp))
      }
      Text(
        text = value,
        fontSize = 13.sp,
        fontWeight = if (isHighlighted) FontWeight.ExtraBold else FontWeight.Medium,
        color = if (isHighlighted) GroceryGreenPrimary else GroceryTextPrimary
      )
    }
  }
}
