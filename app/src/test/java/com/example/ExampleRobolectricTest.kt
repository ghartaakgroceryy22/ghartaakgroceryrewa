package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.GharTakDatabase
import com.example.data.local.InitialData
import com.example.data.models.PaymentMethod
import com.example.data.models.PaymentStatus
import com.example.data.repository.GroceryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  private lateinit var db: GharTakDatabase
  private lateinit var repository: GroceryRepository

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, GharTakDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    repository = GroceryRepository(db.groceryDao())
  }

  @After
  fun tearDown() {
    db.close()
  }

  @Test
  fun `verify app name resource matches Ghar Tak Grocery`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Ghar Tak Grocery", appName)
  }

  @Test
  fun `verify initial data seeding and Rewa delivery zones`() = runBlocking {
    repository.ensureDataSeeded()

    val categories = repository.allCategories.first()
    assertTrue("Categories should not be empty", categories.isNotEmpty())

    val products = repository.allProducts.first()
    assertTrue("Products should be seeded", products.size >= 10)

    val zones = repository.deliveryZones.first()
    val civilLines = zones.find { it.zoneName == "Civil Lines" }
    assertNotNull("Civil Lines Rewa delivery zone must exist", civilLines)
    assertEquals("486001", civilLines?.pincode)
  }

  @Test
  fun `verify cart additions and manual UPI 2 percent cashback order placement`() = runBlocking {
    repository.ensureDataSeeded()

    val products = repository.allProducts.first()
    val potato = products.first { it.id == "p_potato" } // Price 28.0

    // Add 2 potatoes to cart
    repository.addToCart(potato.id)
    repository.addToCart(potato.id)

    val cart = repository.cartItems.first()
    assertEquals(1, cart.size)
    assertEquals(2, cart[0].cartItem.quantity)

    val addresses = repository.addresses.first()
    val defaultAddress = addresses.first()

    // Place manual UPI order
    val orderId = repository.placeOrder(
      items = cart,
      address = defaultAddress,
      paymentMethod = PaymentMethod.MANUAL_UPI,
      upiRefNumber = "UPI/2609/999888",
      paymentProofNote = "Paid via PhonePe",
      appliedCoupon = null,
      useWalletBalance = false
    )

    assertTrue("Order ID should be positive", orderId > 0)

    val orderWithItems = repository.getOrderWithItems(orderId).first()
    assertNotNull(orderWithItems)
    val order = orderWithItems!!.order

    assertEquals(PaymentStatus.PENDING_VERIFICATION, order.paymentStatus)
    assertTrue("Cashback should be computed at 2%", order.cashbackEarned > 0)

    // Approve manual payment
    repository.approveManualPayment(order)
    val approvedOrder = repository.getOrderWithItems(orderId).first()!!.order
    assertEquals(PaymentStatus.VERIFIED_APPROVED, approvedOrder.paymentStatus)
  }
}
