package com.sdd.marketplace.feature.static.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sdd.marketplace.core.ui.theme.SddPink

@Composable
fun ReportBugScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var stepsToReproduce by remember { mutableStateOf("") }
    var expectedBehavior by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("medium") }
    var submitted by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    if (submitted) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.CheckCircle, "Submitted", tint = com.sdd.marketplace.core.ui.theme.SuccessGreen, modifier = Modifier.size(80.dp))
            Spacer(Modifier.height(24.dp))
            Text("Bug Report Submitted!", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text("Our team will investigate. Thank you!", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(32.dp))
            Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = SddPink)) { Text("Back") }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report a Bug", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp).verticalScroll(scrollState), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Spacer(Modifier.height(8.dp))
            Card(colors = CardDefaults.cardColors(containerColor = SddPink.copy(alpha = 0.08f))) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.BugReport, "Bug", tint = SddPink)
                    Spacer(Modifier.width(8.dp))
                    Text("Help us improve by describing the bug you encountered.", style = MaterialTheme.typography.bodySmall)
                }
            }
            OutlinedTextField(title, { title = it }, label = { Text("Bug Title *") }, modifier = Modifier.fillMaxWidth(),
                isError = title.isBlank(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SddPink))
            // Severity
            Text("Severity", fontWeight = FontWeight.Medium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("low" to "Low", "medium" to "Medium", "high" to "High", "critical" to "Critical").forEach { (key, label) ->
                    FilterChip(
                        selected = severity == key, onClick = { severity = key }, label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = SddPink, selectedLabelColor = androidx.compose.ui.graphics.Color.White)
                    )
                }
            }
            OutlinedTextField(description, { description = it }, label = { Text("What happened? *") }, modifier = Modifier.fillMaxWidth(),
                minLines = 3, isError = description.isBlank(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SddPink))
            OutlinedTextField(stepsToReproduce, { stepsToReproduce = it }, label = { Text("Steps to reproduce (optional)") }, modifier = Modifier.fillMaxWidth(),
                minLines = 2, placeholder = { Text("1. Go to ...\n2. Tap on ...\n3. See error") }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SddPink))
            OutlinedTextField(expectedBehavior, { expectedBehavior = it }, label = { Text("Expected behavior (optional)") }, modifier = Modifier.fillMaxWidth(),
                minLines = 2, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SddPink))
            // Device info (auto-populated)
            Card {
                Column(Modifier.padding(12.dp)) {
                    Text("Device Info (Auto-detected)", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(4.dp))
                    Text("App Version: 1.0.0 • Android ${android.os.Build.VERSION.RELEASE} • ${android.os.Build.MODEL}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Button(
                onClick = { submitted = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && description.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = SddPink)
            ) { Text("Submit Bug Report") }
            Spacer(Modifier.height(16.dp))
        }
    }
}
