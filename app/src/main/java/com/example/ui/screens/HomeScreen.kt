package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.*
import com.example.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.viewmodel.GroceryViewModel
import com.example.viewmodel.ScreenDestination
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
  viewModel: GroceryViewModel,
  onNavigateToCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  val banners by viewModel.banners.collectAsState()
  val categories by viewModel.categories.collectAsState()
  val featuredProducts by viewModel.featuredProducts.collectAsState()
  val dealsOfTheDay by viewModel.dealsOfTheDay.collectAsState()
  val popularProducts by viewModel.popularProducts.collectAsState()
  val cartItems by viewModel.cartItems.collectAsState()
  val cartMap = remember(cartItems) {
    cartItems.associate { it.cartItem.productId to it.cartItem.quantity }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(GroceryBackground)
      .testTag("home_screen_list"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // 1. Search Bar Trigger
    item {
      Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 3.dp,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.navigateTo(ScreenDestination.Search) }
              .testTag("home_search_trigger")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Groceries",
                tint = GroceryGreenPrimary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Search \"Atta\", \"Milk\", \"Aloo\", \"Masala\"...",
                  fontSize = 13.sp,
                  color = GroceryTextSecondary
                )
                Text(
                  text = "रीवा में सबसे तेज़ ग्रोसरी डिलीवरी",
                  fontSize = 10.sp,
                  color = GroceryGreenDark,
                  fontWeight = FontWeight.Medium
                )
              }
            }
          }
        }
      }
    }

    // 2. Rewa Promise Banner Strip
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(GroceryGreenContainer)
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.ElectricBolt,
            contentDescription = null,
            tint = GroceryAmber,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Delivery in 15-20 mins across Rewa",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = GroceryGreenDark
          )
        }
        Text(
          text = "Min Order ₹0",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = GroceryGreenPrimary,
          modifier = Modifier
            .background(Color.White, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }
    }

    // 3. Animated Hero Promo Banners
    item {
      if (banners.isNotEmpty()) {
        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier.testTag("promo_banners_row")
        ) {
          items(banners) { banner ->
            HeroBannerCard(
              banner = banner,
              onClick = {
                val matchedCat = categories.find { it.id == banner.actionCategory }
                viewModel.selectCategory(matchedCat)
              }
            )
          }
        }
      }
    }

    // 4. Shop by Category Grid
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp, bottom = 4.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Shop by Category",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = GroceryTextPrimary
            )
            Text(
              text = "ताज़ा सामान श्रेणी अनुसार चुनें",
              fontSize = 11.sp,
              color = GroceryTextSecondary
            )
          }
          TextButton(
            onClick = { viewModel.selectCategory(null) },
            modifier = Modifier.testTag("view_all_categories_btn")
          ) {
            Text(
              text = "View All",
              color = GroceryGreenPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Categories Grid (4 columns)
        val displayCategories = categories.take(8)
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
        ) {
          for (rowIdx in 0 until (displayCategories.size + 3) / 4) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              for (colIdx in 0 until 4) {
                val itemIndex = rowIdx * 4 + colIdx
                if (itemIndex < displayCategories.size) {
                  val cat = displayCategories[itemIndex]
                  CategoryGridItem(
                    category = cat,
                    onClick = { viewModel.selectCategory(cat) },
                    modifier = Modifier.weight(1f)
                  )
                } else {
                  Spacer(modifier = Modifier.weight(1f))
                }
              }
            }
          }
        }
      }
    }

    // 5. 2% Cashback Notice Callout Card
    item {
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = GroceryAmberContainer),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .clickable { viewModel.navigateTo(ScreenDestination.Wallet) }
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .background(GroceryAmber, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.QrCodeScanner,
              contentDescription = "UPI Cashback",
              tint = Color.White,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Pay via UPI & Earn 2% Cashback! 💰",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Color(0xFF78350F)
            )
            Text(
              text = "Get 2% credited directly to your Ghar Tak Wallet on every prepaid order.",
              fontSize = 11.sp,
              color = Color(0xFF92400E)
            )
          }
          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = GroceryAmber
          )
        }
      }
    }

    // 6. Deals of the Day / Rewa Dhamaka Offers
    item {
      if (dealsOfTheDay.isNotEmpty()) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .background(GroceryDiscountPink, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.LocalFireDepartment,
                  contentDescription = null,
                  tint = GroceryDiscountRed,
                  modifier = Modifier.size(20.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "Today's Super Deals 🔥",
                  fontSize = 17.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = GroceryTextPrimary
                )
                Text(
                  text = "रीवा के आज के सबसे सस्ते दाम",
                  fontSize = 11.sp,
                  color = GroceryTextSecondary
                )
              }
            }

            Surface(
              shape = RoundedCornerShape(20.dp),
              color = GroceryDiscountPink,
              border = BorderStroke(1.dp, GroceryDiscountRed.copy(alpha = 0.3f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Timer,
                  contentDescription = null,
                  tint = GroceryDiscountRed,
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "ENDS IN 2H 15M",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = GroceryDiscountRed
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            items(dealsOfTheDay) { product ->
              ProductCard(
                product = product,
                quantityInCart = cartMap[product.id] ?: 0,
                onAddToCart = { viewModel.addToCart(product.id) },
                onIncreaseQuantity = { viewModel.addToCart(product.id) },
                onDecreaseQuantity = { viewModel.decreaseCartQuantity(product.id) },
                onClick = { viewModel.selectProduct(product) }
              )
            }
          }
        }
      }
    }

    // 7. Bestsellers & Mandi Fresh
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 20.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .background(GroceryGreenContainer, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = GroceryGreenPrimary,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Daily Essentials & Bestsellers",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = GroceryTextPrimary
            )
            Text(
              text = "रीवा के घरों में हर रोज़ इस्तेमाल होने वाले सामान",
              fontSize = 11.sp,
              color = GroceryTextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        LazyRow(
          contentPadding = PaddingValues(horizontal = 16.dp),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(featuredProducts) { product ->
            ProductCard(
              product = product,
              quantityInCart = cartMap[product.id] ?: 0,
              onAddToCart = { viewModel.addToCart(product.id) },
              onIncreaseQuantity = { viewModel.addToCart(product.id) },
              onDecreaseQuantity = { viewModel.decreaseCartQuantity(product.id) },
              onClick = { viewModel.selectProduct(product) }
            )
          }
        }
      }
    }

    // 8. Why Choose Ghar Tak Grocery in Rewa
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Text(
          text = "Why Ghar Tak Grocery?",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = GroceryTextPrimary
        )
        Text(
          text = "रीवा का अपना भरोसेमंद ग्रोसरी स्टोर",
          fontSize = 11.sp,
          color = GroceryTextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          WhyFeatureCard(
            title = "15-20 Min Delivery",
            desc = "Local Rewa riders from Bodabag, Civil Lines & Sirmour Chowk",
            icon = Icons.Default.Speed,
            color = GroceryGreenPrimary,
            modifier = Modifier.weight(1f)
          )
          WhyFeatureCard(
            title = "Mandi Rates",
            desc = "Fresh vegetables directly from Rewa Mandi every dawn",
            icon = Icons.Default.Spa,
            color = GroceryAmber,
            modifier = Modifier.weight(1f)
          )
          WhyFeatureCard(
            title = "2% UPI Cashback",
            desc = "Instant wallet reward on prepaid orders",
            icon = Icons.Default.Savings,
            color = GroceryDiscountRed,
            modifier = Modifier.weight(1f)
          )
        }
      }
    }
  }
}

@Composable
fun HeroBannerCard(
  banner: BannerEntity,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val bannerDrawable = when (banner.id) {
    "b_rewa_sale" -> R.drawable.img_hero_grocery_banner
    "b_upi_cashback" -> R.drawable.img_upi_cashback_banner
    "b_mandi_fresh" -> R.drawable.img_fresh_mandi_banner
    else -> R.drawable.img_dairy_bakery_banner
  }

  Card(
    modifier = modifier
      .width(310.dp)
      .height(150.dp)
      .shadow(6.dp, RoundedCornerShape(18.dp))
      .clickable(onClick = onClick)
      .testTag("banner_${banner.id}"),
    shape = RoundedCornerShape(18.dp)
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      // Real generated graphic asset
      Image(
        painter = painterResource(id = bannerDrawable),
        contentDescription = banner.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )

      // Gradient scrim for crisp readability of high-contrast text
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.horizontalGradient(
              colors = listOf(
                Color(0xF0072E1A),
                Color(0xC8083820),
                Color(0x33000000)
              )
            )
          )
          .padding(14.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.85f),
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          Surface(
            color = GroceryLime,
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = banner.tag,
              color = Color(0xFF78350F),
              fontSize = 9.sp,
              fontWeight = FontWeight.ExtraBold,
              modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
            )
          }

          Column {
            Text(
              text = banner.title,
              color = Color.White,
              fontSize = 16.sp,
              fontWeight = FontWeight.ExtraBold,
              lineHeight = 19.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
              text = banner.subtitle,
              color = Color.White.copy(alpha = 0.92f),
              fontSize = 11.sp,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White,
            shadowElevation = 2.dp
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = banner.discountText,
                color = GroceryGreenDark,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 11.sp
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = GroceryGreenDark,
                modifier = Modifier.size(13.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun CategoryGridItem(
  category: CategoryEntity,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val coroutineScope = rememberCoroutineScope()
  val scale = remember { Animatable(1f) }

  val pastelColors = listOf(
    Color(0xFFE8F5E9),
    Color(0xFFFEF3C7),
    Color(0xFFE0F2FE),
    Color(0xFFFCE7F3),
    Color(0xFFEDE9FE),
    Color(0xFFFFEDD5)
  )
  val catBg = pastelColors[kotlin.math.abs(category.id.hashCode()) % pastelColors.size]

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .padding(4.dp)
      .scale(scale.value)
      .clickable {
        coroutineScope.launch {
          scale.animateTo(0.88f, animationSpec = tween(60))
          scale.animateTo(1.05f, animationSpec = tween(90))
          scale.animateTo(1f, animationSpec = tween(60))
        }
        onClick()
      }
      .testTag("cat_item_${category.id}")
  ) {
    Box(
      modifier = Modifier
        .size(64.dp)
        .shadow(2.dp, RoundedCornerShape(18.dp))
        .background(catBg, RoundedCornerShape(18.dp))
        .border(1.dp, Color.White, RoundedCornerShape(18.dp)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = category.iconEmoji,
        fontSize = 30.sp
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = category.name,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = GroceryTextPrimary,
      textAlign = TextAlign.Center,
      maxLines = 2,
      overflow = TextOverflow.Ellipsis,
      lineHeight = 13.sp,
      modifier = Modifier.height(26.dp)
    )
  }
}

@Composable
fun WhyFeatureCard(
  title: String,
  desc: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  color: Color,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surface,
    shadowElevation = 1.dp,
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(10.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .background(color.copy(alpha = 0.12f), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = color,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        textAlign = TextAlign.Center,
        color = GroceryTextPrimary
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = desc,
        fontSize = 9.sp,
        color = GroceryTextSecondary,
        textAlign = TextAlign.Center,
        lineHeight = 11.sp
      )
    }
  }
}
