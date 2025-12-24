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
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.border
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(createHeaderGradient())
            .padding(vertical = UiConstants.PADDING_SMALL)
    ) {
        HeaderContent(passwordHash = state.passwordHash)
        UpdateTimeBadge(lastUpdateTime = state.lastUpdateTime)
    }
}

@Composable
private fun HeaderContent(passwordHash: String) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var clickCount by remember { mutableStateOf(0) }
    var lastClickTime by remember { mutableStateOf(0L) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    
    // Interaction source for removing ripple
    val interactionSource = remember { MutableInteractionSource() }

    if (showPasswordDialog) {
        PasswordDialog(
            passwordHash = passwordHash,
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
fun BoxScope.UpdateTimeBadge(lastUpdateTime: String) {
    Row(
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(horizontal = UiConstants.PADDING_LARGE),
        horizontalArrangement = Arrangement.End
    ) {
        Row(
            modifier = Modifier
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
                text = lastUpdateTime.ifEmpty { UiConstants.LOADING_TEXT },
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
    passwordHash: String,
    onDismiss: () -> Unit,
    onUnlock: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var timeLeft by remember { mutableStateOf(60) }
    var isLoading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current

    val verifyPassword = {
        scope.launch {
            isLoading = true
            errorText = null
            
            val delayTime = Random.nextLong(800, 2000)
            delay(delayTime)
            
            isLoading = false
            if (sha256(password) == passwordHash) {
                onUnlock()
            } else {
                errorText = "Mật khẩu không đúng"
            }
        }
    }

    // Timer logic
    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
             delay(1000L)
             timeLeft--
        }
        
        if (isLoading) {
            while (isLoading) {
                delay(200L)
            }
            delay(3000L)
        }
        
        onDismiss()
    }

    // Custom Dialog implementation
    androidx.compose.ui.window.Dialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        properties = androidx.compose.ui.window.DialogProperties(
            decorFitsSystemWindows = false, // Allow dialog to go behind keyboard
            usePlatformDefaultWidth = false // Allow full width control
        )
    ) {
        // Container that handles IME padding and centers the content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp) // Outer margin
                .imePadding(), // Pad bottom by keyboard height
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.material3.Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f) // Use 90% of available width associated with proper platform width if we had set usePlatformDefaultWidth=true, but here we set false so we limit it manually. 0.9f of screen width is reasonable.
                    .wrapContentHeight(),
                shape = RoundedCornerShape(28.dp),
                color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()), // Make content scrollable if it still overflows
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = "Nhập mật khẩu quản trị",
                        style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Error Message
                    if (errorText != null) {
                        Text(
                            text = errorText!!,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.error,
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Content
                    TextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            errorText = null
                        },
                        label = { Text("Mật khẩu") },
                        singleLine = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                        isError = errorText != null,
                        enabled = !isLoading,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
                            autoCorrect = false,
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onDone = { 
                                keyboardController?.hide()
                                verifyPassword()
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (timeLeft < 60) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tự động đóng sau ${timeLeft}s",
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        // Dismiss Button
                        if (!isLoading) {
                            val interactionSource = remember { MutableInteractionSource() }
                            val isFocused by interactionSource.collectIsFocusedAsState()

                            androidx.compose.material3.TextButton(
                                onClick = onDismiss,
                                interactionSource = interactionSource,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .scale(if (isFocused) 1.1f else 1.0f)
                            ) {
                                Text(
                                    "Hủy",
                                    color = if (isFocused) androidx.compose.material3.MaterialTheme.colorScheme.error else androidx.compose.material3.MaterialTheme.colorScheme.primary,
                                    fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }

                        // Confirm Button
                        val interactionSource = remember { MutableInteractionSource() }
                        val isFocused by interactionSource.collectIsFocusedAsState()
                        
                        Button(
                            onClick = { verifyPassword() },
                            enabled = !isLoading,
                            interactionSource = interactionSource,
                            shape = RoundedCornerShape(8.dp),
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = if (isFocused) Color(0xFF4CAF50) else androidx.compose.material3.MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .scale(if (isFocused) 1.15f else 1.0f)
                                .then(
                                    if (isFocused) {
                                        Modifier.drawBehind {
                                            val strokeWidth = 3.dp.toPx()
                                            val y = size.height - strokeWidth / 2
                                            drawLine(
                                                color = Color.White,
                                                start = Offset(0f, y),
                                                end = Offset(size.width, y),
                                                strokeWidth = strokeWidth
                                            )
                                        }
                                    } else Modifier
                                )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Xác nhận",
                                    fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun sha256(input: String): String {
    val bytes = input.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}
