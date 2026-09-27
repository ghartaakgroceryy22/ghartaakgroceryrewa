package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.models.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun RewaLocationHeader(
  currentLocation: String,
  currentRole: UserRole,
  onLocationClick: () -> Unit,
  onRoleClick: () -> Unit,
  onWalletClick: () -> Unit,
  walletBalance: Double,
  modifier: Modifier = Modifier
) {
  // Live animated pulsing radar effect for 15-min delivery beacon
  val infiniteTransition = rememberInfiniteTransition(label = "RadarBeacon")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.9f,
    targetValue = 1.35f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "PulseScale"
  )
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.9f,
    targetValue = 0.3f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "PulseAlpha"
  )

  Surface(
    color = GroceryGreenPrimary,
    contentColor = Color.White,
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              GroceryGreenDark,
              GroceryGreenPrimary
            )
          )
        )
        .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Location Info
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .weight(1f)
            .clickable(onClick = onLocationClick)
            .testTag("location_selector_btn")
        ) {
          // Delivery vehicle icon with glowing beacon
          Box(
            modifier = Modifier.size(38.dp),
            contentAlignment = Alignment.Center
          ) {
            Box(
              modifier = Modifier
                .size(38.dp)
                .scale(pulseScale)
                .background(GroceryLime.copy(alpha = pulseAlpha * 0.4f), CircleShape)
            )
            Box(
              modifier = Modifier
                .size(32.dp)
                .background(Color.White.copy(alpha = 0.22f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.ElectricBolt,
                contentDescription = "Express Delivery",
                tint = GroceryLime,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              // Glowing green live dot
              Box(
                modifier = Modifier
                  .size(7.dp)
                  .background(Color(0xFF4ADE80), CircleShape)
              )
              Spacer(modifier = Modifier.width(5.dp))
              Text(
                text = "12-18 MINS",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GroceryLime,
                letterSpacing = 0.6.sp
              )
              Text(
                text = " • REWA HUB",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.85f)
              )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = currentLocation,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
              )
              Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Change Location",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Wallet Pill with Gold coin
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = Color.White.copy(alpha = 0.18f),
          border = androidx.compose.foundation.BorderStroke(1.dp, GroceryLime.copy(alpha = 0.4f)),
          modifier = Modifier
            .clickable(onClick = onWalletClick)
            .testTag("header_wallet_btn")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AccountBalanceWallet,
              contentDescription = "Wallet",
              tint = GroceryLime,
              modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "₹${walletBalance.toInt()}",
              fontSize = 12.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Role Switcher Pill
        Surface(
          shape = RoundedCornerShape(20.dp),
          color = when (currentRole) {
            UserRole.CUSTOMER -> GroceryAmber
            UserRole.DELIVERY_PARTNER -> Color(0xFF0284C7)
            UserRole.ADMIN -> Color(0xFF9333EA)
          },
          shadowElevation = 2.dp,
          modifier = Modifier
            .clickable(onClick = onRoleClick)
            .testTag("role_switcher_pill")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = when (currentRole) {
                UserRole.CUSTOMER -> Icons.Default.Person
                UserRole.DELIVERY_PARTNER -> Icons.Default.TwoWheeler
                UserRole.ADMIN -> Icons.Default.AdminPanelSettings
              },
              contentDescription = "Role Mode",
              tint = Color.White,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = when (currentRole) {
                UserRole.CUSTOMER -> "Customer"
                UserRole.DELIVERY_PARTNER -> "Rider"
                UserRole.ADMIN -> "Admin"
              },
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }
  }
}

@Composable
fun ProductCard(
  product: ProductEntity,
  quantityInCart: Int,
  onAddToCart: () -> Unit,
  onIncreaseQuantity: () -> Unit,
  onDecreaseQuantity: () -> Unit,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val coroutineScope = rememberCoroutineScope()
  val buttonScale = remember { Animatable(1f) }

  Card(
    modifier = modifier
      .width(168.dp)
      .shadow(elevation = 3.dp, shape = RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag("product_card_${product.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, GroceryCardBorder.copy(alpha = 0.8f))
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      // Product Image with discount pill
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(118.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFFF8FAFC))
      ) {
        AsyncImage(
          model = ImageRequest.Builder(LocalContext.current)
            .data(product.imageUrl)
            .crossfade(true)
            .build(),
          contentDescription = product.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Discount Tag (Vibrant Pink/Red)
        if (product.discountPercent > 0) {
          Surface(
            color = GroceryDiscountRed,
            shape = RoundedCornerShape(topStart = 10.dp, bottomEnd = 8.dp),
            modifier = Modifier.align(Alignment.TopStart)
          ) {
            Text(
              text = "${product.discountPercent}% OFF",
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.ExtraBold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        // Badge if present (MANDI FRESH / BESTSELLER)
        if (product.badgeText.isNotBlank()) {
          Surface(
            color = GroceryGreenDark.copy(alpha = 0.92f),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier
              .align(Alignment.BottomStart)
              .padding(4.dp)
          ) {
            Text(
              text = product.badgeText,
              color = Color.White,
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Pack size
      Text(
        text = product.packSize,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = GroceryTextSecondary
      )

      // Title
      Text(
        text = product.name,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 16.sp,
        color = GroceryTextPrimary,
        modifier = Modifier.height(34.dp)
      )

      // Hindi subtitle
      Text(
        text = product.hindiName,
        fontSize = 10.sp,
        color = GroceryGreenDark,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(6.dp))

      // Price and Add button Row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "₹${product.price.toInt()}",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = GroceryGreenDark
          )
          if (product.mrp > product.price) {
            Text(
              text = "₹${product.mrp.toInt()}",
              fontSize = 11.sp,
              color = GroceryTextMuted,
              textDecoration = TextDecoration.LineThrough
            )
          }
        }

        // Add / Stepper Button with bounce scale micro-interaction
        AnimatedContent(
          targetState = quantityInCart,
          transitionSpec = {
            (scaleIn(animationSpec = spring(dampingRatio = 0.7f, stiffness = 400f)) + fadeIn()) togetherWith
              (scaleOut(animationSpec = tween(100)) + fadeOut())
          },
          label = "CartStepper"
        ) { qty ->
          if (qty == 0) {
            Button(
              onClick = {
                coroutineScope.launch {
                  buttonScale.animateTo(0.85f, animationSpec = tween(60))
                  buttonScale.animateTo(1.08f, animationSpec = tween(90))
                  buttonScale.animateTo(1f, animationSpec = tween(70))
                }
                onAddToCart()
              },
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = GroceryGreenPrimary
              ),
              modifier = Modifier
                .height(32.dp)
                .scale(buttonScale.value)
                .border(1.5.dp, GroceryGreenPrimary, RoundedCornerShape(10.dp))
                .shadow(1.dp, RoundedCornerShape(10.dp))
                .testTag("add_btn_${product.id}")
            ) {
              Text(
                text = "ADD",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GroceryGreenPrimary
              )
            }
          } else {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = GroceryGreenPrimary,
              shadowElevation = 2.dp,
              modifier = Modifier.height(32.dp)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 2.dp)
              ) {
                IconButton(
                  onClick = onDecreaseQuantity,
                  modifier = Modifier
                    .size(28.dp)
                    .testTag("minus_btn_${product.id}")
                ) {
                  Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                  )
                }

                Text(
                  text = "$qty",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 4.dp)
                )

                IconButton(
                  onClick = onIncreaseQuantity,
                  modifier = Modifier
                    .size(28.dp)
                    .testTag("plus_btn_${product.id}")
                ) {
                  Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
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

@Composable
fun FloatingCartBar(
  itemCount: Int,
  totalPrice: Double,
  onViewCartClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "CartBounce")
  val cartArrowOffset by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 6f,
    animationSpec = infiniteRepeatable(
      animation = tween(700, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "CartArrowOffset"
  )

  AnimatedVisibility(
    visible = itemCount > 0,
    enter = slideInVertically(
      initialOffsetY = { it },
      animationSpec = spring(dampingRatio = 0.75f, stiffness = 350f)
    ) + fadeIn(),
    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
    modifier = modifier
  ) {
    Surface(
      shape = RoundedCornerShape(18.dp),
      color = GroceryGreenPrimary,
      shadowElevation = 10.dp,
      border = androidx.compose.foundation.BorderStroke(1.dp, GroceryLime.copy(alpha = 0.5f)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 6.dp)
        .clickable(onClick = onViewCartClick)
        .testTag("floating_cart_bar")
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.horizontalGradient(
              colors = listOf(
                GroceryGreenPrimary,
                GroceryGreenDark
              )
            )
          )
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .background(Color.White.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.ShoppingBag,
              contentDescription = "Cart",
              tint = GroceryLime,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "$itemCount ${if (itemCount == 1) "ITEM" else "ITEMS"} • ₹${totalPrice.toInt()}",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 15.sp,
              color = Color.White
            )
            Text(
              text = "⚡ Extra ₹${(totalPrice * 0.02).toInt()} cashback on UPI pay",
              fontSize = 11.sp,
              color = GroceryLime,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
          Text(
            text = "View Cart",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp,
            color = Color.White
          )
          Spacer(modifier = Modifier.width(4.dp))
          Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Go to Cart",
            tint = GroceryLime,
            modifier = Modifier
              .size(16.dp)
              .offset(x = cartArrowOffset.dp)
          )
        }
      }
    }
  }
}
