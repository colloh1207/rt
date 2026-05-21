package com.sdd.marketplace.feature.settings.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sdd.marketplace.core.navigation.Screen
import com.sdd.marketplace.core.ui.components.SddButton
import com.sdd.marketplace.core.ui.theme.SddPink
import com.sdd.marketplace.feature.settings.viewmodel.SettingsEvent
import com.sdd.marketplace.feature.settings.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwitchAccountScreen(navController: NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } },
                title = { Text("Switch Account", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = SddPink.copy(alpha = 0.08f))) {
                Row(Modifier.padding(16.dp)) {
                    Icon(Icons.Filled.Info, "Info", tint = SddPink)
                    Spacer(Modifier.width(12.dp))
                    Text("You can have up to 2 accounts on this device. Add a second account to easily switch between them.", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("Current Account", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Card(shape = RoundedCornerShape(12.dp)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(model = null, contentDescription = null, modifier = Modifier.size(48.dp).clip(CircleShape))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Current User", fontWeight = FontWeight.SemiBold)
                        Text("Active", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.Filled.Check, "Active", tint = SddPink)
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("Add Account", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Card(shape = RoundedCornerShape(12.dp)) {
                Row(
                    Modifier.fillMaxWidth().clickable { navController.navigate(Screen.Login.route) }.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.size(48.dp).clip(CircleShape).then(Modifier), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.AddCircleOutline, "Add", tint = SddPink, modifier = Modifier.size(32.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Add another account", fontWeight = FontWeight.Medium, color = SddPink)
                        Text("Sign in with a different email or phone", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("Note: Each device supports a maximum of 2 accounts. Creating more than 2 accounts per device is not permitted.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
