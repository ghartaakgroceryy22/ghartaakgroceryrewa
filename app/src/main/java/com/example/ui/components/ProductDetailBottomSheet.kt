package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.ProductEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailBottomSheet(
  product: ProductEntity,
  quantityInCart: Int,
  onAddToCart: () -> Unit,
  onIncreaseQuantity: () -> Unit,
  onDecreaseQuantity: () -> Unit,
  onDismiss: () -> Unit
) {
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
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = GroceryGreenContainer
        ) {
          Text(
            text = product.brand,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = GroceryGreenDark,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Product Image
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Color(0xFFF1F5F9))
      ) {
        AsyncImage(
          model = product.imageUrl,
          contentDescription = product.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        if (product.discountPercent > 0) {
          Surface(
            color = GroceryDiscountRed,
            shape = RoundedCornerShape(bottomEnd = 8.dp),
            modifier = Modifier.align(Alignment.TopStart)
          ) {
            Text(
              text = "${product.discountPercent}% OFF",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = product.name,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = GroceryTextPrimary
      )

      Text(
        text = product.hindiName,
        fontSize = 13.sp,
        color = GroceryGreenDark,
        fontWeight = FontWeight.Medium
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Pack: ${product.packSize}",
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = GroceryTextSecondary
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = product.description,
        fontSize = 13.sp,
        color = GroceryTextSecondary,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(16.dp))

      HorizontalDivider(color = GroceryCardBorder)

      Spacer(modifier = Modifier.height(16.dp))

      // Price & Add to Cart Action Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "₹${product.price.toInt()}",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 22.sp,
              color = GroceryGreenDark
            )
            if (product.mrp > product.price) {
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "₹${product.mrp.toInt()}",
                fontSize = 14.sp,
                color = GroceryTextMuted,
                textDecoration = TextDecoration.LineThrough
              )
            }
          }
          if (product.mrp > product.price) {
            Text(
              text = "Save ₹${(product.mrp - product.price).toInt()}",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = GroceryDiscountRed
            )
          }
        }

        if (quantityInCart == 0) {
          Button(
            onClick = onAddToCart,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
            modifier = Modifier.height(44.dp)
          ) {
            Text("ADD TO CART", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
          }
        } else {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = GroceryGreenPrimary,
            modifier = Modifier.height(44.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp)
            ) {
              IconButton(onClick = onDecreaseQuantity) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White)
              }
              Text(
                text = "$quantityInCart",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp)
              )
              IconButton(onClick = onIncreaseQuantity) {
                Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}
