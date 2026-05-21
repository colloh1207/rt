package com.sdd.marketplace.feature.auth.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
import com.sdd.marketplace.core.ui.components.*
import com.sdd.marketplace.core.ui.theme.SddLightPink
import com.sdd.marketplace.core.ui.theme.SddPink
import com.sdd.marketplace.feature.auth.viewmodel.AuthEvent
import com.sdd.marketplace.feature.auth.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToForgot: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToOtp: (String) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AuthEvent.NavigateToHome -> onNavigateToHome()
                is AuthEvent.NavigateToOtp -> onNavigateToOtp(uiState.phone)
                is AuthEvent.ShowError -> {}
            }
        }
    }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usePhone by remember { mutableStateOf(false) }
    var phone by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize().background(SddLightPink),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))
            SddLogo()
            Spacer(Modifier.height(24.dp))
            Text("Welcome Back", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Login to continue your shopping journey", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(24.dp)) {
                    if (usePhone) {
                        SddTextField(
                            value = phone, onValueChange = { phone = it },
                            label = "Phone Number",
                            leadingIcon = { Icon(Icons.Outlined.Phone, "Phone") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    } else {
                        SddTextField(
                            value = email, onValueChange = { email = it },
                            label = "Email or Phone Number",
                            leadingIcon = { Icon(Icons.Outlined.Email, "Email") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email)
                        )
                        Spacer(Modifier.height(12.dp))
                        SddTextField(
                            value = password, onValueChange = { password = it },
                            label = "Password", isPassword = true,
                            leadingIcon = { Icon(Icons.Outlined.Lock, "Password") }
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    if (!usePhone) {
                        Text(
                            "Forgot Password?", color = SddPink, fontWeight = FontWeight.Medium,
                            modifier = Modifier.fillMaxWidth().clickable(onClick = onNavigateToForgot),
                            textAlign = TextAlign.End
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    SddButton(
                        text = if (usePhone) "Send OTP" else "Login",
                        onClick = {
                            if (usePhone) viewModel.signInWithPhone(phone)
                            else viewModel.signInWithEmail(email, password)
                        },
                        isLoading = uiState.isLoading
                    )
                    Spacer(Modifier.height(12.dp))
                    TextButton(onClick = { usePhone = !usePhone }, modifier = Modifier.fillMaxWidth()) {
                        Text(if (usePhone) "Use Email Instead" else "Use Phone Instead", color = SddPink)
                    }
                    uiState.error?.let { err ->
                        Spacer(Modifier.height(8.dp))
                        Text(err, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("or continue with", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                SocialLoginButton("G", Color(0xFFDB4437)) { }
                SocialLoginButton("", Color.Black) { }
                SocialLoginButton("f", Color(0xFF4267B2)) { }
            }

            Spacer(Modifier.height(24.dp))
            Row {
                Text("Don't have an account? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Register", color = SddPink, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onNavigateToRegister))
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Continue as Guest",
                color = SddPink, fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { viewModel.continueAsGuest() }
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun SocialLoginButton(text: String, backgroundColor: Color, onClick: () -> Unit) {
    Surface(
        onClick = onClick, shape = RoundedCornerShape(12.dp),
        color = backgroundColor.copy(alpha = 0.1f), tonalElevation = 0.dp,
        modifier = Modifier.size(52.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text, fontWeight = FontWeight.Bold, color = backgroundColor, fontSize = 18.sp)
        }
    }
}
