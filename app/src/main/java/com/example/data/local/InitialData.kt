package com.example.data.local

import com.example.data.models.*

object InitialData {

  val categories = listOf(
    CategoryEntity(
      id = "fruits_veg",
      name = "Fruits & Vegetables",
      hindiName = "ताज़ी फल एवं सब्जियां",
      iconEmoji = "🥦",
      displayOrder = 1
    ),
    CategoryEntity(
      id = "dairy_eggs",
      name = "Dairy & Eggs",
      hindiName = "दूध, दही और अंडे",
      iconEmoji = "🥛",
      displayOrder = 2
    ),
    CategoryEntity(
      id = "atta_rice",
      name = "Atta, Rice & Grains",
      hindiName = "आटा, चावल व अनाज",
      iconEmoji = "🌾",
      displayOrder = 3
    ),
    CategoryEntity(
      id = "dal_pulses",
      name = "Pulses & Dal",
      hindiName = "दालें व दलहन",
      iconEmoji = "🍲",
      displayOrder = 4
    ),
    CategoryEntity(
      id = "oil_ghee",
      name = "Cooking Oil & Ghee",
      hindiName = "तेल और शुद्ध घी",
      iconEmoji = "🛢️",
      displayOrder = 5
    ),
    CategoryEntity(
      id = "spices_masala",
      name = "Spices & Masala",
      hindiName = "खड़े व पिसे मसाले",
      iconEmoji = "🌶️",
      displayOrder = 6
    ),
    CategoryEntity(
      id = "snacks",
      name = "Snacks & Namkeen",
      hindiName = "नमकीन और स्नैक्स",
      iconEmoji = "🥨",
      displayOrder = 7
    ),
    CategoryEntity(
      id = "biscuits_bakery",
      name = "Biscuits & Bakery",
      hindiName = "बिस्कुट और ब्रेड",
      iconEmoji = "🍪",
      displayOrder = 8
    ),
    CategoryEntity(
      id = "tea_coffee",
      name = "Tea & Coffee",
      hindiName = "चाय पत्ती व कॉफ़ी",
      iconEmoji = "☕",
      displayOrder = 9
    ),
    CategoryEntity(
      id = "beverages",
      name = "Beverages & Cold Drinks",
      hindiName = "कोल्ड ड्रिंक्स व जूस",
      iconEmoji = "🥤",
      displayOrder = 10
    ),
    CategoryEntity(
      id = "instant_food",
      name = "Instant & Packaged Food",
      hindiName = "मैगी, पास्ता व नूडल्स",
      iconEmoji = "🍜",
      displayOrder = 11
    ),
    CategoryEntity(
      id = "cleaning",
      name = "Household & Cleaning",
      hindiName = "डिटर्जेंट व सफाई का सामान",
      iconEmoji = "🧼",
      displayOrder = 12
    ),
    CategoryEntity(
      id = "personal_care",
      name = "Personal Care",
      hindiName = "साबुन, शैम्पू व टूथपेस्ट",
      iconEmoji = "🧴",
      displayOrder = 13
    ),
    CategoryEntity(
      id = "baby_care",
      name = "Baby Care",
      hindiName = "शिशु आहार व डायपर",
      iconEmoji = "👶",
      displayOrder = 14
    )
  )

  val deliveryZones = listOf(
    DeliveryZoneEntity("Civil Lines", "486001", true, 25.0, 199.0, "15-20 mins"),
    DeliveryZoneEntity("Bodabag", "486001", true, 25.0, 199.0, "15-25 mins"),
    DeliveryZoneEntity("Urrahat", "486001", true, 25.0, 199.0, "15-20 mins"),
    DeliveryZoneEntity("Narendra Nagar", "486001", true, 25.0, 199.0, "18-25 mins"),
    DeliveryZoneEntity("Sirmour Square (Chauraha)", "486001", true, 20.0, 199.0, "12-20 mins"),
    DeliveryZoneEntity("University Road / APSU", "486003", true, 30.0, 249.0, "20-30 mins"),
    DeliveryZoneEntity("Dhekaha", "486001", true, 25.0, 199.0, "15-25 mins"),
    DeliveryZoneEntity("Ratahara", "486003", true, 30.0, 249.0, "20-30 mins"),
    DeliveryZoneEntity("Padra", "486001", true, 25.0, 199.0, "15-20 mins"),
    DeliveryZoneEntity("Kothi Compound", "486001", true, 20.0, 199.0, "10-18 mins"),
    DeliveryZoneEntity("Ananthpur", "486002", true, 25.0, 199.0, "18-25 mins"),
    DeliveryZoneEntity("Chorhatta", "486006", true, 35.0, 299.0, "25-35 mins"),
    DeliveryZoneEntity("Govindgarh Road", "486001", true, 30.0, 249.0, "20-30 mins")
  )

  val defaultAddresses = listOf(
    AddressEntity(
      recipientName = "Rajesh Patel",
      phone = "+91 94251 88320",
      houseFlatNo = "House No. 42-B",
      street = "Near Circuit House Road",
      area = "Civil Lines",
      landmark = "Behind District Court",
      pincode = "486001",
      addressType = "Home",
      isDefault = true
    ),
    AddressEntity(
      recipientName = "Rajesh Patel",
      phone = "+91 94251 88320",
      houseFlatNo = "Shop 14, Commercial Complex",
      street = "College Road",
      area = "Sirmour Square (Chauraha)",
      landmark = "Opposite Rewa Hotel",
      pincode = "486001",
      addressType = "Work",
      isDefault = false
    )
  )

  val products = listOf(
    // Fruits & Veg
    ProductEntity(
      id = "p_potato",
      categoryId = "fruits_veg",
      name = "Fresh Desi Potato (Aloo)",
      hindiName = "ताज़ा देसी आलू",
      brand = "Rewa Mandi Fresh",
      description = "Farm fresh, unwashed high quality local potatoes sourced directly from farmers around Rewa.",
      packSize = "1 kg",
      price = 28.0,
      mrp = 35.0,
      discountPercent = 20,
      isFeatured = true,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1518977676601-b53f82aba655?w=500&auto=format&fit=crop&q=80",
      badgeText = "Mandi Fresh"
    ),
    ProductEntity(
      id = "p_onion",
      categoryId = "fruits_veg",
      name = "Red Onion (Pyaaz)",
      hindiName = "लाल प्याज",
      brand = "Rewa Mandi Fresh",
      description = "Crisp, pungent red onions, handpicked for daily cooking, curries and tadka.",
      packSize = "1 kg",
      price = 36.0,
      mrp = 45.0,
      discountPercent = 20,
      isFeatured = true,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1618512496248-a07fe83aa8cb?w=500&auto=format&fit=crop&q=80",
      badgeText = "Bestseller"
    ),
    ProductEntity(
      id = "p_tomato",
      categoryId = "fruits_veg",
      name = "Hybrid Juicy Tomato (Tamatar)",
      hindiName = "ताज़ा लाल टमाटर",
      brand = "Rewa Mandi Fresh",
      description = "Plump, ripe red tomatoes ideal for rich Indian gravies, salads and chutneys.",
      packSize = "1 kg",
      price = 32.0,
      mrp = 45.0,
      discountPercent = 28,
      isFeatured = true,
      isDealOfTheDay = true,
      imageUrl = "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=500&auto=format&fit=crop&q=80",
      badgeText = "28% OFF"
    ),
    ProductEntity(
      id = "p_coriander_chilli",
      categoryId = "fruits_veg",
      name = "Fresh Green Coriander & Chilli Combo",
      hindiName = "हरा धनिया और हरी मिर्च कॉम्बो",
      brand = "Rewa Mandi Fresh",
      description = "Aromatic desi dhaniya leaves with spicy green chillies, freshly plucked.",
      packSize = "100g + 100g",
      price = 22.0,
      mrp = 30.0,
      discountPercent = 26,
      imageUrl = "https://images.unsplash.com/photo-1599818818872-3e5f2cfc3e89?w=500&auto=format&fit=crop&q=80"
    ),
    ProductEntity(
      id = "p_apple",
      categoryId = "fruits_veg",
      name = "Shimla Royal Delicious Apple",
      hindiName = "रॉयल सेब (सेव)",
      brand = "Fresh Orchard",
      description = "Sweet, crunchy and antioxidant rich fresh Himachal royal apples.",
      packSize = "500 g (3-4 pcs)",
      price = 85.0,
      mrp = 110.0,
      discountPercent = 22,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?w=500&auto=format&fit=crop&q=80"
    ),

    // Dairy & Eggs
    ProductEntity(
      id = "p_amul_gold",
      categoryId = "dairy_eggs",
      name = "Amul Gold Full Cream Fresh Milk",
      hindiName = "अमूल गोल्ड फुल क्रीम दूध",
      brand = "Amul",
      description = "Pasteurized full cream milk rich in vitamins and calcium, daily morning essential.",
      packSize = "500 ml",
      price = 33.0,
      mrp = 33.0,
      discountPercent = 0,
      isFeatured = true,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=500&auto=format&fit=crop&q=80",
      badgeText = "Daily Need"
    ),
    ProductEntity(
      id = "p_amul_taaza",
      categoryId = "dairy_eggs",
      name = "Amul Taaza Toned Milk",
      hindiName = "अमूल ताज़ा टोन्ड दूध",
      brand = "Amul",
      description = "Fresh homogenized toned milk with 3.0% fat, perfect for tea, coffee and cereal.",
      packSize = "500 ml",
      price = 27.0,
      mrp = 27.0,
      discountPercent = 0,
      imageUrl = "https://images.unsplash.com/photo-1563636619-e9143da7973b?w=500&auto=format&fit=crop&q=80"
    ),
    ProductEntity(
      id = "p_amul_paneer",
      categoryId = "dairy_eggs",
      name = "Amul Fresh Malai Paneer",
      hindiName = "अमूल मलाई पनीर",
      brand = "Amul",
      description = "Soft, smooth paneer made from fresh milk, rich in protein for delicious curries.",
      packSize = "200 g",
      price = 92.0,
      mrp = 98.0,
      discountPercent = 6,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?w=500&auto=format&fit=crop&q=80"
    ),
    ProductEntity(
      id = "p_amul_butter",
      categoryId = "dairy_eggs",
      name = "Amul Pasteurised Butter",
      hindiName = "अमूल बटर (मक्खन)",
      brand = "Amul",
      description = "Utterly butterly delicious classic salted butter for parathas, toast and pav bhaji.",
      packSize = "100 g",
      price = 56.0,
      mrp = 60.0,
      discountPercent = 7,
      imageUrl = "https://images.unsplash.com/photo-1589985270826-4b7bb135bc9d?w=500&auto=format&fit=crop&q=80"
    ),
    ProductEntity(
      id = "p_farm_eggs",
      categoryId = "dairy_eggs",
      name = "Fresh White Farm Eggs",
      hindiName = "ताज़े सफेद अंडे (क्रेट/ट्रे)",
      brand = "Local Poultries Rewa",
      description = "Clean, graded farm fresh protein rich eggs directly supplied to store daily.",
      packSize = "6 Pcs Pack",
      price = 48.0,
      mrp = 60.0,
      discountPercent = 20,
      isDealOfTheDay = true,
      imageUrl = "https://images.unsplash.com/photo-1506976785307-8732e854ad03?w=500&auto=format&fit=crop&q=80",
      badgeText = "20% OFF"
    ),

    // Atta, Rice & Grains
    ProductEntity(
      id = "p_aashirvaad_atta",
      categoryId = "atta_rice",
      name = "Aashirvaad Shudh Chakki Atta",
      hindiName = "आशीर्वाद शुद्ध चक्की आटा (100% साबुत गेहूं)",
      brand = "Aashirvaad",
      description = "Made with high quality MP Sehore wheat grains, ensures extra soft and fluffy rotis.",
      packSize = "5 kg",
      price = 245.0,
      mrp = 285.0,
      discountPercent = 14,
      isFeatured = true,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=500&auto=format&fit=crop&q=80",
      badgeText = "Bestseller"
    ),
    ProductEntity(
      id = "p_fortune_basmati",
      categoryId = "atta_rice",
      name = "Fortune Everyday Full Grain Basmati Rice",
      hindiName = "फॉर्च्यून बासमती चावल",
      brand = "Fortune",
      description = "Aromatic, long grain basmati rice suitable for daily khichdi, pulao and steamed rice.",
      packSize = "1 kg",
      price = 98.0,
      mrp = 135.0,
      discountPercent = 27,
      isDealOfTheDay = true,
      imageUrl = "https://images.unsplash.com/photo-1586201375761-83865001e31c?w=500&auto=format&fit=crop&q=80",
      badgeText = "27% OFF"
    ),
    ProductEntity(
      id = "p_suji_rawa",
      categoryId = "atta_rice",
      name = "Rajdhani Fine Sooji / Rawa",
      hindiName = "राजधानी बारीक सूजी / रवा",
      brand = "Rajdhani",
      description = "High fibre semolina for crispy upma, halwa and south Indian dosa batter.",
      packSize = "500 g",
      price = 34.0,
      mrp = 40.0,
      discountPercent = 15,
      imageUrl = "https://images.unsplash.com/photo-1627735483794-912239f61b0c?w=500&auto=format&fit=crop&q=80"
    ),
    ProductEntity(
      id = "p_poha",
      categoryId = "atta_rice",
      name = "Madhya Pradesh Thick Poha (Flaked Rice)",
      hindiName = "एमपी का स्वादिष्ट मोटा पोहा",
      brand = "Rewa Swad",
      description = "Clean, light and healthy breakfast staple favorite across Madhya Pradesh.",
      packSize = "1 kg",
      price = 62.0,
      mrp = 75.0,
      discountPercent = 17,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1614777986387-015c2a89b696?w=500&auto=format&fit=crop&q=80"
    ),

    // Pulses & Dal
    ProductEntity(
      id = "p_toor_dal",
      categoryId = "dal_pulses",
      name = "Tata Sampann Unpolished Toor Dal (Arhar)",
      hindiName = "टाटा सम्पन्न अरहर / तुअर दाल (अनपॉलिश्ड)",
      brand = "Tata Sampann",
      description = "Naturally protein-rich, unpolished lentils with no added color or chemicals.",
      packSize = "1 kg",
      price = 168.0,
      mrp = 195.0,
      discountPercent = 14,
      isFeatured = true,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1585994192701-f1a505c817ea?w=500&auto=format&fit=crop&q=80"
    ),
    ProductEntity(
      id = "p_moong_dal",
      categoryId = "dal_pulses",
      name = "Tata Sampann Yellow Moong Dal Dhuli",
      hindiName = "मूंग दाल धुली (पीली मूंग)",
      brand = "Tata Sampann",
      description = "Easy to digest, light and nutritious yellow split lentils for dal tadka and khichdi.",
      packSize = "500 g",
      price = 82.0,
      mrp = 98.0,
      discountPercent = 16,
      imageUrl = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=500&auto=format&fit=crop&q=80"
    ),
    ProductEntity(
      id = "p_chana_desi",
      categoryId = "dal_pulses",
      name = "Desi Brown Chana (Kala Chana)",
      hindiName = "देसी काला चना",
      brand = "Rewa Agro",
      description = "High fiber chickpeas for morning sprouts, dry prasad chana and spicy curries.",
      packSize = "1 kg",
      price = 88.0,
      mrp = 110.0,
      discountPercent = 20,
      imageUrl = "https://images.unsplash.com/photo-1515543237350-b3eea1ec8082?w=500&auto=format&fit=crop&q=80"
    ),

    // Cooking Oil & Ghee
    ProductEntity(
      id = "p_fortune_mustard",
      categoryId = "oil_ghee",
      name = "Fortune Kachi Ghani Pure Mustard Oil (Sarson)",
      hindiName = "फॉर्च्यून कच्ची घानी सरसों का तेल",
      brand = "Fortune",
      description = "Cold pressed pungent pure mustard oil with natural aroma, preferred in Rewa households.",
      packSize = "1 L Pouch",
      price = 142.0,
      mrp = 175.0,
      discountPercent = 18,
      isFeatured = true,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500&auto=format&fit=crop&q=80",
      badgeText = "Bestseller"
    ),
    ProductEntity(
      id = "p_amul_ghee",
      categoryId = "oil_ghee",
      name = "Amul Pure Cow Ghee (Desi Ghee)",
      hindiName = "अमूल शुद्ध गाय का देसी घी",
      brand = "Amul",
      description = "Rich granular texture and traditional festive aroma made from pure milk fat.",
      packSize = "1 L Tin",
      price = 585.0,
      mrp = 650.0,
      discountPercent = 10,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1628088062854-d1870b4553da?w=500&auto=format&fit=crop&q=80"
    ),
    ProductEntity(
      id = "p_fortune_sunflower",
      categoryId = "oil_ghee",
      name = "Fortune Sunlite Refined Sunflower Oil",
      hindiName = "फॉर्च्यून रिफाइंड सनफ्लावर ऑइल",
      brand = "Fortune",
      description = "Light and healthy cooking oil enriched with vitamins A & D, easy to digest.",
      packSize = "1 L Pouch",
      price = 138.0,
      mrp = 160.0,
      discountPercent = 13,
      imageUrl = "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?w=500&auto=format&fit=crop&q=80"
    ),

    // Spices & Masala
    ProductEntity(
      id = "p_everest_garam",
      categoryId = "spices_masala",
      name = "Everest Garam Masala Powder",
      hindiName = "एवरेस्ट गरम मसाला",
      brand = "Everest",
      description = "Master blend of 13 roasted spices giving rich aroma and authentic flavor to dishes.",
      packSize = "100 g",
      price = 84.0,
      mrp = 95.0,
      discountPercent = 11,
      imageUrl = "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=500&auto=format&fit=crop&q=80"
    ),
    ProductEntity(
      id = "p_tata_salt",
      categoryId = "spices_masala",
      name = "Tata Salt Vacuum Evaporated Iodized Salt",
      hindiName = "टाटा नमक - देश का नमक",
      brand = "Tata",
      description = "Pure vacuum evaporated iodized table salt with proper iodine assurance.",
      packSize = "1 kg",
      price = 28.0,
      mrp = 28.0,
      discountPercent = 0,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?w=500&auto=format&fit=crop&q=80",
      badgeText = "Daily Essential"
    ),
    ProductEntity(
      id = "p_catch_turmeric",
      categoryId = "spices_masala",
      name = "Catch Pure Turmeric Powder (Haldi)",
      hindiName = "कैच शुद्ध हल्दी पाउडर",
      brand = "Catch",
      description = "Low temperature ground turmeric with high natural curcumin content.",
      packSize = "200 g",
      price = 58.0,
      mrp = 68.0,
      discountPercent = 14,
      imageUrl = "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=500&auto=format&fit=crop&q=80"
    ),

    // Snacks & Namkeen
    ProductEntity(
      id = "p_haldiram_bhujia",
      categoryId = "snacks",
      name = "Haldiram's Nagpur Aloo Bhujia",
      hindiName = "हल्दीराम आलू भुजिया",
      brand = "Haldiram's",
      description = "Crispy, spicy potato and gram flour sev flavored with mint and Indian spices.",
      packSize = "400 g",
      price = 105.0,
      mrp = 130.0,
      discountPercent = 19,
      isFeatured = true,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1566478989037-eec170784d0b?w=500&auto=format&fit=crop&q=80"
    ),
    ProductEntity(
      id = "p_lays_magic",
      categoryId = "snacks",
      name = "Lay's India's Magic Masala Potato Chips",
      hindiName = "लेय्स मैजिक मसाला चिप्स",
      brand = "Lay's",
      description = "Thinly sliced crunchy potato chips tossed in blend of exotic spices.",
      packSize = "73 g Party Pack",
      price = 30.0,
      mrp = 30.0,
      discountPercent = 0,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1566478989037-eec170784d0b?w=500&auto=format&fit=crop&q=80"
    ),

    // Biscuits & Bakery
    ProductEntity(
      id = "p_parle_g",
      categoryId = "biscuits_bakery",
      name = "Parle-G Original Glucose Biscuits",
      hindiName = "पारले-जी ग्लूकोज बिस्कुट",
      brand = "Parle",
      description = "India's beloved glucose biscuit filled with nutrition of wheat and milk.",
      packSize = "250 g Family Pack",
      price = 25.0,
      mrp = 25.0,
      discountPercent = 0,
      isFeatured = true,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=500&auto=format&fit=crop&q=80",
      badgeText = "All Time Fav"
    ),
    ProductEntity(
      id = "p_britannia_goodday",
      categoryId = "biscuits_bakery",
      name = "Britannia Good Day Butter Cookies",
      hindiName = "ब्रिटानिया गुड डे बटर कुकीज",
      brand = "Britannia",
      description = "Rich buttery crunch with delightful smile design, perfect with evening tea.",
      packSize = "200 g",
      price = 45.0,
      mrp = 55.0,
      discountPercent = 18,
      imageUrl = "https://images.unsplash.com/photo-1558961363-fa8fdf82db35?w=500&auto=format&fit=crop&q=80"
    ),

    // Tea & Coffee
    ProductEntity(
      id = "p_tata_tea_gold",
      categoryId = "tea_coffee",
      name = "Tata Tea Gold Leaf & CTC Black Tea",
      hindiName = "टाटा टी गोल्ड (पत्ती व कड़क दाने)",
      brand = "Tata Tea",
      description = "Exquisite blend of valley grown Assam tea with 15% gently rolled aromatic long leaves.",
      packSize = "500 g",
      price = 275.0,
      mrp = 330.0,
      discountPercent = 16,
      isFeatured = true,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=500&auto=format&fit=crop&q=80",
      badgeText = "Top Seller"
    ),
    ProductEntity(
      id = "p_nescafe_classic",
      categoryId = "tea_coffee",
      name = "Nescafe Classic 100% Pure Instant Coffee",
      hindiName = "नेस्कैफे क्लासिक इंस्टेंट कॉफ़ी",
      brand = "Nescafe",
      description = "Rich and robust coffee aroma crafted from slow roasted quality Arabica and Robusta beans.",
      packSize = "50 g Glass Jar",
      price = 185.0,
      mrp = 210.0,
      discountPercent = 11,
      imageUrl = "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=500&auto=format&fit=crop&q=80"
    ),

    // Beverages
    ProductEntity(
      id = "p_frooti_mango",
      categoryId = "beverages",
      name = "Frooti Real Mango Juice Drink",
      hindiName = "फ्रूटी मैंगो ड्रिंक",
      brand = "Parle Agro",
      description = "Made from real ripe Alphonso and Totapuri mango pulp, refreshing thirst quencher.",
      packSize = "600 ml Bottle",
      price = 40.0,
      mrp = 40.0,
      discountPercent = 0,
      imageUrl = "https://images.unsplash.com/photo-1546173159-315724a31696?w=500&auto=format&fit=crop&q=80"
    ),
    ProductEntity(
      id = "p_coca_cola",
      categoryId = "beverages",
      name = "Coca-Cola Original Refreshing Soft Drink",
      hindiName = "कोका कोला कोल्ड ड्रिंक",
      brand = "Coca-Cola",
      description = "The world's favorite sparkling carbonated soft drink served chilled.",
      packSize = "750 ml Pet Bottle",
      price = 40.0,
      mrp = 40.0,
      discountPercent = 0,
      imageUrl = "https://images.unsplash.com/photo-1554866585-cd94860890b7?w=500&auto=format&fit=crop&q=80"
    ),

    // Instant Food
    ProductEntity(
      id = "p_maggi_noodles",
      categoryId = "instant_food",
      name = "Maggi 2-Minute Masala Instant Noodles",
      hindiName = "मैगी २-मिनट मसाला नूडल्स (४ पैक)",
      brand = "Nestle Maggi",
      description = "Classic instant noodles packed with the irresistible blend of 10 spices and herbs.",
      packSize = "280 g (Pack of 4)",
      price = 56.0,
      mrp = 60.0,
      discountPercent = 7,
      isFeatured = true,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1612927601601-6638404737ce?w=500&auto=format&fit=crop&q=80",
      badgeText = "Quick 2-Min"
    ),

    // Household & Cleaning
    ProductEntity(
      id = "p_surf_excel_quick",
      categoryId = "cleaning",
      name = "Surf Excel Quick Wash Detergent Powder",
      hindiName = "सर्फ एक्सेल क्विक वॉश पाउडर",
      brand = "Surf Excel",
      description = "Tough stain removal in just 1 rinse, safe on hands and colors.",
      packSize = "1 kg",
      price = 145.0,
      mrp = 165.0,
      discountPercent = 12,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1583947215259-38e31be8751f?w=500&auto=format&fit=crop&q=80"
    ),
    ProductEntity(
      id = "p_vim_dishwash",
      categoryId = "cleaning",
      name = "Vim Lemon Dishwash Gel with Power of 100 Lemons",
      hindiName = "विम नींबू डिशवॉश जेल",
      brand = "Vim",
      description = "Effortlessly cuts through stubborn burnt oil grease without scratching vessels.",
      packSize = "500 ml Bottle",
      price = 115.0,
      mrp = 135.0,
      discountPercent = 14,
      imageUrl = "https://images.unsplash.com/photo-1585421514738-01798e348b17?w=500&auto=format&fit=crop&q=80"
    ),

    // Personal Care
    ProductEntity(
      id = "p_dettol_soap",
      categoryId = "personal_care",
      name = "Dettol Original Germ Protection Bathing Soap",
      hindiName = "डेटॉल ओरिजिनल बाथिंग सोप (कॉम्बो पैक)",
      brand = "Dettol",
      description = "Protects from 100 illness causing germs with classic pine fragrance.",
      packSize = "Pack of 4 (75g each)",
      price = 132.0,
      mrp = 160.0,
      discountPercent = 17,
      isPopular = true,
      imageUrl = "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=500&auto=format&fit=crop&q=80"
    ),
    ProductEntity(
      id = "p_colgate_total",
      categoryId = "personal_care",
      name = "Colgate MaxFresh Spicy Red Gel Toothpaste",
      hindiName = "कोलगेट मैक्सफ्रेश रेड जेल टूथपेस्ट",
      brand = "Colgate",
      description = "Cooling crystals give intense burst of minty freshness and cavity protection.",
      packSize = "150 g Saver Pack",
      price = 98.0,
      mrp = 115.0,
      discountPercent = 14,
      imageUrl = "https://images.unsplash.com/photo-1559591937-e10b1a03e1cf?w=500&auto=format&fit=crop&q=80"
    )
  )

  val coupons = listOf(
    CouponEntity(
      code = "WELCOME50",
      description = "Flat ₹50 OFF on your first grocery delivery in Rewa!",
      discountAmount = 50.0,
      minOrderAmount = 249.0
    ),
    CouponEntity(
      code = "REWA20",
      description = "20% OFF up to ₹75 on fresh essentials",
      discountPercent = 20,
      discountAmount = 75.0,
      minOrderAmount = 199.0
    ),
    CouponEntity(
      code = "FREEDEL",
      description = "Free Doorstep Delivery anywhere in Rewa",
      discountAmount = 25.0,
      minOrderAmount = 149.0
    )
  )

  val banners = listOf(
    BannerEntity(
      id = "b_rewa_sale",
      title = "Rewa Dhamaka Sale! 🛒",
      subtitle = "Up to 35% OFF on Dal, Atta, Ghee & Daily Essentials",
      tag = "REWA SPECIAL",
      discountText = "UP TO 35% OFF",
      backgroundHex = "#0D5C3A",
      actionCategory = "atta_rice"
    ),
    BannerEntity(
      id = "b_upi_cashback",
      title = "Prepay via UPI & QR Code ⚡",
      subtitle = "Get flat 2% Instant Cashback credited directly to your Wallet!",
      tag = "2% CASHBACK",
      discountText = "SAVE EXTRA",
      backgroundHex = "#C2410C",
      actionCategory = "fruits_veg"
    ),
    BannerEntity(
      id = "b_mandi_fresh",
      title = "Farm Fresh Rewa Sabzi Mandi 🍅",
      subtitle = "Plucked this morning from farms around Rewa, delivered crisp!",
      tag = "15 MIN DELIVERY",
      discountText = "SUPER FRESH",
      backgroundHex = "#047857",
      actionCategory = "fruits_veg"
    )
  )

  val demoUser = UserEntity(
    id = 1,
    name = "Rajesh Patel",
    email = "rajesh.patel@rewa.in",
    phone = "+91 94251 88320",
    role = UserRole.CUSTOMER,
    walletBalance = 75.0, // Initial balance with welcome bonus
    isVerified = true
  )

  val initialWalletTransactions = listOf(
    WalletTransactionEntity(
      amount = 50.0,
      isCredit = true,
      title = "Welcome Reward Bonus 🎉",
      description = "Welcome to Ghar Tak Grocery Rewa!",
      orderNumber = "WELCOME"
    ),
    WalletTransactionEntity(
      amount = 25.0,
      isCredit = true,
      title = "2% Prepaid UPI Cashback",
      description = "Cashback for prepaid order #GTG-REW-1002",
      orderNumber = "GTG-REW-1002"
    )
  )
}
