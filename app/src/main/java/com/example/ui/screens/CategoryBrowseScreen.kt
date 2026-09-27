package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.CategoryEntity
import com.example.data.models.ProductEntity
import com.example.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.viewmodel.GroceryViewModel

enum class SortOption(val title: String) {
  POPULAR("Popular"),
  PRICE_LOW("Price: Low to High"),
  PRICE_HIGH("Price: High to Low"),
  DISCOUNT("Highest Discount")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBrowseScreen(
  viewModel: GroceryViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val categories by viewModel.categories.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val products by viewModel.categoryFilteredProducts.collectAsState()
  val cartItems by viewModel.cartItems.collectAsState()

  val cartMap = remember(cartItems) {
    cartItems.associate { it.cartItem.productId to it.cartItem.quantity }
  }

  var currentSort by remember { mutableStateOf(SortOption.POPULAR) }

  val sortedProducts = remember(products, currentSort) {
    when (currentSort) {
      SortOption.POPULAR -> products.sortedByDescending { it.isPopular }
      SortOption.PRICE_LOW -> products.sortedBy { it.price }
      SortOption.PRICE_HIGH -> products.sortedByDescending { it.price }
      SortOption.DISCOUNT -> products.sortedByDescending { it.discountPercent }
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = selectedCategory?.name ?: "All Products",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold
            )
            if (selectedCategory != null) {
              Text(
                text = selectedCategory!!.hindiName,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("category_back_btn")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
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
      // Horizontal Category Tabs
      LazyRow(
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
      ) {
        item {
          val isAllSelected = selectedCategory == null
          FilterChip(
            selected = isAllSelected,
            onClick = { viewModel.selectCategory(null) },
            label = { Text("All Items", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = GroceryGreenPrimary,
              selectedLabelColor = Color.White
            )
          )
        }

        items(categories) { cat ->
          val isSelected = selectedCategory?.id == cat.id
          FilterChip(
            selected = isSelected,
            onClick = { viewModel.selectCategory(cat) },
            label = {
              Text(
                text = "${cat.iconEmoji} ${cat.name}",
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = GroceryGreenPrimary,
              selectedLabelColor = Color.White
            )
          )
        }
      }

      // Sort & Count Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${sortedProducts.size} Products available in Rewa",
          fontSize = 12.sp,
          color = GroceryTextSecondary,
          fontWeight = FontWeight.Medium
        )

        // Simple Sort Dropdown
        var showSortMenu by remember { mutableStateOf(false) }
        Box {
          Row(
            modifier = Modifier
              .clickable { showSortMenu = true }
              .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.FilterList,
              contentDescription = "Sort",
              tint = GroceryGreenPrimary,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = currentSort.title,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = GroceryGreenPrimary
            )
          }

          DropdownMenu(
            expanded = showSortMenu,
            onDismissRequest = { showSortMenu = false }
          ) {
            SortOption.values().forEach { option ->
              DropdownMenuItem(
                text = { Text(option.title, fontSize = 13.sp) },
                onClick = {
                  currentSort = option
                  showSortMenu = false
                }
              )
            }
          }
        }
      }

      // Products Grid
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 90.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
          .fillMaxSize()
          .testTag("category_products_grid")
      ) {
        items(sortedProducts) { product ->
          ProductCard(
            product = product,
            quantityInCart = cartMap[product.id] ?: 0,
            onAddToCart = { viewModel.addToCart(product.id) },
            onIncreaseQuantity = { viewModel.addToCart(product.id) },
            onDecreaseQuantity = { viewModel.decreaseCartQuantity(product.id) },
            onClick = { viewModel.selectProduct(product) },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }
  }
}
