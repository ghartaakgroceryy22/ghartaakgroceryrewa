package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentConfirmationBottomSheet(
  amountPayable: Double,
  cashbackAmount: Double,
  onDismiss: () -> Unit,
  onConfirmPayment: (utrNumber: String, proofUriString: String) -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()

  var utrNumber by remember { mutableStateOf("") }
  var screenshotUri by remember { mutableStateOf<Uri?>(null) }
  var copiedUpiId by remember { mutableStateOf(false) }
  var isSubmitting by remember { mutableStateOf(false) }

  // Photo Picker launcher for payment screenshot (Zero-permission Android Photo Picker)
  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri: Uri? ->
    if (uri != null) {
      screenshotUri = uri
      Toast.makeText(context, "Payment screenshot attached! ✓", Toast.LENGTH_SHORT).show()
    }
  }

  // Spring scale for confirm button
  val confirmScale = remember { Animatable(1f) }

  ModalBottomSheet(
    onDismissRequest = { if (!isSubmitting) onDismiss() },
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 8.dp,
    dragHandle = {
      BottomSheetDefaults.DragHandle(
        color = GroceryGreenDark.copy(alpha = 0.4f),
        width = 44.dp,
        height = 4.dp
      )
    },
    modifier = Modifier.testTag("payment_confirmation_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .navigationBarsPadding()
        .padding(bottom = 20.dp)
    ) {
      // 1. Header with Security Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Confirm Payment",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 20.sp,
              color = GroceryTextPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = GroceryGreenContainer
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Shield,
                  contentDescription = null,
                  tint = GroceryGreenDark,
                  modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                  text = "VERIFIED",
                  fontSize = 9.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = GroceryGreenDark
                )
              }
            }
          }
          Text(
            text = "Ghar Tak Grocery Rewa Central Hub",
            fontSize = 12.sp,
            color = GroceryTextSecondary
          )
        }

        IconButton(
          onClick = onDismiss,
          enabled = !isSubmitting,
          modifier = Modifier.size(32.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = GroceryTextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 2. Payable Amount & Cashback Card
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF0FDF4),
        border = BorderStroke(1.5.dp, GroceryGreenPrimary.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Total Payable Amount",
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium,
              color = GroceryTextSecondary
            )
            Text(
              text = "₹${amountPayable.toInt()}",
              fontSize = 26.sp,
              fontWeight = FontWeight.ExtraBold,
              color = GroceryGreenDark
            )
          }

          if (cashbackAmount > 0) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = GroceryAmberContainer,
              border = BorderStroke(1.dp, GroceryAmber.copy(alpha = 0.6f))
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text("💰", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                  Text(
                    text = "+₹${cashbackAmount.toInt()} Cashback",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF78350F)
                  )
                  Text(
                    text = "to wallet upon verify",
                    fontSize = 9.sp,
                    color = Color(0xFF92400E)
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 3. Quick UPI Apps Launch Row
      Text(
        text = "Pay using your installed UPI App:",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = GroceryTextPrimary
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        val upiApps = listOf(
          Triple("Google Pay", "gpay", Color(0xFFEA4335)),
          Triple("PhonePe", "phonepe", Color(0xFF5F259F)),
          Triple("Paytm", "paytm", Color(0xFF00B9F5)),
          Triple("BHIM / Other", "bhim", Color(0xFF00796B))
        )

        upiApps.forEach { (name, id, accent) ->
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, GroceryCardBorder),
            modifier = Modifier
              .weight(1f)
              .clickable {
                try {
                  val uri = Uri.parse(
                    "upi://pay?pa=ghartakgrocery@icici&pn=Ghar+Tak+Grocery+Rewa&am=${amountPayable.toInt()}&cu=INR&tn=Rewa+Grocery+Order"
                  )
                  val intent = Intent(Intent.ACTION_VIEW, uri)
                  context.startActivity(intent)
                } catch (e: Exception) {
                  Toast.makeText(
                    context,
                    "Opening $name... If not installed, please use UPI ID or scan QR.",
                    Toast.LENGTH_SHORT
                  ).show()
                }
              }
          ) {
            Column(
              modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .background(accent.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = name.take(1),
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 13.sp,
                  color = accent
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4. Scannable QR & Copyable UPI ID Box
      Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, GroceryCardBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(14.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // QR Frame
            Box(
              modifier = Modifier
                .size(110.dp)
                .background(Color.White, RoundedCornerShape(10.dp))
                .border(2.dp, GroceryGreenPrimary, RoundedCornerShape(10.dp))
                .padding(6.dp),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.QrCode2,
                contentDescription = "Rewa Hub QR Code",
                tint = Color.Black,
                modifier = Modifier.fillMaxSize()
              )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Rewa QR Code",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = GroceryTextPrimary
              )
              Text(
                text = "Scan from phone or secondary screen to pay directly",
                fontSize = 11.sp,
                color = GroceryTextSecondary,
                lineHeight = 15.sp
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "VPA: ghartakgrocery@icici",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GroceryGreenDark
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 1-Tap Copy UPI ID Button
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = GroceryGreenContainer,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "UPI ID: ghartakgrocery@icici",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = GroceryGreenDark
                )
                Text(
                  text = "Ghar Tak Grocery Rewa Dark Store",
                  fontSize = 10.sp,
                  color = GroceryTextSecondary
                )
              }

              Button(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", "ghartakgrocery@icici"))
                  copiedUpiId = true
                  Toast.makeText(context, "UPI ID copied! Paste in your UPI app.", Toast.LENGTH_SHORT).show()
                },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                modifier = Modifier.height(30.dp)
              ) {
                Text(
                  text = if (copiedUpiId) "Copied! ✓" else "Copy",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // 5. Modern Screenshot Upload Section (Native Clean UI)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Upload Payment Screenshot",
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = GroceryTextPrimary
        )
        Text(
          text = "Fast Track Dispatch",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = GroceryGreenDark
        )
      }
      Spacer(modifier = Modifier.height(8.dp))

      if (screenshotUri == null) {
        // Upload Dropzone (Card with dashed border aesthetic)
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = Color(0xFFF9FAFB),
          border = BorderStroke(1.5.dp, GroceryGreenPrimary.copy(alpha = 0.5f)),
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
              )
            }
            .testTag("upload_screenshot_dropzone")
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .background(GroceryGreenContainer, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AddPhotoAlternate,
                contentDescription = "Upload Screenshot",
                tint = GroceryGreenDark,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Tap to Select Payment Screenshot",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold,
              color = GroceryGreenDark
            )
            Text(
              text = "JPG, PNG from Gallery • Zero Permission Photo Picker",
              fontSize = 11.sp,
              color = GroceryTextMuted
            )
          }
        }
      } else {
        // Screenshot Attached Preview Card
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = GroceryGreenContainer.copy(alpha = 0.4f),
          border = BorderStroke(1.5.dp, GroceryGreenPrimary),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            AsyncImage(
              model = ImageRequest.Builder(context)
                .data(screenshotUri)
                .crossfade(true)
                .build(),
              contentDescription = "Uploaded Payment Screenshot",
              contentScale = ContentScale.Crop,
              modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, GroceryGreenDark, RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = GroceryGreenDark,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Screenshot Attached",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.ExtraBold,
                  color = GroceryGreenDark
                )
              }
              Text(
                text = "Ready for store manager verification",
                fontSize = 10.sp,
                color = GroceryTextSecondary
              )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              TextButton(
                onClick = {
                  photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                  )
                },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
              ) {
                Text("Change", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              IconButton(
                onClick = { screenshotUri = null },
                modifier = Modifier.size(28.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Delete,
                  contentDescription = "Remove",
                  tint = Color.Red.copy(alpha = 0.7f),
                  modifier = Modifier.size(16.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // 6. UTR Reference Field (Optional if screenshot provided)
      OutlinedTextField(
        value = utrNumber,
        onValueChange = { utrNumber = it },
        label = { Text("12-Digit UPI Ref / UTR (Optional if screenshot added)") },
        placeholder = { Text("e.g. 428190348123") },
        singleLine = true,
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Tag,
            contentDescription = null,
            tint = GroceryGreenDark,
            modifier = Modifier.size(18.dp)
          )
        },
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = GroceryGreenPrimary,
          focusedLabelColor = GroceryGreenPrimary
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("modal_utr_input")
      )

      Spacer(modifier = Modifier.height(18.dp))

      // 7. Verify & Confirm Button with Spring Physics
      Button(
        onClick = {
          if (!isSubmitting) {
            isSubmitting = true
            coroutineScope.launch {
              // Bouncy spring animation
              confirmScale.animateTo(
                targetValue = 0.92f,
                animationSpec = spring(
                  dampingRatio = Spring.DampingRatioMediumBouncy,
                  stiffness = Spring.StiffnessMediumLow
                )
              )
              confirmScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                  dampingRatio = Spring.DampingRatioMediumBouncy,
                  stiffness = Spring.StiffnessLow
                )
              )
              delay(300)
              val finalUtr = if (utrNumber.isNotBlank()) {
                utrNumber.trim()
              } else if (screenshotUri != null) {
                "PROOF-IMG-${System.currentTimeMillis() % 100000000}"
              } else {
                "UPI-APP-${System.currentTimeMillis() % 100000000}"
              }

              val proofUri = screenshotUri?.toString() ?: "No screenshot attached (Direct UPI)"
              onConfirmPayment(finalUtr, proofUri)
            }
          }
        },
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .scale(confirmScale.value)
          .testTag("confirm_payment_submit_btn")
      ) {
        if (isSubmitting) {
          CircularProgressIndicator(
            color = Color.White,
            modifier = Modifier.size(22.dp),
            strokeWidth = 2.5.dp
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Verifying & Dispatching...",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = Color.White
          )
        } else {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Confirm & Dispatch (₹${amountPayable.toInt()})",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            color = Color.White
          )
        }
      }
    }
  }
}
