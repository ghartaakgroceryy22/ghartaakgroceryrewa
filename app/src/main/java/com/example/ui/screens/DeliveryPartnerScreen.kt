package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.*
import com.example.ui.theme.*
import com.example.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryPartnerScreen(
  viewModel: GroceryViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val allOrders by viewModel.allOrders.collectAsState()
  var showOnlyActive by remember { mutableStateOf(true) }

  val displayedOrders = remember(allOrders, showOnlyActive) {
    if (showOnlyActive) {
      allOrders.filter { it.order.orderStatus != OrderStatus.DELIVERED && it.order.orderStatus != OrderStatus.CANCELLED }
    } else {
      allOrders.filter { it.order.orderStatus == OrderStatus.DELIVERED }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Delivery Partner Portal", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text("Rider: Ramesh Sharma • Rewa Hub", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("delivery_back_btn")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    modifier = modifier
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(GroceryBackground)
    ) {
      // Filter switch: Active vs Delivered
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = showOnlyActive,
          onClick = { showOnlyActive = true },
          label = { Text("Active Deliveries (${allOrders.count { it.order.orderStatus != OrderStatus.DELIVERED && it.order.orderStatus != OrderStatus.CANCELLED }})") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GroceryGreenPrimary,
            selectedLabelColor = Color.White
          )
        )
        FilterChip(
          selected = !showOnlyActive,
          onClick = { showOnlyActive = false },
          label = { Text("Delivered (${allOrders.count { it.order.orderStatus == OrderStatus.DELIVERED }})") },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = GroceryGreenPrimary,
            selectedLabelColor = Color.White
          )
        )
      }

      if (displayedOrders.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🛵", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = if (showOnlyActive) "No active delivery tasks" else "No delivered orders yet",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = GroceryTextPrimary
            )
            Text(
              text = "New orders in Rewa will appear here for pickup",
              fontSize = 12.sp,
              color = GroceryTextSecondary
            )
          }
        }
      } else {
        LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          items(displayedOrders) { orderWithItems ->
            val order = orderWithItems.order
            val items = orderWithItems.items

            Card(
              shape = RoundedCornerShape(14.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                // Header: Order number and status
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Order #${order.orderNumber}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                  Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (order.orderStatus) {
                      OrderStatus.DELIVERED -> GroceryGreenContainer
                      OrderStatus.OUT_FOR_DELIVERY -> GroceryAmberContainer
                      else -> Color(0xFFE0F2FE)
                    }
                  ) {
                    Text(
                      text = order.orderStatus.label,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = when (order.orderStatus) {
                        OrderStatus.DELIVERED -> GroceryGreenDark
                        OrderStatus.OUT_FOR_DELIVERY -> Color(0xFF78350F)
                        else -> Color(0xFF0369A1)
                      },
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Customer Info & Phone Call
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = order.customerName,
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp
                    )
                    Text(
                      text = order.deliveryAddressText,
                      fontSize = 11.sp,
                      color = GroceryTextSecondary,
                      lineHeight = 15.sp
                    )
                  }

                  IconButton(
                    onClick = {},
                    modifier = Modifier
                      .background(GroceryGreenContainer, CircleShape)
                      .size(36.dp)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Phone,
                      contentDescription = "Call Customer",
                      tint = GroceryGreenDark,
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Payment badge
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = if (order.paymentMethod == PaymentMethod.COD) Color(0xFFFEF3C7) else Color(0xFFE0F2FE),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Icon(
                      imageVector = if (order.paymentMethod == PaymentMethod.COD) Icons.Default.Payments else Icons.Default.QrCode,
                      contentDescription = null,
                      tint = GroceryGreenDark,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      text = "${order.paymentMethod.title} • Collect: ₹${order.totalAmount.toInt()}",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = GroceryGreenDark
                    )
                  }
                }

                // If UPI verification pending, allow delivery partner to approve as requested!
                if (order.paymentMethod == PaymentMethod.MANUAL_UPI && order.paymentStatus == PaymentStatus.PENDING_VERIFICATION) {
                  Spacer(modifier = Modifier.height(8.dp))
                  Button(
                    onClick = { viewModel.approveManualPayment(order) },
                    colors = ButtonDefaults.buttonColors(containerColor = GroceryAmber),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Verify & Confirm UPI Payment (Credit 2% Cashback)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                  }
                }

                // Delivery Status Progression Actions
                if (order.orderStatus != OrderStatus.DELIVERED && order.orderStatus != OrderStatus.CANCELLED) {
                  Spacer(modifier = Modifier.height(10.dp))
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    if (order.orderStatus != OrderStatus.OUT_FOR_DELIVERY) {
                      Button(
                        onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.OUT_FOR_DELIVERY) },
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                      ) {
                        Icon(Icons.Default.TwoWheeler, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Delivery", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                      }
                    }

                    Button(
                      onClick = {
                        val isCod = order.paymentMethod == PaymentMethod.COD
                        viewModel.deliveryPartnerDeliverOrder(order, isCod)
                      },
                      colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenDark),
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.weight(1f)
                    ) {
                      Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        if (order.paymentMethod == PaymentMethod.COD) "Collect ₹ & Deliver" else "Mark Delivered",
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
      }
    }
  }
}
