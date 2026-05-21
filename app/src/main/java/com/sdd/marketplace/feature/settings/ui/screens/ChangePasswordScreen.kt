package com.sdd.marketplace.feature.settings.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sdd.marketplace.core.ui.components.*
import com.sdd.marketplace.core.ui.theme.SddPink
import com.sdd.marketplace.feature.settings.viewmodel.SettingsEvent
import com.sdd.marketplace.feature.settings.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsEvent.ShowMessage -> { navController.popBackStack() }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } },
                title = { Text("Change Password", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp)) {
            when (step) {
                0 -> {
                    Text("Verify Your Identity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("We'll send a verification code to your phone number.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(24.dp))
                    SddTextField(phone, { phone = it }, "Phone Number", leadingIcon = { Icon(Icons.Filled.Phone, "Phone") })
                    Spacer(Modifier.height(16.dp))
                    SddButton("Send OTP", onClick = {
                        viewModel.sendOtpForPasswordChange(phone)
                        step = 1
                    }, isLoading = uiState.isLoading)
                }
                1 -> {
                    Text("Enter Verification Code", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Enter the 6-digit code sent to $phone", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(24.dp))
                    SddTextField(otp, { otp = it }, "OTP Code", leadingIcon = { Icon(Icons.Filled.Security, "OTP") })
                    Spacer(Modifier.height(16.dp))
                    SddButton("Verify OTP", onClick = {
                        if (otp.length == 6) step = 2
                    })
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { viewModel.sendOtpForPasswordChange(phone) }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        Text("Resend OTP", color = SddPink)
                    }
                }
                2 -> {
                    Text("Set New Password", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Create a strong password with at least 8 characters.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(24.dp))
                    SddTextField(newPassword, { newPassword = it }, "New Password", isPassword = true, leadingIcon = { Icon(Icons.Filled.Lock, "Password") })
                    Spacer(Modifier.height(12.dp))
                    SddTextField(confirmPassword, { confirmPassword = it }, "Confirm Password", isPassword = true, leadingIcon = { Icon(Icons.Filled.Lock, "Confirm") })
                    if (newPassword.isNotEmpty() && confirmPassword.isNotEmpty() && newPassword != confirmPassword) {
                        Spacer(Modifier.height(4.dp))
                        Text("Passwords do not match", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(16.dp))
                    SddButton(
                        "Change Password",
                        onClick = { if (newPassword == confirmPassword) viewModel.changePassword(currentPassword, newPassword, otp) },
                        isLoading = uiState.isLoading,
                        enabled = newPassword.length >= 8 && newPassword == confirmPassword
                    )
                }
            }
            uiState.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
