<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

// Role mode: 'customer' | 'rider' | 'store'
const currentRole = ref('customer')

// Category filter
const selectedCategory = ref('all')
const searchQuery = ref('')

// Cart state: map of productId -> quantity
const cart = ref({})
const isCartOpen = ref(false)
const isCheckoutOpen = ref(false)
const isOrderTrackingOpen = ref(false)
const activeOrder = ref(null)

// Rewa Addresses
const addresses = [
  'House #42, Civil Lines, Near Commissioner Office, Rewa (486001)',
  'Flat 204, Bodabag PTS Road, Near Stadium, Rewa (486001)',
  'Hostel Block B, APSU University Campus, Rewa (486003)',
  'Plot 18, Rajendra Nagar, Near Bus Stand, Rewa (486001)',
  'Kothi Compound, Venkat Road, Rewa (486001)'
]
const selectedAddress = ref(addresses[0])
const selectedPayment = ref('upi')
const customerPhone = ref('9827012345')

// Countdown timer for live tracking
const countdownMinutes = ref(14)
const countdownSeconds = ref(45)
let timerInterval = null

// Products catalog
const categories = [
  { id: 'all', name: 'All Items', icon: '🌟' },
  { id: 'dairy', name: 'Dairy & Paneer', icon: '🥛' },
  { id: 'veggies', name: 'Fresh Veggies', icon: '🥬' },
  { id: 'staples', name: 'Atta, Dals & Rice', icon: '🌾' },
  { id: 'snacks', name: 'Tea & Snacks', icon: '☕' },
  { id: 'essentials', name: 'Daily Essentials', icon: '🧴' }
]

const products = ref([
  {
    id: 1,
    name: 'Fresh Cow Milk (Pouch)',
    hindiName: 'ताज़ा गाय का दूध',
    category: 'dairy',
    price: 32,
    mrp: 35,
    unit: '500 ml',
    icon: '🥛',
    badge: 'Rewa Farm Fresh',
    stock: 45
  },
  {
    id: 2,
    name: 'Desi Malai Paneer',
    hindiName: 'देसी मलाई पनीर',
    category: 'dairy',
    price: 95,
    mrp: 110,
    unit: '250 g',
    icon: '🧈',
    badge: 'Bestseller',
    stock: 28
  },
  {
    id: 3,
    name: 'Pure Desi Ghee (Jar)',
    hindiName: 'शुद्ध देसी घी',
    category: 'dairy',
    price: 340,
    mrp: 380,
    unit: '500 ml',
    icon: '🫙',
    badge: '100% Pure',
    stock: 15
  },
  {
    id: 4,
    name: 'Farm Fresh Tomatoes (Hybrid)',
    hindiName: 'लाल देसी टमाटर',
    category: 'veggies',
    price: 24,
    mrp: 35,
    unit: '1 kg',
    icon: '🍅',
    badge: 'Fresh Today',
    stock: 60
  },
  {
    id: 5,
    name: 'New Crop Potatoes (Aloo)',
    hindiName: 'नया आलू',
    category: 'veggies',
    price: 28,
    mrp: 35,
    unit: '1 kg',
    icon: '🥔',
    badge: 'Local MP Crop',
    stock: 80
  },
  {
    id: 6,
    name: 'Fresh Green Peas (Matar)',
    hindiName: 'हरी मीठी मटर',
    category: 'veggies',
    price: 45,
    mrp: 60,
    unit: '500 g',
    icon: '🫛',
    badge: 'Sweet & Tender',
    stock: 35
  },
  {
    id: 7,
    name: 'MP Sharbati Wheat Atta',
    hindiName: 'एमपी शरबती गेहूँ आटा',
    category: 'staples',
    price: 235,
    mrp: 260,
    unit: '5 kg',
    icon: '🌾',
    badge: '100% Sharbati',
    stock: 40
  },
  {
    id: 8,
    name: 'Premium Toor / Arhar Dal',
    hindiName: 'देसी तुअर दाल (अरहर)',
    category: 'staples',
    price: 158,
    mrp: 180,
    unit: '1 kg',
    icon: '🥣',
    badge: 'Unpolished',
    stock: 30
  },
  {
    id: 9,
    name: 'Kachi Ghani Mustard Oil',
    hindiName: 'कच्ची घानी सरसों तेल',
    category: 'staples',
    price: 142,
    mrp: 165,
    unit: '1 Litre',
    icon: '🌻',
    badge: 'Cold Pressed',
    stock: 22
  },
  {
    id: 10,
    name: 'Taj Mahal Premium Tea',
    hindiName: 'ताज महल कड़क चाय',
    category: 'snacks',
    price: 165,
    mrp: 190,
    unit: '250 g',
    icon: '☕',
    badge: 'Kadak Swad',
    stock: 50
  },
  {
    id: 11,
    name: 'Rewa Special Namkeen Mixture',
    hindiName: 'रीवा स्पेशल नमकीन मिक्चर',
    category: 'snacks',
    price: 65,
    mrp: 80,
    unit: '400 g',
    icon: '🥨',
    badge: 'Rewa Taste',
    stock: 45
  },
  {
    id: 12,
    name: 'Parle-G Gold Biscuits (Family Pack)',
    hindiName: 'पारले-जी बिस्कुट',
    category: 'snacks',
    price: 40,
    mrp: 45,
    unit: '1 kg pack',
    icon: '🍪',
    badge: 'Family Favorite',
    stock: 75
  },
  {
    id: 13,
    name: 'Tata Iodized Rock Salt',
    hindiName: 'टाटा आयोडाइज्ड नमक',
    category: 'essentials',
    price: 26,
    mrp: 28,
    unit: '1 kg',
    icon: '🧂',
    badge: 'Kitchen Basic',
    stock: 90
  },
  {
    id: 14,
    name: 'Everest Turmeric / Haldi Powder',
    hindiName: 'एवरेस्ट हल्दी पाउडर',
    category: 'essentials',
    price: 38,
    mrp: 45,
    unit: '200 g',
    icon: '🌿',
    badge: 'Pure Spice',
    stock: 40
  },
  {
    id: 15,
    name: 'Dettol Original Bathing Soap',
    hindiName: 'डेटॉल ओरिजिनल साबुन (Pack of 3)',
    category: 'essentials',
    price: 99,
    mrp: 120,
    unit: '3 x 100 g',
    icon: '🧼',
    badge: 'Germ Protection',
    stock: 35
  }
])

// Filtered products
const filteredProducts = computed(() => {
  return products.value.filter(p => {
    const matchesCategory = selectedCategory.value === 'all' || p.category === selectedCategory.value
    const query = searchQuery.value.toLowerCase().trim()
    const matchesSearch = !query || 
      p.name.toLowerCase().includes(query) || 
      p.hindiName.toLowerCase().includes(query) ||
      p.badge.toLowerCase().includes(query)
    return matchesCategory && matchesSearch
  })
})

// Cart item count and amounts
const cartItemCount = computed(() => {
  return Object.values(cart.value).reduce((sum, qty) => sum + qty, 0)
})

const cartItemsList = computed(() => {
  return Object.entries(cart.value)
    .filter(([_, qty]) => qty > 0)
    .map(([id, qty]) => {
      const prod = products.value.find(p => p.id === parseInt(id))
      return { ...prod, quantity: qty, itemTotal: (prod ? prod.price : 0) * qty }
    })
})

const itemsTotal = computed(() => {
  return cartItemsList.value.reduce((sum, item) => sum + item.itemTotal, 0)
})

const deliveryFee = computed(() => {
  if (itemsTotal.value === 0) return 0
  return itemsTotal.value >= 199 ? 0 : 25
})

const platformFee = 2

const grandTotal = computed(() => {
  if (itemsTotal.value === 0) return 0
  return itemsTotal.value + deliveryFee.value + platformFee
})

const freeDeliveryDifference = computed(() => {
  return Math.max(0, 199 - itemsTotal.value)
})

// Cart Actions
function addToCart(productId) {
  cart.value[productId] = (cart.value[productId] || 0) + 1
}

function removeFromCart(productId) {
  if (cart.value[productId] > 1) {
    cart.value[productId]--
  } else {
    delete cart.value[productId]
  }
}

function clearCart() {
  cart.value = {}
  isCartOpen.value = false
}

// Checkout flow
function openCheckout() {
  isCartOpen.value = false
  isCheckoutOpen.value = true
}

function confirmOrder() {
  const orderId = 'GTG-' + Math.floor(100000 + Math.random() * 900000)
  activeOrder.value = {
    id: orderId,
    items: [...cartItemsList.value],
    total: grandTotal.value,
    address: selectedAddress.value,
    paymentMethod: selectedPayment.value.toUpperCase(),
    orderTime: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    riderName: 'Ramesh Patel',
    riderVehicle: 'Hero Splendor (MP 17 MC 4192)',
    riderPhone: '+91 94251 78432',
    otp: Math.floor(1000 + Math.random() * 9000),
    statusStep: 1 // 1: Confirmed, 2: Packed, 3: On The Way, 4: Delivered
  }
  
  // Clear cart and show tracking
  cart.value = {}
  isCheckoutOpen.value = false
  isOrderTrackingOpen.value = true
  
  // Start countdown simulation
  countdownMinutes.value = 14
  countdownSeconds.value = 50
  startCountdown()
  
  // Progress simulator
  setTimeout(() => { if (activeOrder.value) activeOrder.value.statusStep = 2 }, 8000)
  setTimeout(() => { if (activeOrder.value) activeOrder.value.statusStep = 3 }, 18000)
}

function startCountdown() {
  if (timerInterval) clearInterval(timerInterval)
  timerInterval = setInterval(() => {
    if (countdownSeconds.value > 0) {
      countdownSeconds.value--
    } else if (countdownMinutes.value > 0) {
      countdownMinutes.value--
      countdownSeconds.value = 59
    } else {
      if (activeOrder.value) activeOrder.value.statusStep = 4
      clearInterval(timerInterval)
    }
  }, 1000)
}

onMounted(() => {
  // Pre-seed 2 sample items in cart for exciting first experience
  cart.value[1] = 1
  cart.value[2] = 1
})

onUnmounted(() => {
  if (timerInterval) clearInterval(timerInterval)
})
</script>

<template>
  <div class="ghar-tak-wrapper">
    <!-- Top Alert Notification -->
    <div class="promo-strip">
      <span class="badge-flash">⚡ REWA LIVE</span>
      <span>15-Minute Guaranteed Delivery across Civil Lines, Bodabag, APSU & Rajendra Nagar!</span>
      <span class="free-tag">Free delivery on ₹199+</span>
    </div>

    <!-- Main Navigation Bar -->
    <header class="app-header">
      <div class="brand-section">
        <div class="logo-box">🛒</div>
        <div>
          <h1 class="brand-title">Ghar Tak Grocery</h1>
          <p class="brand-location">📍 Rewa, MP • <span class="open-pill">12-18 Mins Dark Store</span></p>
        </div>
      </div>

      <!-- Role Selector -->
      <div class="role-selector">
        <button 
          :class="['role-btn', currentRole === 'customer' ? 'active' : '']"
          @click="currentRole = 'customer'"
        >
          🛒 Customer
        </button>
        <button 
          :class="['role-btn', currentRole === 'rider' ? 'active' : '']"
          @click="currentRole = 'rider'"
        >
          🚴 Rider Mode
        </button>
        <button 
          :class="['role-btn', currentRole === 'store' ? 'active' : '']"
          @click="currentRole = 'store'"
        >
          🏪 Store Admin
        </button>
      </div>

      <!-- Cart Button Header -->
      <div class="header-actions">
        <button v-if="activeOrder" class="track-btn" @click="isOrderTrackingOpen = true">
          📡 Track Live Order
        </button>

        <button class="cart-btn" @click="isCartOpen = true">
          <span class="cart-icon">🛍️</span>
          <span class="cart-label">Cart</span>
          <span class="cart-badge" v-if="cartItemCount > 0">{{ cartItemCount }}</span>
        </button>
      </div>
    </header>

    <!-- CUSTOMER VIEW -->
    <div v-if="currentRole === 'customer'" class="customer-container">
      <!-- Search & Hero -->
      <div class="search-banner">
        <div class="search-input-box">
          <span class="search-icon">🔍</span>
          <input 
            v-model="searchQuery" 
            type="text" 
            placeholder="Search fresh milk, paneer, atta, aloo, tea, spices..."
            class="search-input"
          />
          <button v-if="searchQuery" class="clear-btn" @click="searchQuery = ''">✕</button>
        </div>
      </div>

      <!-- Category Filter Pills -->
      <div class="category-scroll">
        <button 
          v-for="cat in categories" 
          :key="cat.id"
          :class="['cat-chip', selectedCategory === cat.id ? 'active' : '']"
          @click="selectedCategory = cat.id"
        >
          <span>{{ cat.icon }}</span>
          <span>{{ cat.name }}</span>
        </button>
      </div>

      <!-- Products Grid -->
      <div class="products-section">
        <div class="section-header">
          <h2>{{ categories.find(c => c.id === selectedCategory)?.name || 'Grocery Items' }}</h2>
          <span class="count-tag">{{ filteredProducts.length }} items available</span>
        </div>

        <div class="products-grid">
          <div v-for="product in filteredProducts" :key="product.id" class="product-card">
            <div class="product-icon-wrap">
              <span class="product-emoji">{{ product.icon }}</span>
              <span class="product-badge">{{ product.badge }}</span>
            </div>
            
            <div class="product-details">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-hindi">{{ product.hindiName }}</p>
              <p class="product-unit">{{ product.unit }}</p>
              
              <div class="price-row">
                <div class="prices">
                  <span class="current-price">₹{{ product.price }}</span>
                  <span class="mrp-price">₹{{ product.mrp }}</span>
                </div>

                <!-- Add to cart stepper -->
                <div class="action-wrap">
                  <button 
                    v-if="!cart[product.id]" 
                    class="add-btn" 
                    @click="addToCart(product.id)"
                  >
                    ADD +
                  </button>
                  <div v-else class="stepper-box">
                    <button class="step-btn" @click="removeFromCart(product.id)">−</button>
                    <span class="step-qty">{{ cart[product.id] }}</span>
                    <button class="step-btn" @click="addToCart(product.id)">+</button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Floating Bottom Cart Bar (Mobile Friendly) -->
      <div v-if="cartItemCount > 0" class="floating-cart-bar">
        <div class="bar-summary">
          <span class="bar-count">{{ cartItemCount }} {{ cartItemCount === 1 ? 'item' : 'items' }}</span>
          <span class="bar-total">₹{{ grandTotal }}</span>
          <span v-if="deliveryFee === 0" class="bar-free">🎉 Free Delivery</span>
        </div>
        <button class="checkout-bar-btn" @click="isCartOpen = true">
          View Cart & Pay →
        </button>
      </div>
    </div>

    <!-- RIDER VIEW -->
    <div v-else-if="currentRole === 'rider'" class="partner-container">
      <div class="partner-card">
        <div class="partner-header">
          <span class="rider-avatar">🛵</span>
          <div>
            <h3>Delivery Partner Dashboard</h3>
            <p>Rider: <strong>Ramesh Patel</strong> • Rewa City Hub</p>
          </div>
          <span class="online-status">🟢 Online (Active)</span>
        </div>
        
        <div class="metrics-row">
          <div class="metric-card">
            <h4>₹420</h4>
            <p>Today's Payout</p>
          </div>
          <div class="metric-card">
            <h4>11</h4>
            <p>Completed Trips</p>
          </div>
          <div class="metric-card">
            <h4>13.4 min</h4>
            <p>Avg Delivery Speed</p>
          </div>
        </div>

        <h4>Active Deliveries in Rewa</h4>
        <div class="job-card">
          <div class="job-badge">Order #GTG-74921 • Civil Lines</div>
          <p><strong>Customer:</strong> Amit Sharma • +91 98270 12345</p>
          <p><strong>Drop:</strong> House #42, Civil Lines, Near Commissioner Office</p>
          <p><strong>Items:</strong> Fresh Milk (500ml x 2), Malai Paneer (250g)</p>
          <div class="job-actions">
            <button class="job-btn call">📞 Call Customer</button>
            <button class="job-btn map">🗺️ Navigate</button>
            <button class="job-btn finish" @click="alert('Marked as delivered!')">✅ Mark Delivered</button>
          </div>
        </div>
      </div>
    </div>

    <!-- STORE MANAGER VIEW -->
    <div v-else-if="currentRole === 'store'" class="store-container">
      <div class="partner-card">
        <div class="partner-header">
          <span class="rider-avatar">🏪</span>
          <div>
            <h3>Rewa Central Micro-Dark Store</h3>
            <p>Zone: Bodabag Industrial Area • Hub ID: RW-01</p>
          </div>
          <span class="online-status">🟢 Dispatching</span>
        </div>

        <div class="metrics-row">
          <div class="metric-card">
            <h4>₹18,450</h4>
            <p>Today's Revenue</p>
          </div>
          <div class="metric-card">
            <h4>142</h4>
            <p>Orders Packed</p>
          </div>
          <div class="metric-card">
            <h4>8</h4>
            <p>Active Riders</p>
          </div>
        </div>

        <h4>Live Inventory & Stock Health</h4>
        <div class="stock-list">
          <div v-for="item in products" :key="item.id" class="stock-item">
            <span>{{ item.icon }} {{ item.name }} ({{ item.unit }})</span>
            <div class="stock-controls">
              <span class="stock-badge">{{ item.stock }} in stock</span>
              <span class="price-badge">₹{{ item.price }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- CART SLIDE-OVER MODAL -->
    <div v-if="isCartOpen" class="modal-overlay" @click.self="isCartOpen = false">
      <div class="modal-card">
        <div class="modal-header">
          <h3>Your Grocery Basket ({{ cartItemCount }})</h3>
          <button class="close-x" @click="isCartOpen = false">✕</button>
        </div>

        <!-- Free Delivery Threshold -->
        <div class="milestone-box">
          <div v-if="freeDeliveryDifference > 0">
            <span>Add <strong>₹{{ freeDeliveryDifference }}</strong> more for <strong>FREE DELIVERY</strong>!</span>
            <div class="progress-track">
              <div class="progress-fill" :style="{ width: `${Math.min(100, (itemsTotal / 199) * 100)}%` }"></div>
            </div>
          </div>
          <div v-else class="free-unlocked">
            🎉 You have unlocked <strong>FREE 15-Min Delivery</strong>!
          </div>
        </div>

        <!-- Items List -->
        <div class="cart-items-scroll">
          <div v-if="cartItemsList.length === 0" class="empty-state">
            <span class="empty-icon">🧺</span>
            <p>Your grocery basket is empty</p>
            <button class="browse-btn" @click="isCartOpen = false">Browse Fresh Items</button>
          </div>

          <div v-for="item in cartItemsList" :key="item.id" class="cart-row">
            <span class="cart-item-icon">{{ item.icon }}</span>
            <div class="cart-item-info">
              <h4>{{ item.name }}</h4>
              <p>{{ item.unit }} • ₹{{ item.price }} each</p>
            </div>
            <div class="cart-item-stepper">
              <button @click="removeFromCart(item.id)">−</button>
              <span>{{ item.quantity }}</span>
              <button @click="addToCart(item.id)">+</button>
            </div>
            <span class="cart-row-total">₹{{ item.itemTotal }}</span>
          </div>
        </div>

        <!-- Bill Details -->
        <div v-if="cartItemsList.length > 0" class="bill-box">
          <div class="bill-line">
            <span>Item Total</span>
            <span>₹{{ itemsTotal }}</span>
          </div>
          <div class="bill-line">
            <span>Delivery Fee (Rewa Hub)</span>
            <span :class="deliveryFee === 0 ? 'green-text' : ''">
              {{ deliveryFee === 0 ? 'FREE' : '₹' + deliveryFee }}
            </span>
          </div>
          <div class="bill-line">
            <span>Handling & Platform Fee</span>
            <span>₹{{ platformFee }}</span>
          </div>
          <div class="bill-line total-line">
            <span>To Pay</span>
            <span>₹{{ grandTotal }}</span>
          </div>

          <button class="checkout-submit-btn" @click="openCheckout">
            Proceed to Checkout (₹{{ grandTotal }}) →
          </button>
        </div>
      </div>
    </div>

    <!-- CHECKOUT MODAL -->
    <div v-if="isCheckoutOpen" class="modal-overlay" @click.self="isCheckoutOpen = false">
      <div class="modal-card">
        <div class="modal-header">
          <h3>Checkout & Delivery Address</h3>
          <button class="close-x" @click="isCheckoutOpen = false">✕</button>
        </div>

        <div class="checkout-body">
          <h4>📍 Select Rewa Delivery Location</h4>
          <div class="address-options">
            <label v-for="(addr, idx) in addresses" :key="idx" class="address-option">
              <input type="radio" v-model="selectedAddress" :value="addr" />
              <span>{{ addr }}</span>
            </label>
          </div>

          <div class="phone-input-wrap">
            <label>Recipient Mobile Number (for delivery OTP):</label>
            <input v-model="customerPhone" type="tel" class="text-input" placeholder="Enter 10-digit mobile" />
          </div>

          <h4>💳 Payment Method</h4>
          <div class="payment-options">
            <label class="payment-option">
              <input type="radio" v-model="selectedPayment" value="upi" />
              <span>⚡ Instant UPI (PhonePe / Google Pay / Paytm QR)</span>
            </label>
            <label class="payment-option">
              <input type="radio" v-model="selectedPayment" value="cod" />
              <span>💵 Cash on Delivery (Pay upon arrival)</span>
            </label>
          </div>

          <div class="order-summary-box">
            <span>Amount Payable: <strong>₹{{ grandTotal }}</strong></span>
            <span>Estimated Time: <strong>14-18 Minutes</strong></span>
          </div>

          <button class="confirm-order-btn" @click="confirmOrder">
            🚀 Confirm Order & Dispatch (₹{{ grandTotal }})
          </button>
        </div>
      </div>
    </div>

    <!-- LIVE ORDER TRACKING MODAL -->
    <div v-if="isOrderTrackingOpen && activeOrder" class="modal-overlay" @click.self="isOrderTrackingOpen = false">
      <div class="modal-card tracking-card">
        <div class="modal-header">
          <div>
            <h3>Live Order Tracking</h3>
            <span class="order-id">{{ activeOrder.id }}</span>
          </div>
          <button class="close-x" @click="isOrderTrackingOpen = false">✕</button>
        </div>

        <div class="timer-beacon">
          <div class="radar-pulse"></div>
          <div class="countdown-display">
            <span class="time-num">{{ String(countdownMinutes).padStart(2, '0') }}:{{ String(countdownSeconds).padStart(2, '0') }}</span>
            <span class="time-sub">ESTIMATED ARRIVAL IN REWA</span>
          </div>
        </div>

        <!-- 4 Step Tracker -->
        <div class="tracker-steps">
          <div :class="['step-item', activeOrder.statusStep >= 1 ? 'completed' : '']">
            <span class="step-dot">✓</span>
            <div>
              <strong>Order Confirmed</strong>
              <p>Sent to Rewa Dark Store at {{ activeOrder.orderTime }}</p>
            </div>
          </div>
          <div :class="['step-item', activeOrder.statusStep >= 2 ? 'completed' : '']">
            <span class="step-dot">{{ activeOrder.statusStep >= 2 ? '✓' : '2' }}</span>
            <div>
              <strong>Packed & Sealed</strong>
              <p>Hygiene checked & bagged</p>
            </div>
          </div>
          <div :class="['step-item', activeOrder.statusStep >= 3 ? 'completed' : '']">
            <span class="step-dot">{{ activeOrder.statusStep >= 3 ? '✓' : '3' }}</span>
            <div>
              <strong>Rider On The Way 🛵</strong>
              <p>{{ activeOrder.riderName }} ({{ activeOrder.riderVehicle }})</p>
            </div>
          </div>
          <div :class="['step-item', activeOrder.statusStep >= 4 ? 'completed' : '']">
            <span class="step-dot">{{ activeOrder.statusStep >= 4 ? '✓' : '4' }}</span>
            <div>
              <strong>Arrived at Doorstep 🏠</strong>
              <p>Share Delivery OTP: <strong>{{ activeOrder.otp }}</strong></p>
            </div>
          </div>
        </div>

        <div class="rider-info-box">
          <div class="rider-avatar-small">🛵</div>
          <div class="rider-details-text">
            <strong>{{ activeOrder.riderName }}</strong>
            <p>{{ activeOrder.riderVehicle }}</p>
            <p class="otp-pill">Delivery OTP: <strong>{{ activeOrder.otp }}</strong></p>
          </div>
          <a :href="'tel:' + activeOrder.riderPhone" class="call-rider-btn">Call Rider</a>
        </div>
      </div>
    </div>

    <!-- Android App Banner Footer -->
    <div class="native-app-banner">
      <div class="native-banner-content">
        <span class="android-icon">🤖</span>
        <div>
          <h4>Get the Native Android App for Rewa</h4>
          <p>Download the APK or test in real-time Android Emulator with push notifications & GPS beacon.</p>
        </div>
      </div>
      <div class="banner-buttons">
        <a href="https://ais-pre-j3gb42fi2j4ufcx4whfrrn-36962474408.asia-southeast1.run.app" target="_blank" class="preview-btn">
          Open In-Browser Emulator ↗
        </a>
      </div>
    </div>
  </div>
</template>

<style scoped>
.ghar-tak-wrapper {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
  color: #1a1a1a;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 12px 60px 12px;
}

/* Promo Strip */
.promo-strip {
  background: linear-gradient(90deg, #1b5e20, #2e7d32);
  color: white;
  padding: 8px 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 0.85rem;
  margin-top: 12px;
  flex-wrap: wrap;
  gap: 8px;
}
.badge-flash {
  background: #ffeb3b;
  color: #1b5e20;
  font-weight: 800;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 0.75rem;
}
.free-tag {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 4px;
}

/* Header */
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0;
  border-bottom: 1px solid #e0e0e0;
  flex-wrap: wrap;
  gap: 12px;
}
.brand-section {
  display: flex;
  align-items: center;
  gap: 12px;
}
.logo-box {
  font-size: 2rem;
  background: #e8f5e9;
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
}
.brand-title {
  margin: 0;
  font-size: 1.4rem;
  font-weight: 800;
  color: #1b5e20;
  line-height: 1.2;
}
.brand-location {
  margin: 2px 0 0 0;
  font-size: 0.85rem;
  color: #616161;
}
.open-pill {
  color: #2e7d32;
  font-weight: 600;
}

/* Role Selector */
.role-selector {
  display: flex;
  background: #f1f8e9;
  padding: 4px;
  border-radius: 30px;
  gap: 4px;
}
.role-btn {
  border: none;
  background: transparent;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  color: #33691e;
  cursor: pointer;
  transition: all 0.2s ease;
}
.role-btn.active {
  background: #2e7d32;
  color: white;
  box-shadow: 0 2px 6px rgba(46, 125, 50, 0.3);
}

/* Cart Button */
.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}
.track-btn {
  background: #e3f2fd;
  color: #0277bd;
  border: 1px solid #81d4fa;
  padding: 8px 14px;
  border-radius: 24px;
  font-size: 0.85rem;
  font-weight: 700;
  cursor: pointer;
}
.cart-btn {
  background: #1b5e20;
  color: white;
  border: none;
  padding: 8px 18px;
  border-radius: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-weight: 700;
  font-size: 0.95rem;
  box-shadow: 0 4px 12px rgba(27, 94, 32, 0.25);
  transition: transform 0.15s;
}
.cart-btn:hover {
  transform: translateY(-1px);
}
.cart-badge {
  background: #ffd600;
  color: #000;
  border-radius: 12px;
  padding: 2px 7px;
  font-size: 0.75rem;
  font-weight: 800;
}

/* Search */
.search-banner {
  margin: 18px 0;
}
.search-input-box {
  display: flex;
  align-items: center;
  background: #ffffff;
  border: 2px solid #81c784;
  border-radius: 12px;
  padding: 8px 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}
.search-icon {
  font-size: 1.2rem;
  margin-right: 10px;
  color: #757575;
}
.search-input {
  border: none;
  outline: none;
  width: 100%;
  font-size: 1rem;
}
.clear-btn {
  background: transparent;
  border: none;
  color: #9e9e9e;
  cursor: pointer;
}

/* Categories */
.category-scroll {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 8px;
  margin-bottom: 20px;
}
.cat-chip {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 20px;
  background: #f5f5f5;
  border: 1px solid #e0e0e0;
  font-size: 0.88rem;
  font-weight: 600;
  white-space: nowrap;
  cursor: pointer;
}
.cat-chip.active {
  background: #e8f5e9;
  border-color: #4caf50;
  color: #1b5e20;
}

/* Products Grid */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.section-header h2 {
  margin: 0;
  font-size: 1.3rem;
  color: #212121;
}
.count-tag {
  color: #757575;
  font-size: 0.85rem;
}
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}
.product-card {
  background: white;
  border: 1px solid #eeeeee;
  border-radius: 14px;
  padding: 14px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 6px rgba(0,0,0,0.03);
}
.product-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0,0,0,0.08);
}
.product-icon-wrap {
  position: relative;
  background: #f9fbe7;
  border-radius: 10px;
  height: 110px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
}
.product-emoji {
  font-size: 3rem;
}
.product-badge {
  position: absolute;
  top: 6px;
  left: 6px;
  background: #2e7d32;
  color: white;
  font-size: 0.68rem;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 4px;
}
.product-name {
  margin: 0 0 2px 0;
  font-size: 0.95rem;
  font-weight: 700;
  color: #212121;
}
.product-hindi {
  margin: 0 0 4px 0;
  font-size: 0.8rem;
  color: #757575;
}
.product-unit {
  margin: 0 0 10px 0;
  font-size: 0.8rem;
  color: #9e9e9e;
  font-weight: 500;
}
.price-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}
.current-price {
  font-size: 1.1rem;
  font-weight: 800;
  color: #1b5e20;
}
.mrp-price {
  font-size: 0.8rem;
  color: #9e9e9e;
  text-decoration: line-through;
  margin-left: 4px;
}
.add-btn {
  background: #e8f5e9;
  color: #1b5e20;
  border: 1.5px solid #2e7d32;
  padding: 6px 14px;
  border-radius: 6px;
  font-weight: 800;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.15s;
}
.add-btn:hover {
  background: #2e7d32;
  color: white;
}
.stepper-box {
  display: flex;
  align-items: center;
  background: #1b5e20;
  border-radius: 6px;
  color: white;
}
.step-btn {
  background: transparent;
  border: none;
  color: white;
  font-size: 1rem;
  font-weight: 700;
  padding: 4px 10px;
  cursor: pointer;
}
.step-qty {
  font-weight: 700;
  font-size: 0.85rem;
  padding: 0 4px;
}

/* Floating Bottom Bar */
.floating-cart-bar {
  position: fixed;
  bottom: 16px;
  left: 50%;
  transform: translateX(-50%);
  width: calc(100% - 32px);
  max-width: 600px;
  background: #1b5e20;
  color: white;
  border-radius: 16px;
  padding: 12px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 8px 24px rgba(27, 94, 32, 0.4);
  z-index: 99;
}
.bar-summary {
  display: flex;
  flex-direction: column;
}
.bar-count {
  font-size: 0.8rem;
  opacity: 0.9;
}
.bar-total {
  font-size: 1.2rem;
  font-weight: 800;
}
.bar-free {
  font-size: 0.75rem;
  color: #ffeb3b;
}
.checkout-bar-btn {
  background: #ffd600;
  color: #1b5e20;
  border: none;
  padding: 10px 18px;
  border-radius: 10px;
  font-weight: 800;
  font-size: 0.95rem;
  cursor: pointer;
}

/* Modals */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
  padding: 16px;
}
.modal-card {
  background: white;
  border-radius: 18px;
  width: 100%;
  max-width: 520px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  box-shadow: 0 12px 36px rgba(0,0,0,0.25);
}
.modal-header {
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eeeeee;
}
.modal-header h3 {
  margin: 0;
  font-size: 1.15rem;
}
.close-x {
  background: #f5f5f5;
  border: none;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  font-weight: bold;
  cursor: pointer;
}

/* Milestone */
.milestone-box {
  background: #e8f5e9;
  padding: 10px 20px;
  font-size: 0.85rem;
  color: #1b5e20;
}
.progress-track {
  height: 6px;
  background: #c8e6c9;
  border-radius: 4px;
  margin-top: 6px;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  background: #2e7d32;
  transition: width 0.3s;
}

/* Cart Items Scroll */
.cart-items-scroll {
  padding: 16px 20px;
  overflow-y: auto;
  flex: 1;
}
.cart-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}
.cart-item-icon {
  font-size: 1.8rem;
}
.cart-item-info {
  flex: 1;
}
.cart-item-info h4 {
  margin: 0;
  font-size: 0.95rem;
}
.cart-item-info p {
  margin: 2px 0 0 0;
  font-size: 0.8rem;
  color: #757575;
}
.cart-item-stepper {
  display: flex;
  align-items: center;
  border: 1px solid #c8e6c9;
  border-radius: 6px;
}
.cart-item-stepper button {
  background: transparent;
  border: none;
  padding: 4px 10px;
  font-weight: bold;
  color: #1b5e20;
  cursor: pointer;
}
.cart-item-stepper span {
  font-size: 0.85rem;
  font-weight: bold;
}
.cart-row-total {
  font-weight: 700;
  font-size: 0.95rem;
  min-width: 50px;
  text-align: right;
}

/* Bill Box */
.bill-box {
  background: #fafafa;
  padding: 16px 20px;
  border-top: 1px solid #eeeeee;
}
.bill-line {
  display: flex;
  justify-content: space-between;
  font-size: 0.88rem;
  margin-bottom: 6px;
  color: #616161;
}
.total-line {
  font-weight: 800;
  font-size: 1.05rem;
  color: #1b5e20;
  border-top: 1px dashed #bdbdbd;
  padding-top: 8px;
  margin-top: 8px;
}
.green-text {
  color: #2e7d32;
  font-weight: 700;
}
.checkout-submit-btn, .confirm-order-btn {
  background: #1b5e20;
  color: white;
  width: 100%;
  border: none;
  padding: 12px;
  border-radius: 10px;
  font-weight: 800;
  font-size: 1rem;
  margin-top: 12px;
  cursor: pointer;
}

/* Checkout body */
.checkout-body {
  padding: 16px 20px;
  overflow-y: auto;
}
.checkout-body h4 {
  margin: 12px 0 8px 0;
  font-size: 0.95rem;
}
.address-options, .payment-options {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 14px;
}
.address-option, .payment-option {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  font-size: 0.85rem;
  background: #f9f9f9;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  cursor: pointer;
}
.phone-input-wrap {
  margin-bottom: 14px;
}
.phone-input-wrap label {
  font-size: 0.85rem;
  display: block;
  margin-bottom: 4px;
  color: #424242;
}
.text-input {
  width: 100%;
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #bdbdbd;
  font-size: 0.9rem;
  box-sizing: border-box;
}
.order-summary-box {
  background: #e8f5e9;
  padding: 10px 14px;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  font-size: 0.88rem;
  margin-top: 10px;
}

/* Live Tracking */
.tracking-card {
  padding-bottom: 16px;
}
.order-id {
  font-size: 0.8rem;
  color: #757575;
}
.timer-beacon {
  background: linear-gradient(135deg, #1b5e20, #388e3c);
  color: white;
  text-align: center;
  padding: 24px 16px;
}
.time-num {
  font-size: 2.5rem;
  font-weight: 800;
  letter-spacing: 2px;
  display: block;
}
.time-sub {
  font-size: 0.75rem;
  letter-spacing: 1px;
  opacity: 0.9;
}
.tracker-steps {
  padding: 16px 24px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.step-item {
  display: flex;
  gap: 12px;
  opacity: 0.4;
  align-items: center;
}
.step-item.completed {
  opacity: 1;
}
.step-dot {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: #2e7d32;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 0.85rem;
}
.step-item strong {
  display: block;
  font-size: 0.9rem;
}
.step-item p {
  margin: 2px 0 0 0;
  font-size: 0.78rem;
  color: #757575;
}
.rider-info-box {
  margin: 0 20px;
  background: #f5f5f5;
  padding: 12px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
}
.rider-avatar-small {
  font-size: 1.8rem;
}
.rider-details-text {
  flex: 1;
}
.rider-details-text p {
  margin: 2px 0 0 0;
  font-size: 0.78rem;
  color: #616161;
}
.otp-pill {
  color: #1b5e20 !important;
}
.call-rider-btn {
  background: #2e7d32;
  color: white;
  text-decoration: none;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 0.8rem;
  font-weight: bold;
}

/* Partner & Store Modes */
.partner-container, .store-container {
  padding: 20px 0;
}
.partner-card {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 14px;
  padding: 20px;
}
.partner-header {
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid #eeeeee;
  padding-bottom: 14px;
  margin-bottom: 16px;
}
.partner-header h3 {
  margin: 0;
}
.partner-header p {
  margin: 2px 0 0 0;
  font-size: 0.85rem;
  color: #757575;
}
.online-status {
  margin-left: auto;
  font-size: 0.85rem;
  font-weight: 700;
  color: #2e7d32;
}
.metrics-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}
.metric-card {
  background: #f9fbe7;
  padding: 12px;
  border-radius: 10px;
  text-align: center;
  border: 1px solid #dce775;
}
.metric-card h4 {
  margin: 0;
  font-size: 1.3rem;
  color: #1b5e20;
}
.metric-card p {
  margin: 4px 0 0 0;
  font-size: 0.78rem;
  color: #558b2f;
}
.job-card {
  background: #f1f8e9;
  border: 1px solid #aed581;
  padding: 14px;
  border-radius: 10px;
}
.job-badge {
  font-weight: bold;
  color: #1b5e20;
  margin-bottom: 6px;
}
.job-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}
.job-btn {
  padding: 6px 12px;
  border-radius: 6px;
  border: none;
  font-size: 0.8rem;
  font-weight: bold;
  cursor: pointer;
}
.job-btn.call { background: #e0f2f1; color: #00796b; }
.job-btn.map { background: #e3f2fd; color: #0277bd; }
.job-btn.finish { background: #1b5e20; color: white; margin-left: auto; }

.stock-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.stock-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: #fafafa;
  border-radius: 6px;
  font-size: 0.88rem;
}
.stock-controls {
  display: flex;
  gap: 8px;
}
.stock-badge {
  background: #e8f5e9;
  color: #1b5e20;
  font-size: 0.75rem;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: bold;
}
.price-badge {
  font-weight: bold;
}

/* Native App Footer */
.native-app-banner {
  margin-top: 40px;
  background: #212121;
  color: white;
  border-radius: 14px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 16px;
}
.native-banner-content {
  display: flex;
  align-items: center;
  gap: 12px;
}
.android-icon {
  font-size: 2rem;
}
.native-banner-content h4 {
  margin: 0;
  font-size: 1rem;
}
.native-banner-content p {
  margin: 2px 0 0 0;
  font-size: 0.8rem;
  color: #bdbdbd;
}
.preview-btn {
  background: #4caf50;
  color: white;
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 8px;
  font-weight: bold;
  font-size: 0.85rem;
}
</style>
