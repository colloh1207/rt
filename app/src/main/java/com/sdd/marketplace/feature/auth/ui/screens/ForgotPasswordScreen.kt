package com.sdd.marketplace.feature.auth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sdd.marketplace.core.ui.components.*
import com.sdd.marketplace.core.ui.theme.SddLightPink
import com.sdd.marketplace.core.ui.theme.SddPink
import com.sdd.marketplace.feature.auth.viewmodel.AuthViewModel

@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(SddLightPink).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Row(Modifier.fillMaxWidth()) {
            IconButton(onClick = onNavigateBack) { Icon(Icons.Filled.ArrowBack, "Back") }
        }
        Spacer(Modifier.height(32.dp))
        SddLogo(size = 60.dp)
        Spacer(Modifier.height(24.dp))

        if (uiState.otpSent) {
            Text("OTP Sent!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = SddPink)
            Text("Check your email for the reset link", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            SddButton("Back to Login", onClick = onNavigateBack)
        } else {
            Text("Forgot Password?", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Enter your email or phone number and we'll send you a password reset OTP",
                textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(24.dp)) {
                    SddTextField(
                        value = email, onValueChange = { email = it },
                        label = "Email or Phone Number",
                        leadingIcon = { Icon(Icons.Outlined.Email, "Email") }
                    )
                    Spacer(Modifier.height(20.dp))
                    SddButton("Send OTP", onClick = { viewModel.sendPasswordReset(email) }, isLoading = uiState.isLoading)
                    uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp)) }
                }
            }
        }
    }
}
