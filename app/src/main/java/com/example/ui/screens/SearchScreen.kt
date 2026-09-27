package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ProductCard
import com.example.ui.theme.*
import com.example.viewmodel.GroceryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
  viewModel: GroceryViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val searchQuery by viewModel.searchQuery.collectAsState()
  val searchResults by viewModel.searchResults.collectAsState()
  val cartItems by viewModel.cartItems.collectAsState()

  val cartMap = remember(cartItems) {
    cartItems.associate { it.cartItem.productId to it.cartItem.quantity }
  }

  val popularSearches = listOf(
    "Atta", "Milk", "Aloo", "Poha", "Mustard Oil", "Toor Dal", "Maggi", "Salt", "Paneer", "Haldiram"
  )

  Scaffold(
    topBar = {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("search_back_btn")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }

          TextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = {
              Text(
                "Search groceries in Rewa...",
                fontSize = 14.sp,
                color = GroceryTextSecondary
              )
            },
            leadingIcon = {
              Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = GroceryGreenPrimary
              )
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                  Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
              focusedContainerColor = GroceryBackground,
              unfocusedContainerColor = GroceryBackground,
              focusedIndicatorColor = Color.Transparent,
              unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
              .weight(1f)
              .testTag("search_input_field")
          )
        }
      }
    },
    modifier = modifier
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .background(GroceryBackground)
    ) {
      // Popular search keywords
      if (searchQuery.isBlank()) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Popular Searches in Rewa",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = GroceryTextSecondary
          )
          Spacer(modifier = Modifier.height(8.dp))
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(popularSearches) { tag ->
              SuggestionChip(
                onClick = { viewModel.updateSearchQuery(tag) },
                label = { Text(tag, fontSize = 12.sp) },
                shape = RoundedCornerShape(20.dp),
                colors = SuggestionChipDefaults.suggestionChipColors(
                  containerColor = MaterialTheme.colorScheme.surface
                )
              )
            }
          }
        }
      }

      // Results header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = if (searchQuery.isBlank()) "Recommended Essentials" else "Found ${searchResults.size} items",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = GroceryTextPrimary
        )
      }

      // Empty State
      if (searchResults.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🔍", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "No groceries found for \"$searchQuery\"",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = GroceryTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Try searching for potato, milk, oil, tea, or rice",
              fontSize = 12.sp,
              color = GroceryTextSecondary
            )
          }
        }
      } else {
        // Results Grid
        LazyVerticalGrid(
          columns = GridCells.Fixed(2),
          contentPadding = PaddingValues(start = 12.dp, end = 12.dp, bottom = 90.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          modifier = Modifier
            .fillMaxSize()
            .testTag("search_results_grid")
        ) {
          items(searchResults) { product ->
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
}
