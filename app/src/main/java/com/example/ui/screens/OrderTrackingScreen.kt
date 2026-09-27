package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.*
import com.example.ui.theme.*
import com.example.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
  orderId: Long,
  viewModel: GroceryViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val allOrders by viewModel.allOrders.collectAsState()
  val orderWithItems = allOrders.find { it.order.id == orderId }
  val order = orderWithItems?.order
  val items = orderWithItems?.items ?: emptyList()

  // Bike animation along road
  val infiniteTransition = rememberInfiniteTransition(label = "bike")
  val bikeOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(2200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "bikeOffset"
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "Track Order #${order?.orderNumber ?: ""}",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Text(
              text = "Estimated Time: ${order?.estimatedDeliveryTime ?: "15-25 Mins"}",
              fontSize = 11.sp,
              color = GroceryGreenDark,
              fontWeight = FontWeight.SemiBold
            )
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("tracking_back_btn")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    modifier = modifier
  ) { padding ->
    if (order == null) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator(color = GroceryGreenPrimary)
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .background(GroceryBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // 1. Payment Verification Status Banner (Requested Feature)
        item {
          if (order.paymentMethod == PaymentMethod.MANUAL_UPI) {
            when (order.paymentStatus) {
              PaymentStatus.PENDING_VERIFICATION -> {
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = Color(0xFFFFFBEB),
                  border = BorderStroke(1.dp, GroceryAmber),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      modifier = Modifier
                        .size(36.dp)
                        .background(GroceryAmber, CircleShape),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = "UPI Payment Verification Pending ⏳",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF78350F)
                      )
                      Text(
                        text = "UTR: ${order.upiRefNumber.ifEmpty { "Proof submitted" }}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GroceryTextPrimary
                      )
                      Text(
                        text = "Admin or Rider is reviewing your payment proof. 2% cashback will be credited immediately upon confirmation!",
                        fontSize = 11.sp,
                        color = Color(0xFF92400E)
                      )
                    }
                  }
                }
              }

              PaymentStatus.VERIFIED_APPROVED -> {
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = GroceryGreenContainer,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GroceryGreenDark, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(
                        text = "UPI Payment Approved ✓",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GroceryGreenDark
                      )
                      Text(
                        text = "₹${order.cashbackEarned.toInt()} Cashback credited to your Ghar Tak Wallet!",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF047857)
                      )
                    }
                  }
                }
              }

              PaymentStatus.REJECTED -> {
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = GroceryDiscountPink,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(Icons.Default.Cancel, contentDescription = null, tint = GroceryDiscountRed, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Text(
                        text = "Payment Not Received / Cancelled ❌",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GroceryDiscountRed
                      )
                      if (order.cancellationReason.isNotBlank()) {
                        Text(
                          text = "Reason: ${order.cancellationReason}",
                          fontSize = 11.sp,
                          color = GroceryDiscountRed
                        )
                      }
                    }
                  }
                }
              }

              else -> {}
            }
          }
        }

        // 2. Animated Courier Rider in Rewa
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Live Delivery Simulation",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
                Surface(
                  shape = RoundedCornerShape(4.dp),
                  color = GroceryGreenContainer
                ) {
                  Text(
                    text = order.orderStatus.label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GroceryGreenDark,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(14.dp))

              // Moving road track
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(44.dp)
                  .background(Color(0xFFF1F5F9), RoundedCornerShape(22.dp))
                  .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
              ) {
                // Dashed line
                HorizontalDivider(color = Color(0xFFCBD5E1), thickness = 2.dp)

                // Start icon: Store
                Icon(
                  imageVector = Icons.Default.Storefront,
                  contentDescription = "Rewa Hub",
                  tint = GroceryGreenDark,
                  modifier = Modifier
                    .align(Alignment.CenterStart)
                    .size(22.dp)
                )

                // End icon: Home
                Icon(
                  imageVector = Icons.Default.Home,
                  contentDescription = "Doorstep",
                  tint = GroceryAmber,
                  modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(22.dp)
                )

                // Animated Bike Position
                val positionPercent = if (order.orderStatus == OrderStatus.DELIVERED) 0.95f
                else if (order.orderStatus == OrderStatus.OUT_FOR_DELIVERY) (0.2f + 0.6f * bikeOffset)
                else 0.15f

                Box(
                  modifier = Modifier
                    .fillMaxWidth(positionPercent)
                    .align(Alignment.CenterStart),
                  contentAlignment = Alignment.CenterEnd
                ) {
                  Surface(
                    shape = CircleShape,
                    color = GroceryGreenPrimary,
                    shadowElevation = 3.dp,
                    modifier = Modifier.size(30.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.TwoWheeler,
                      contentDescription = "Rider",
                      tint = Color.White,
                      modifier = Modifier
                        .padding(5.dp)
                        .size(18.dp)
                    )
                  }
                }
              }
            }
          }
        }

        // 3. Step Timeline
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "Order Timeline",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Spacer(modifier = Modifier.height(12.dp))

              val steps = listOf(
                OrderStatus.PLACED,
                OrderStatus.CONFIRMED,
                OrderStatus.PREPARING,
                OrderStatus.READY_FOR_DELIVERY,
                OrderStatus.OUT_FOR_DELIVERY,
                OrderStatus.DELIVERED
              )

              val currentStepIndex = order.orderStatus.stepIndex

              steps.forEachIndexed { index, status ->
                val isCompleted = currentStepIndex >= index
                val isCurrent = currentStepIndex == index

                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.Top
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                      modifier = Modifier
                        .size(24.dp)
                        .background(
                          color = if (isCompleted) GroceryGreenPrimary else Color(0xFFE2E8F0),
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
                      }
                    }

                    if (index < steps.size - 1) {
                      Box(
                        modifier = Modifier
                          .width(2.dp)
                          .height(28.dp)
                          .background(if (currentStepIndex > index) GroceryGreenPrimary else Color(0xFFE2E8F0))
                      )
                    }
                  }

                  Spacer(modifier = Modifier.width(12.dp))

                  Column {
                    Text(
                      text = status.label,
                      fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                      fontSize = 13.sp,
                      color = if (isCompleted) GroceryTextPrimary else GroceryTextMuted
                    )
                    Text(
                      text = when (status) {
                        OrderStatus.PLACED -> "Order received by Ghar Tak Grocery"
                        OrderStatus.CONFIRMED -> "Verified and accepted for delivery"
                        OrderStatus.PREPARING -> "Fresh items being packed at Rewa Hub"
                        OrderStatus.READY_FOR_DELIVERY -> "Assigned to courier partner"
                        OrderStatus.OUT_FOR_DELIVERY -> "Rider is heading to your address"
                        OrderStatus.DELIVERED -> "Handed over at your doorstep"
                        else -> ""
                      },
                      fontSize = 11.sp,
                      color = GroceryTextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                  }
                }
              }
            }
          }
        }

        // 4. Delivery Partner Info
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
                    .size(44.dp)
                    .background(GroceryGreenContainer, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = GroceryGreenDark,
                    modifier = Modifier.size(24.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = order.assignedDeliveryPartnerName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                  )
                  Text(
                    text = "Rewa Hub Partner • Hero Splendor (MP-17)",
                    fontSize = 11.sp,
                    color = GroceryTextSecondary
                  )
                }
              }

              Surface(
                shape = CircleShape,
                color = GroceryGreenContainer,
                modifier = Modifier.size(38.dp)
              ) {
                IconButton(onClick = {}) {
                  Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call Rider",
                    tint = GroceryGreenDark,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }

        // 5. Delivery Address
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "Delivery Location in Rewa",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = order.deliveryAddressText,
                fontSize = 12.sp,
                color = GroceryTextSecondary,
                lineHeight = 16.sp
              )
            }
          }
        }

        // 6. Ordered Items List
        item {
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "Items Ordered (${items.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Spacer(modifier = Modifier.height(8.dp))

              items.forEach { item ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.productName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                      .size(38.dp)
                      .clip(RoundedCornerShape(6.dp))
                      .background(Color(0xFFF1F5F9))
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = item.productName,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Medium
                    )
                    Text(
                      text = "${item.quantity} x ${item.packSize}",
                      fontSize = 10.sp,
                      color = GroceryTextSecondary
                    )
                  }
                  Text(
                    text = "₹${item.totalPrice.toInt()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = GroceryGreenDark
                  )
                }
              }

              HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = GroceryCardBorder)

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = "Total Paid", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(
                  text = "₹${order.totalAmount.toInt()}",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 15.sp,
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
