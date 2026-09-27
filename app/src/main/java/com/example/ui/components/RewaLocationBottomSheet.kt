package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.DeliveryZoneEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RewaLocationBottomSheet(
  zones: List<DeliveryZoneEntity>,
  currentLocation: String,
  onSelectZone: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var pincodeSearch by remember { mutableStateOf("") }

  val filteredZones = remember(zones, pincodeSearch) {
    if (pincodeSearch.isBlank()) zones
    else zones.filter {
      it.zoneName.contains(pincodeSearch, ignoreCase = true) ||
          it.pincode.contains(pincodeSearch)
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .navigationBarsPadding()
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Select Delivery Area in Rewa",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = GroceryTextPrimary
          )
          Text(
            text = "Rewa, Madhya Pradesh • Pincode 486001-486006",
            fontSize = 12.sp,
            color = GroceryTextSecondary
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      OutlinedTextField(
        value = pincodeSearch,
        onValueChange = { pincodeSearch = it },
        placeholder = { Text("Search Rewa area or pincode (486001)...", fontSize = 13.sp) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = GroceryGreenPrimary) },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("location_search_field")
      )

      Spacer(modifier = Modifier.height(12.dp))

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 350.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(filteredZones) { zone ->
          val isSelected = currentLocation.startsWith(zone.zoneName)
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = if (isSelected) GroceryGreenContainer else Color(0xFFF8FAFC),
            border = androidx.compose.foundation.BorderStroke(
              width = 1.dp,
              color = if (isSelected) GroceryGreenPrimary else GroceryCardBorder
            ),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onSelectZone("${zone.zoneName}, Rewa")
                onDismiss()
              }
              .testTag("zone_item_${zone.zoneName}")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = zone.zoneName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = GroceryTextPrimary
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "(${zone.pincode})",
                    fontSize = 12.sp,
                    color = GroceryTextSecondary
                  )
                }
                Text(
                  text = "⚡ Delivery: ${zone.estimatedMins} • Free above ₹${zone.minOrderFreeDelivery.toInt()}",
                  fontSize = 11.sp,
                  color = GroceryGreenDark,
                  fontWeight = FontWeight.Medium
                )
              }

              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Selected",
                  tint = GroceryGreenDark
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
