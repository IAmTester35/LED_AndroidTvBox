package com.reecotech.androidtvbox.ui.screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.reecotech.androidtvbox.R
import com.reecotech.androidtvbox.ui.viewmodel.MainUiState
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Shadow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlinx.coroutines.delay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.imePadding
import kotlinx.coroutines.launch
import kotlin.random.Random
import java.security.MessageDigest
/**
 * Header section with title, logo, and last update time
 */
@Composable
fun HeaderSection(state: MainUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(createHeaderGradient())
            .padding(vertical = UiConstants.PADDING_SMALL),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderContent()
    }
}

@Composable
private fun HeaderContent() {
    val context = androidx.compose.ui.platform.LocalContext.current
    var clickCount by remember { mutableStateOf(0) }
    var lastClickTime by remember { mutableStateOf(0L) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    
    // Interaction source for removing ripple
    val interactionSource = remember { MutableInteractionSource() }

    if (showPasswordDialog) {
        PasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onUnlock = {
                showPasswordDialog = false
                try {
                    val intent = android.content.Intent(android.provider.Settings.ACTION_SETTINGS)
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (e: Exception) {
                    android.widget.Toast.makeText(context, "Cannot open Settings", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = UiConstants.PADDING_LARGE),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_government),
            contentDescription = "Government Logo",
            modifier = Modifier
                .size(UiConstants.LOGO_GOVERNMENT_SIZE)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null // Remove visual ripple
                ) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - lastClickTime > 2000) {
                        clickCount = 0
                    }
                    clickCount++
                    lastClickTime = currentTime

                    if (clickCount >= 5) {
                        clickCount = 0
                        showPasswordDialog = true
                    }
                }
        )
        Spacer(modifier = Modifier.width(UiConstants.SPACING_LARGE))
        
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = UiConstants.HEADER_TITLE,
                color = Color(0xFFFF0000),
                fontSize = UiConstants.FONT_SIZE_HEADER_TITLE,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )
            Text(
                text = UiConstants.HEADER_SUBTITLE,
                color = Color(0xFFFFE75E),
                fontSize = UiConstants.FONT_SIZE_HEADER_SUBTITLE,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )
        }
    }
}

@Composable
fun UpdateTimeBadge(lastUpdateTime: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = UiConstants.PADDING_LARGE),
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            modifier = Modifier
                .padding(top = UiConstants.PADDING_SMALL)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(UiConstants.CORNER_RADIUS_UPDATE_BADGE),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )
                .background(
                    color = Color(0xFFD7F9FF),
                    shape = RoundedCornerShape(UiConstants.CORNER_RADIUS_UPDATE_BADGE)
                )
                .padding(
                    horizontal = UiConstants.PADDING_SMALL,
                    vertical = UiConstants.SPACING_TINY
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ClockIcon(
                size = 11.dp,
                color = UiConstants.BADGE_TEXT
            )
            Spacer(modifier = Modifier.width(UiConstants.SPACING_SMALL))
            Text(
                text = "${UiConstants.UPDATE_TIME_PREFIX}${lastUpdateTime.ifEmpty { UiConstants.LOADING_TEXT }}",
                color = UiConstants.BADGE_TEXT,
                fontSize = UiConstants.FONT_SIZE_SMALL,
                fontWeight = FontWeight.Bold,
                lineHeight = UiConstants.LINE_HEIGHT_SMALL
            )
        }
    }
}

@Composable
private fun ClockIcon(
    size: androidx.compose.ui.unit.Dp,
    color: Color
) {
    Canvas(modifier = Modifier.size(size)) {
        val canvasSize = this.size.minDimension
        val center = Offset(canvasSize / 2, canvasSize / 2)
        val radius = canvasSize / 2
        
        // Draw clock circle
        drawCircle(
            color = color,
            radius = radius,
            center = center,
            style = Stroke(width = radius * 0.15f)
        )
        
        // Draw hour hand (pointing to 10 o'clock)
        val hourAngle = Math.toRadians(-60.0) // 10 o'clock position
        val hourHandLength = radius * 0.5f
        drawLine(
            color = color,
            start = center,
            end = Offset(
                center.x + (hourHandLength * cos(hourAngle)).toFloat(),
                center.y + (hourHandLength * sin(hourAngle)).toFloat()
            ),
            strokeWidth = radius * 0.12f,
            cap = StrokeCap.Round
        )
        
        // Draw minute hand (pointing to 2 o'clock)
        val minuteAngle = Math.toRadians(60.0) // 2 o'clock position
        val minuteHandLength = radius * 0.7f
        drawLine(
            color = color,
            start = center,
            end = Offset(
                center.x + (minuteHandLength * cos(minuteAngle)).toFloat(),
                center.y + (minuteHandLength * sin(minuteAngle)).toFloat()
            ),
            strokeWidth = radius * 0.12f,
            cap = StrokeCap.Round
        )
        
        // Draw center dot
        drawCircle(
            color = color,
            radius = radius * 0.1f,
            center = center
        )
    }
}

private fun createHeaderGradient() = Brush.linearGradient(
    colorStops = arrayOf(
        UiConstants.HEADER_GRADIENT_START_OFFSET to UiConstants.HEADER_GRADIENT_START,
        UiConstants.HEADER_GRADIENT_END_OFFSET to UiConstants.HEADER_GRADIENT_END
    ),
)

@Composable
fun PasswordDialog(
    onDismiss: () -> Unit,
    onUnlock: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(60) } // 60 seconds timeout
    var isLoading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    // Timer logic
    LaunchedEffect(Unit) {
        while (timeLeft > 0 && !isLoading) { // Pause timer while loading or keep counting? Let's keep counting but maybe slower? Or just simple count.
             delay(1000L)
             timeLeft--
        }
        if (timeLeft <= 0) {
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text(text = "Nhập mật khẩu quản trị") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()) // Make scrollable for landscape/keyboard
                    .imePadding() // Push up when keyboard opens
            ) {
                TextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        errorText = null // Clear error on type
                    },
                    label = { Text("Mật khẩu") },
                    singleLine = true,
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    isError = errorText != null,
                    enabled = !isLoading
                )
                
                if (errorText != null) {
                    Text(
                        text = errorText!!,
                        color = Color.Red,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (timeLeft < 60) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tự động đóng sau ${timeLeft}s",
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorText = null
                        
                        // Fake loading 1-3 seconds
                        val delayTime = Random.nextLong(1000, 3000)
                        delay(delayTime)
                        
                        isLoading = false
                        if (sha256(password) == "99edc2b391da70f08d8aed876b0c2bb1e976bcaff860abc0f29dcd45fd09d1dc") {
                            onUnlock()
                        } else {
                            errorText = "Mật khẩu không đúng"
                        }
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Xác nhận")
                }
            }
        },
        dismissButton = {
            if (!isLoading) {
                Button(onClick = onDismiss) {
                    Text("Hủy")
                }
            }
        }
    )
}

private fun sha256(input: String): String {
    val bytes = input.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}
