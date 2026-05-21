package com.sdd.marketplace.feature.settings.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sdd.marketplace.core.ui.theme.SddPink

data class LanguageOption(val code: String, val name: String, val nativeName: String, val flag: String)

@Composable
fun ChangeLanguageScreen(navController: NavController) {
    var selectedLanguage by remember { mutableStateOf("en") }
    val languages = listOf(
        LanguageOption("en", "English", "English", "🇬🇧"),
        LanguageOption("hi", "Hindi", "हिन्दी", "🇮🇳"),
        LanguageOption("ta", "Tamil", "தமிழ்", "🇮🇳"),
        LanguageOption("te", "Telugu", "తెలుగు", "🇮🇳"),
        LanguageOption("kn", "Kannada", "ಕನ್ನಡ", "🇮🇳"),
        LanguageOption("ml", "Malayalam", "മലയാളം", "🇮🇳"),
        LanguageOption("mr", "Marathi", "मराठी", "🇮🇳"),
        LanguageOption("gu", "Gujarati", "ગુજરાતી", "🇮🇳"),
        LanguageOption("pa", "Punjabi", "ਪੰਜਾਬੀ", "🇮🇳"),
        LanguageOption("bn", "Bengali", "বাংলা", "🇮🇳")
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Change Language", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item {
                Card(Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Icon(Icons.Filled.Language, "Language", tint = SddPink)
                        Spacer(Modifier.height(8.dp))
                        Text("Select your preferred language for the app interface.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            items(languages) { lang ->
                Row(
                    Modifier.fillMaxWidth().clickable { selectedLanguage = lang.code }.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(lang.flag, style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(lang.name, fontWeight = FontWeight.Medium)
                        Text(lang.nativeName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    RadioButton(selected = selectedLanguage == lang.code, onClick = { selectedLanguage = lang.code }, colors = RadioButtonDefaults.colors(selectedColor = SddPink))
                }
                Divider(Modifier.padding(horizontal = 16.dp))
            }
            item {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SddPink)
                ) { Text("Apply Language") }
            }
        }
    }
}
