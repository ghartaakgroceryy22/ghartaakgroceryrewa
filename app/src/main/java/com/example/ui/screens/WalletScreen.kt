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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.GroceryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
  viewModel: GroceryViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val user by viewModel.currentUser.collectAsState()
  val transactions by viewModel.walletTransactions.collectAsState()
  val balance = user?.walletBalance ?: 0.0

  val dateFormatter = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Ghar Tak Wallet", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("wallet_back_btn")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    modifier = modifier
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(GroceryBackground),
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Balance Hero Card
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = GroceryGreenPrimary),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.horizontalGradient(
                  colors = listOf(GroceryGreenPrimary, GroceryGreenDark)
                )
              )
              .padding(20.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "Available Balance",
                  color = Color.White.copy(alpha = 0.85f),
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Medium
                )
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = GroceryAmber
                ) {
                  Text(
                    text = "REWA SAVINGS",
                    color = Color(0xFF78350F),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = "₹${balance.toInt()}",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = "Can be used anytime for grocery checkout in Rewa",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.9f)
              )
            }
          }
        }
      }

      // 2. 2% Cashback Explainer Card
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = GroceryAmberContainer),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(34.dp)
                  .background(GroceryAmber, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.CurrencyRupee,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "How 2% Prepaid Cashback Works",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color(0xFF78350F)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "1. Select 'Prepaid UPI & QR' during checkout.\n2. Pay via PhonePe, Google Pay, or Paytm and enter your UTR number.\n3. When our Rewa Admin or Delivery Agent approves your payment receipt, 2% of the order value is instantly credited back into this wallet!\n4. Use it on your next grocery order.",
              fontSize = 12.sp,
              color = Color(0xFF92400E),
              lineHeight = 17.sp
            )
          }
        }
      }

      // 3. Transactions History List
      item {
        Text(
          text = "Wallet History",
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = GroceryTextPrimary
        )
      }

      if (transactions.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "No transactions yet",
              fontSize = 13.sp,
              color = GroceryTextSecondary
            )
          }
        }
      } else {
        items(transactions) { tx ->
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .background(
                      if (tx.isCredit) GroceryGreenContainer else GroceryDiscountPink,
                      CircleShape
                    ),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = if (tx.isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = if (tx.isCredit) GroceryGreenDark else GroceryDiscountRed,
                    modifier = Modifier.size(18.dp)
                  )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                  Text(
                    text = tx.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = GroceryTextPrimary
                  )
                  Text(
                    text = tx.description,
                    fontSize = 11.sp,
                    color = GroceryTextSecondary
                  )
                  Text(
                    text = dateFormatter.format(Date(tx.timestamp)),
                    fontSize = 10.sp,
                    color = GroceryTextMuted
                  )
                }
              }

              Text(
                text = "${if (tx.isCredit) "+" else "-"}₹${tx.amount.toInt()}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = if (tx.isCredit) GroceryGreenDark else GroceryDiscountRed
              )
            }
          }
        }
      }
    }
  }
}
