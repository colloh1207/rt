package com.sdd.marketplace.feature.moderation.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sdd.marketplace.core.navigation.Screen
import com.sdd.marketplace.core.ui.theme.SddPink
import com.sdd.marketplace.domain.model.*
import com.sdd.marketplace.feature.moderation.viewmodel.ModerationEvent
import com.sdd.marketplace.feature.moderation.viewmodel.SuspensionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuspendedScreen(navController: NavController, viewModel: SuspensionViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ModerationEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                is ModerationEvent.AppealSubmitted -> { /* stay on screen to show submitted state */ }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {

            Spacer(Modifier.height(32.dp))

            val suspension = uiState.suspension
            val isPermanent = suspension?.type == SuspensionType.PERMANENT

            Icon(
                if (isPermanent) Icons.Filled.GppBad else Icons.Filled.Block,
                "Suspended",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(80.dp)
            )

            Text(
                if (isPermanent) "Account Permanently Banned" else "Account Temporarily Suspended",
                fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center
            )

            suspension?.let { s ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Reason", fontWeight = FontWeight.Bold)
                        Text(s.reason)
                        if (!isPermanent) {
                            s.endsAt?.let { Text("Suspension ends: ${it.take(10)}", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                }
            }

            // What you can still do
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("While suspended you can:", fontWeight = FontWeight.Bold)
                    val allowed = listOf("Browse products", "View seller profiles", "Read your messages")
                    val blocked = listOf("Send messages", "Post products", "Change name or profile photo", "Write reviews", "Rate sellers")
                    allowed.forEach { Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, "Allowed", tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp)); Text(it, style = MaterialTheme.typography.bodySmall)
                    } }
                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    Text("You cannot:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    blocked.forEach { Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Cancel, "Blocked", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp)); Text(it, style = MaterialTheme.typography.bodySmall)
                    } }
                }
            }

            // Appeal Section
            val appealStatus = uiState.suspension?.appealStatus
            if (appealStatus == AppealStatus.NONE || appealStatus == null) {
                if (!uiState.appealSubmitted) {
                    HorizontalDivider()
                    Text("Submit an Appeal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Explain why you believe this action was unfair. Our team will review within 24 hours.",
                        textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall)
                    OutlinedTextField(
                        value = uiState.appealNote,
                        onValueChange = { viewModel.onAppealNoteChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Your appeal note") },
                        placeholder = { Text("Explain your case in detail...") },
                        minLines = 4,
                        maxLines = 8,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SddPink)
                    )
                    uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                    Button(
                        onClick = { viewModel.submitAppeal() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = uiState.appealNote.isNotBlank() && !uiState.isSubmittingAppeal,
                        colors = ButtonDefaults.buttonColors(containerColor = SddPink)
                    ) {
                        if (uiState.isSubmittingAppeal) CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        else Text("Submit Appeal")
                    }
                }
            } else if (appealStatus == AppealStatus.PENDING || uiState.appealSubmitted) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.HourglassTop, "Pending", tint = Color(0xFFFF9800))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Appeal Under Review", fontWeight = FontWeight.Bold, color = Color(0xFFFF9800))
                            Text("We'll respond within 24 hours.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } else if (appealStatus == AppealStatus.APPROVED) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CheckCircle, "Approved", tint = Color(0xFF4CAF50))
                            Spacer(Modifier.width(8.dp))
                            Text("Appeal Approved!", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                        }
                        Text("You must complete KYC verification to restore full access.", style = MaterialTheme.typography.bodySmall)
                        Button(onClick = { navController.navigate(Screen.KycVerification.route) },
                            modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = SddPink)) {
                            Text("Complete KYC Verification")
                        }
                    }
                }
            } else if (appealStatus == AppealStatus.REJECTED) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Cancel, "Rejected", tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Appeal Rejected", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            Text("Your appeal was reviewed and denied.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Browse button
            OutlinedButton(onClick = { navController.navigate(Screen.Home.route) }, modifier = Modifier.fillMaxWidth()) {
                Text("Continue Browsing Products")
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
