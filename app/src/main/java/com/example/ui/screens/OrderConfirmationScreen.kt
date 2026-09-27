package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PaymentMethod
import com.example.ui.theme.*
import com.example.viewmodel.GroceryViewModel
import com.example.viewmodel.ScreenDestination

@Composable
fun OrderConfirmationScreen(
  orderId: Long,
  viewModel: GroceryViewModel,
  onTrackOrder: () -> Unit,
  onGoHome: () -> Unit,
  modifier: Modifier = Modifier
) {
  val allOrders by viewModel.allOrders.collectAsState()
  val orderWithItems = allOrders.find { it.order.id == orderId }
  val order = orderWithItems?.order

  // Pulsing celebration animation
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  Surface(
    color = GroceryBackground,
    modifier = modifier.fillMaxSize()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      // Animated Checkmark
      Box(
        modifier = Modifier
          .size(100.dp)
          .scale(pulseScale)
          .background(GroceryGreenContainer, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(76.dp)
            .background(GroceryGreenPrimary, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Success",
            tint = Color.White,
            modifier = Modifier.size(44.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "Order Placed Successfully! 🎉",
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        color = GroceryTextPrimary,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Order #${order?.orderNumber ?: "GTG-REW-$orderId"}",
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = GroceryGreenDark
      )

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Delivering to your doorstep in Rewa within 15-25 Mins",
        fontSize = 13.sp,
        color = GroceryTextSecondary,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(20.dp))

      // 2% Cashback Award Card
      if (order?.paymentMethod == PaymentMethod.MANUAL_UPI && (order.cashbackEarned > 0)) {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = GroceryAmberContainer),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .background(GroceryAmber, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Savings,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "₹${order.cashbackEarned.toInt()} Cashback Reserved! 💰",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF78350F)
              )
              Text(
                text = "Will be credited into your Ghar Tak Wallet once the payment is approved by admin/delivery agent.",
                fontSize = 11.sp,
                color = Color(0xFF92400E)
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Action Buttons
      Button(
        onClick = onTrackOrder,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("track_order_btn")
      ) {
        Icon(Icons.Default.TwoWheeler, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Track Live Delivery Status", fontWeight = FontWeight.Bold, fontSize = 15.sp)
      }

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedButton(
        onClick = onGoHome,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("continue_shopping_btn")
      ) {
        Text("Continue Shopping", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GroceryGreenPrimary)
      }
    }
  }
}
