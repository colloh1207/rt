package com.sdd.marketplace.feature.auth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.sdd.marketplace.core.ui.components.SddButton
import com.sdd.marketplace.core.ui.theme.SddLightPink
import com.sdd.marketplace.core.ui.theme.SddPink
import com.sdd.marketplace.feature.auth.viewmodel.AuthEvent
import com.sdd.marketplace.feature.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun OtpVerifyScreen(
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var otp by remember { mutableStateOf("") }
    var resendTimer by remember { mutableIntStateOf(28) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { when (it) { is AuthEvent.NavigateToHome -> onNavigateToHome(); else -> {} } }
    }
    LaunchedEffect(Unit) {
        while (resendTimer > 0) { delay(1000); resendTimer-- }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(SddLightPink).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Verify OTP", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Enter the 6-digit code sent to", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("+91 ${uiState.phone}", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(32.dp))

        OtpInputField(otp = otp, onOtpChanged = { if (it.length <= 6) otp = it })

        Spacer(Modifier.height(24.dp))
        if (resendTimer > 0) {
            Text("Resend OTP in ", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("00:${resendTimer.toString().padStart(2, '0')}", color = SddPink, fontWeight = FontWeight.Bold)
        } else {
            TextButton(onClick = { resendTimer = 28; viewModel.signInWithPhone(uiState.phone) }) {
                Text("Resend OTP", color = SddPink)
            }
        }
        Spacer(Modifier.height(32.dp))
        SddButton(
            text = "Verify & Login",
            onClick = { viewModel.verifyOtp(otp) },
            isLoading = uiState.isLoading,
            enabled = otp.length == 6
        )
        uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
        Spacer(Modifier.height(24.dp))
        TextButton(onClick = { viewModel.continueAsGuest() }) {
            Text("Continue as Guest", color = SddPink)
        }
    }
}

@Composable
fun OtpInputField(otp: String, onOtpChanged: (String) -> Unit) {
    BasicTextField(
        value = otp, onValueChange = onOtpChanged,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(6) { index ->
                    val char = otp.getOrNull(index)
                    Box(
                        modifier = Modifier.size(48.dp).border(
                            2.dp,
                            if (index == otp.length) SddPink else Color.Gray.copy(alpha = 0.4f),
                            RoundedCornerShape(8.dp)
                        ).background(Color.White, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(char?.toString() ?: "", fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    )
}
