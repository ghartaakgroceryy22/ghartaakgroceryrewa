package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.data.models.*
import com.example.ui.theme.*
import com.example.viewmodel.GroceryViewModel

enum class AdminTab(val title: String) {
  PAYMENTS("Pending UPI"),
  ORDERS("All Orders"),
  PRODUCTS("Products"),
  OVERVIEW("KPIs")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  viewModel: GroceryViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(AdminTab.PAYMENTS) }

  val allOrders by viewModel.allOrders.collectAsState()
  val pendingOrders by viewModel.pendingVerificationOrders.collectAsState()
  val products by viewModel.categoryFilteredProducts.collectAsState()
  val categories by viewModel.categories.collectAsState()
  val lowStockProducts by viewModel.lowStockProducts.collectAsState()

  // Rejection Dialog State
  var orderToReject by remember { mutableStateOf<OrderEntity?>(null) }
  var rejectReason by remember { mutableStateOf("") }

  // Add / Edit Product Dialog State
  var showProductDialog by remember { mutableStateOf(false) }
  var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Admin Panel • Rewa", fontWeight = FontWeight.Bold, fontSize = 17.sp)
            Text("Ghar Tak Grocery Operations", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("admin_back_btn")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
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
      // Admin Navigation Tabs
      TabRow(
        selectedTabIndex = selectedTab.ordinal,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = GroceryGreenPrimary
      ) {
        AdminTab.values().forEach { tab ->
          Tab(
            selected = selectedTab == tab,
            onClick = { selectedTab = tab },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = tab.title,
                  fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 12.sp
                )
                if (tab == AdminTab.PAYMENTS && pendingOrders.isNotEmpty()) {
                  Spacer(modifier = Modifier.width(4.dp))
                  Surface(
                    shape = CircleShape,
                    color = GroceryDiscountRed,
                    modifier = Modifier.size(18.dp)
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(
                        text = "${pendingOrders.size}",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }
                }
              }
            }
          )
        }
      }

      when (selectedTab) {
        AdminTab.PAYMENTS -> {
          // Manual UPI Approval Queue
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            item {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = GroceryAmberContainer,
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF78350F),
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(
                    text = "Verify customer UPI payment with Bank Statement. On approval, order confirms and 2% cashback is credited to customer wallet.",
                    fontSize = 12.sp,
                    color = Color(0xFF78350F)
                  )
                }
              }
            }

            if (pendingOrders.isEmpty()) {
              item {
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                      imageVector = Icons.Default.CheckCircle,
                      contentDescription = null,
                      tint = GroceryGreenDark,
                      modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                      text = "All UPI Payments Verified!",
                      fontWeight = FontWeight.Bold,
                      fontSize = 15.sp,
                      color = GroceryGreenDark
                    )
                    Text(
                      text = "No pending manual payments at this time.",
                      fontSize = 12.sp,
                      color = GroceryTextSecondary
                    )
                  }
                }
              }
            } else {
              items(pendingOrders) { orderWithItems ->
                val order = orderWithItems.order
                val items = orderWithItems.items

                Card(
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column {
                        Text(
                          text = "Order #${order.orderNumber}",
                          fontWeight = FontWeight.Bold,
                          fontSize = 14.sp
                        )
                        Text(
                          text = "${order.customerName} (${order.customerPhone})",
                          fontSize = 12.sp,
                          color = GroceryTextSecondary
                        )
                      }
                      Text(
                        text = "₹${order.totalAmount.toInt()}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = GroceryGreenDark
                      )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                      color = Color(0xFFF1F5F9),
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                          text = "UTR / Ref No: ${order.upiRefNumber.ifEmpty { "Not provided" }}",
                          fontWeight = FontWeight.Bold,
                          fontSize = 13.sp,
                          color = GroceryTextPrimary
                        )
                        Text(
                          text = "Proof Note: ${order.paymentProofNote.ifEmpty { "Screenshot uploaded" }}",
                          fontSize = 11.sp,
                          color = GroceryTextSecondary
                        )
                        Text(
                          text = "Delivery to: ${order.area}, Rewa",
                          fontSize = 11.sp,
                          color = GroceryGreenDark
                        )
                      }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                      text = "Items: " + items.joinToString(", ") { "${it.quantity}x ${it.productName}" },
                      fontSize = 11.sp,
                      color = GroceryTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      OutlinedButton(
                        onClick = {
                          orderToReject = order
                          rejectReason = "Payment not credited to ICICI account"
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GroceryDiscountRed),
                        modifier = Modifier.weight(1f)
                      ) {
                        Text("Reject", fontWeight = FontWeight.Bold)
                      }

                      Button(
                        onClick = { viewModel.approveManualPayment(order) },
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                        modifier = Modifier.weight(1.5f)
                      ) {
                        Text("Approve & Confirm", fontWeight = FontWeight.Bold)
                      }
                    }
                  }
                }
              }
            }
          }
        }

        AdminTab.ORDERS -> {
          // Orders List with status update
          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            items(allOrders) { orderWithItems ->
              val order = orderWithItems.order

              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Column {
                      Text(
                        text = "Order #${order.orderNumber}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                      )
                      Text(
                        text = "${order.customerName} • ${order.area}",
                        fontSize = 11.sp,
                        color = GroceryTextSecondary
                      )
                    }
                    Text(
                      text = "₹${order.totalAmount.toInt()}",
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp,
                      color = GroceryGreenDark
                    )
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  // Change status button / chips
                  Text("Update Status:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    if (order.orderStatus == OrderStatus.PLACED || order.orderStatus == OrderStatus.CONFIRMED) {
                      Button(
                        onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.PREPARING) },
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryAmber),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                      ) {
                        Text("Preparing", fontSize = 10.sp)
                      }
                    }
                    if (order.orderStatus == OrderStatus.PREPARING) {
                      Button(
                        onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.READY_FOR_DELIVERY) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                      ) {
                        Text("Ready", fontSize = 10.sp)
                      }
                    }
                    if (order.orderStatus == OrderStatus.READY_FOR_DELIVERY) {
                      Button(
                        onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.OUT_FOR_DELIVERY) },
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                      ) {
                        Text("Out for Delivery", fontSize = 10.sp)
                      }
                    }
                    if (order.orderStatus == OrderStatus.OUT_FOR_DELIVERY) {
                      Button(
                        onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.DELIVERED) },
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenDark),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                      ) {
                        Text("Mark Delivered", fontSize = 10.sp)
                      }
                    }
                  }
                }
              }
            }
          }
        }

        AdminTab.PRODUCTS -> {
          // Products Management
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(16.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Catalog (${products.size} Products)",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
              Button(
                onClick = {
                  editingProduct = null
                  showProductDialog = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
              ) {
                Text("+ Add Product", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
              verticalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.fillMaxSize()
            ) {
              items(products) { prod ->
                Card(
                  shape = RoundedCornerShape(10.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Column(modifier = Modifier.weight(1f)) {
                      Text(
                        text = prod.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                      )
                      Text(
                        text = "${prod.hindiName} • ${prod.packSize} • ₹${prod.price.toInt()} (MRP ₹${prod.mrp.toInt()})",
                        fontSize = 11.sp,
                        color = GroceryTextSecondary
                      )
                      Text(
                        text = "Stock: ${prod.stockCount} units",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (prod.stockCount <= 10) GroceryDiscountRed else GroceryGreenDark
                      )
                    }

                    Row {
                      IconButton(
                        onClick = {
                          editingProduct = prod
                          showProductDialog = true
                        },
                        modifier = Modifier.size(30.dp)
                      ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GroceryGreenPrimary, modifier = Modifier.size(18.dp))
                      }
                      IconButton(
                        onClick = { viewModel.deleteProduct(prod.id) },
                        modifier = Modifier.size(30.dp)
                      ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GroceryDiscountRed, modifier = Modifier.size(18.dp))
                      }
                    }
                  }
                }
              }
            }
          }
        }

        AdminTab.OVERVIEW -> {
          // KPIs & Business Information
          val totalRevenue = allOrders.filter { it.order.orderStatus != OrderStatus.CANCELLED }.sumOf { it.order.totalAmount }
          val deliveredCount = allOrders.count { it.order.orderStatus == OrderStatus.DELIVERED }

          LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                KpiCard(title = "Total Orders", value = "${allOrders.size}", color = GroceryGreenPrimary, modifier = Modifier.weight(1f))
                KpiCard(title = "Delivered", value = "$deliveredCount", color = Color(0xFF0284C7), modifier = Modifier.weight(1f))
              }
            }

            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                KpiCard(title = "Total Revenue", value = "₹${totalRevenue.toInt()}", color = Color(0xFF047857), modifier = Modifier.weight(1f))
                KpiCard(title = "Pending UPI", value = "${pendingOrders.size}", color = GroceryAmber, modifier = Modifier.weight(1f))
              }
            }

            item {
              Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(14.dp)) {
                  Text(
                    text = "Rewa Operating Hubs & Zones",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "Serving: Civil Lines, Bodabag, Urrahat, Narendra Nagar, Sirmour Square, University Road, Dhekaha, Ratahara, Padra, Kothi Compound, Ananthpur, Chorhatta.",
                    fontSize = 12.sp,
                    color = GroceryTextSecondary,
                    lineHeight = 16.sp
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  // Reject Order Dialog
  if (orderToReject != null) {
    AlertDialog(
      onDismissRequest = { orderToReject = null },
      title = { Text("Reject Order & Cancel", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text("Provide cancellation reason for customer:", fontSize = 12.sp)
          Spacer(modifier = Modifier.height(8.dp))
          OutlinedTextField(
            value = rejectReason,
            onValueChange = { rejectReason = it },
            label = { Text("Reason") },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.rejectManualPayment(orderToReject!!.id, rejectReason)
            orderToReject = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = GroceryDiscountRed)
        ) {
          Text("Confirm Reject")
        }
      },
      dismissButton = {
        TextButton(onClick = { orderToReject = null }) {
          Text("Cancel")
        }
      }
    )
  }

  // Add / Edit Product Dialog
  if (showProductDialog) {
    var pName by remember { mutableStateOf(editingProduct?.name ?: "") }
    var pHindiName by remember { mutableStateOf(editingProduct?.hindiName ?: "") }
    var pBrand by remember { mutableStateOf(editingProduct?.brand ?: "Rewa Fresh") }
    var pDesc by remember { mutableStateOf(editingProduct?.description ?: "Fresh grocery item") }
    var pPackSize by remember { mutableStateOf(editingProduct?.packSize ?: "1 kg") }
    var pPrice by remember { mutableStateOf(editingProduct?.price?.toString() ?: "50") }
    var pMrp by remember { mutableStateOf(editingProduct?.mrp?.toString() ?: "60") }
    var pStock by remember { mutableStateOf(editingProduct?.stockCount?.toString() ?: "50") }
    var pCategory by remember { mutableStateOf(editingProduct?.categoryId ?: "fruits_veg") }
    var pImage by remember { mutableStateOf(editingProduct?.imageUrl ?: "https://images.unsplash.com/photo-1542838132-92c53300491e?w=500&auto=format&fit=crop&q=80") }

    AlertDialog(
      onDismissRequest = { showProductDialog = false },
      title = { Text(if (editingProduct == null) "Add New Product" else "Edit Product", fontWeight = FontWeight.Bold) },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(value = pName, onValueChange = { pName = it }, label = { Text("Product Name *") }, singleLine = true)
          OutlinedTextField(value = pHindiName, onValueChange = { pHindiName = it }, label = { Text("Hindi Name") }, singleLine = true)
          OutlinedTextField(value = pPackSize, onValueChange = { pPackSize = it }, label = { Text("Pack Size (e.g. 1 kg)") }, singleLine = true)
          OutlinedTextField(value = pPrice, onValueChange = { pPrice = it }, label = { Text("Selling Price (₹) *") }, singleLine = true)
          OutlinedTextField(value = pMrp, onValueChange = { pMrp = it }, label = { Text("MRP (₹)") }, singleLine = true)
          OutlinedTextField(value = pStock, onValueChange = { pStock = it }, label = { Text("Stock Count") }, singleLine = true)
          OutlinedTextField(value = pImage, onValueChange = { pImage = it }, label = { Text("Image URL") }, singleLine = true)
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (pName.isNotBlank() && pPrice.toDoubleOrNull() != null) {
              val priceVal = pPrice.toDoubleOrNull() ?: 50.0
              val mrpVal = pMrp.toDoubleOrNull() ?: priceVal
              val discount = if (mrpVal > priceVal) (((mrpVal - priceVal) / mrpVal) * 100).toInt() else 0

              val prod = ProductEntity(
                id = editingProduct?.id ?: "p_${System.currentTimeMillis()}",
                categoryId = pCategory,
                name = pName,
                hindiName = pHindiName,
                brand = pBrand,
                description = pDesc,
                packSize = pPackSize,
                price = priceVal,
                mrp = mrpVal,
                discountPercent = discount,
                stockCount = pStock.toIntOrNull() ?: 50,
                imageUrl = pImage
              )
              viewModel.saveProduct(prod, isNew = editingProduct == null)
              showProductDialog = false
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary)
        ) {
          Text("Save")
        }
      },
      dismissButton = {
        TextButton(onClick = { showProductDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun KpiCard(
  title: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(text = title, fontSize = 11.sp, color = GroceryTextSecondary)
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = color)
    }
  }
}
