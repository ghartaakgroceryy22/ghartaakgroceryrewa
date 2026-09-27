package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserRole
import com.example.ui.theme.*

@Composable
fun RoleSwitcherDialog(
  currentRole: UserRole,
  onSelectRole: (UserRole) -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Switch User Mode",
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Switch roles to test and experience all features of Ghar Tak Grocery:",
          fontSize = 12.sp,
          color = GroceryTextSecondary
        )

        RoleOptionCard(
          title = "Customer Mode",
          desc = "Browse groceries, add to cart, pay with UPI & get 2% cashback, track order live",
          icon = Icons.Default.Person,
          color = GroceryGreenPrimary,
          isSelected = currentRole == UserRole.CUSTOMER,
          onClick = {
            onSelectRole(UserRole.CUSTOMER)
            onDismiss()
          },
          testTag = "role_customer_opt"
        )

        RoleOptionCard(
          title = "Delivery Partner Mode",
          desc = "Rider portal (Rewa Hub): assigned orders, customer location, verify UPI payment, mark delivered",
          icon = Icons.Default.TwoWheeler,
          color = Color(0xFF0284C7),
          isSelected = currentRole == UserRole.DELIVERY_PARTNER,
          onClick = {
            onSelectRole(UserRole.DELIVERY_PARTNER)
            onDismiss()
          },
          testTag = "role_delivery_opt"
        )

        RoleOptionCard(
          title = "Admin Mode",
          desc = "Admin dashboard: approve/reject manual UPI payments, manage product catalog & stock, orders",
          icon = Icons.Default.AdminPanelSettings,
          color = Color(0xFF9333EA),
          isSelected = currentRole == UserRole.ADMIN,
          onClick = {
            onSelectRole(UserRole.ADMIN)
            onDismiss()
          },
          testTag = "role_admin_opt"
        )
      }
    },
    confirmButton = {
      TextButton(onClick = onDismiss) {
        Text("Close")
      }
    }
  )
}

@Composable
fun RoleOptionCard(
  title: String,
  desc: String,
  icon: ImageVector,
  color: Color,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = if (isSelected) color.copy(alpha = 0.12f) else Color(0xFFF8FAFC),
    border = BorderStroke(
      width = if (isSelected) 1.5.dp else 1.dp,
      color = if (isSelected) color else GroceryCardBorder
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag(testTag)
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .background(color, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = GroceryTextPrimary)
        Text(text = desc, fontSize = 11.sp, color = GroceryTextSecondary, lineHeight = 15.sp)
      }
      if (isSelected) {
        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = color)
      }
    }
  }
}
