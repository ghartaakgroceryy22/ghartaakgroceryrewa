package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.models.OrderStatus
import com.example.data.models.PaymentMethod
import com.example.data.models.PaymentStatus
import com.example.ui.theme.*
import com.example.viewmodel.GroceryViewModel
import com.example.viewmodel.ScreenDestination
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersHistoryScreen(
  viewModel: GroceryViewModel,
  onBack: () -> Unit,
  onTrackOrder: (Long) -> Unit,
  modifier: Modifier = Modifier
) {
  val allOrders by viewModel.allOrders.collectAsState()
  val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("My Orders", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("orders_history_back_btn")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    modifier = modifier
  ) { padding ->
    if (allOrders.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "📦", fontSize = 48.sp)
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "No orders placed yet",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = GroceryTextPrimary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Orders you place in Rewa will show up here",
            fontSize = 12.sp,
            color = GroceryTextSecondary
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .background(GroceryBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        items(allOrders) { orderWithItems ->
          val order = orderWithItems.order
          val items = orderWithItems.items

          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("order_card_${order.id}")
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              // Top Row: Order Number & Status Pill
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "Order #${order.orderNumber}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GroceryTextPrimary
                  )
                  Text(
                    text = dateFormatter.format(Date(order.createdAt)),
                    fontSize = 11.sp,
                    color = GroceryTextMuted
                  )
                }

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = when (order.orderStatus) {
                    OrderStatus.DELIVERED -> GroceryGreenContainer
                    OrderStatus.CANCELLED -> GroceryDiscountPink
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
                      OrderStatus.CANCELLED -> GroceryDiscountRed
                      OrderStatus.OUT_FOR_DELIVERY -> Color(0xFF78350F)
                      else -> Color(0xFF0369A1)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Payment Status Badge
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (order.paymentMethod == PaymentMethod.MANUAL_UPI) Icons.Default.QrCode else Icons.Default.Payments,
                  contentDescription = null,
                  tint = GroceryGreenDark,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "${order.paymentMethod.title} • ${order.paymentStatus.label}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Medium,
                  color = GroceryTextSecondary
                )
              }

              Spacer(modifier = Modifier.height(6.dp))

              // Items summary text
              Text(
                text = items.joinToString(", ") { "${it.quantity}x ${it.productName}" },
                fontSize = 12.sp,
                color = GroceryTextSecondary,
                maxLines = 2
              )

              HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = GroceryCardBorder)

              // Bottom Row: Total Amount & Track Button
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "Total: ₹${order.totalAmount.toInt()}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = GroceryGreenDark
                  )
                  if (order.cashbackEarned > 0) {
                    Text(
                      text = "+₹${order.cashbackEarned.toInt()} Cashback",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = GroceryAmber
                    )
                  }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  Button(
                    onClick = { onTrackOrder(order.id) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp)
                  ) {
                    Text("Track", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
