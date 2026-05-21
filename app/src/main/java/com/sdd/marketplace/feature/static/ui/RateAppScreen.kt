package com.sdd.marketplace.feature.static.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sdd.marketplace.core.ui.theme.SddPink
import com.sdd.marketplace.core.ui.theme.StarYellow

@Composable
fun RateAppScreen(navController: NavController) {
    var selectedRating by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    if (submitted) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Favorite, "Thank You", tint = SddPink, modifier = Modifier.size(80.dp))
            Spacer(Modifier.height(24.dp))
            Text("Thank You!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Your rating helps us improve the app.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(32.dp))
            Button(onClick = { navController.popBackStack() }, colors = ButtonDefaults.buttonColors(containerColor = SddPink)) { Text("Back to Settings") }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rate App", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(32.dp))
            Icon(Icons.Filled.ShoppingBag, "App", tint = SddPink, modifier = Modifier.size(80.dp))
            Spacer(Modifier.height(16.dp))
            Text("Enjoying SDD Marketplace?", fontSize = 22.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text("Tap a star to rate your experience", color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                (1..5).forEach { star ->
                    IconButton(onClick = { selectedRating = star }, modifier = Modifier.size(56.dp)) {
                        Icon(if (star <= selectedRating) Icons.Filled.Star else Icons.Outlined.StarBorder, "Star $star",
                            tint = if (star <= selectedRating) StarYellow else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(48.dp))
                    }
                }
            }
            if (selectedRating > 0) {
                Spacer(Modifier.height(8.dp))
                Text(listOf("Poor", "Fair", "Good", "Great", "Excellent!")[selectedRating - 1], color = SddPink, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(
                value = feedback, onValueChange = { feedback = it },
                label = { Text("Tell us more (optional)") },
                modifier = Modifier.fillMaxWidth(), minLines = 3, maxLines = 6,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SddPink)
            )
            Spacer(Modifier.height(32.dp))
            Button(
                onClick = { submitted = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedRating > 0,
                colors = ButtonDefaults.buttonColors(containerColor = SddPink)
            ) { Text("Submit Rating") }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { navController.popBackStack() }) { Text("Maybe Later") }
        }
    }
}
