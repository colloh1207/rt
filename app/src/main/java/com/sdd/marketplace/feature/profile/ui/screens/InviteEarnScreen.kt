package com.sdd.marketplace.feature.profile.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sdd.marketplace.core.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteEarnScreen(navController: NavController) {
    val context = LocalContext.current
    val referralCode = "SDD-XY7K2"
    var showCopied by remember { mutableStateOf(false) }

    val steps = listOf(
        Triple(Icons.Outlined.Share, "Share Your Code", "Share your unique code with friends & family"),
        Triple(Icons.Outlined.PersonAdd, "Friend Signs Up", "They sign up using your referral code"),
        Triple(Icons.Outlined.EmojiEvents, "Both Earn Rewards", "You both get ₹100 wallet credit!")
    )

    val referralHistory = listOf(
        Triple("Priya Sharma", "Joined 3 days ago", "₹100"),
        Triple("Rahul Verma", "Joined 1 week ago", "₹100"),
        Triple("Sneha Patel", "Joined 2 weeks ago", "₹100")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } },
                title = { Text("Invite & Earn", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding)) {
            item {
                Box(
                    Modifier.fillMaxWidth().background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(listOf(SddPink.copy(0.12f), MaterialTheme.colorScheme.surface))
                    ).padding(32.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Box(Modifier.size(100.dp).clip(CircleShape).background(SddPink.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Text("🎁", fontSize = 50.sp)
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("Invite Friends, Earn ₹100!", fontWeight = FontWeight.Bold, fontSize = 22.sp, textAlign = TextAlign.Center)
                        Spacer(Modifier.height(8.dp))
                        Text("For every friend who joins Sdd using your referral code, both of you get ₹100 wallet credit!", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(24.dp))

                        Row(Modifier.fillMaxWidth(0.85f).border(2.dp, SddPink, RoundedCornerShape(16.dp)).clip(RoundedCornerShape(16.dp)).background(SddPink.copy(alpha = 0.05f)).padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Your Referral Code", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(referralCode, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = SddPink, letterSpacing = 2.sp)
                            }
                            IconButton(onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Referral Code", referralCode))
                                showCopied = true
                            }) {
                                Icon(if (showCopied) Icons.Filled.CheckCircle else Icons.Outlined.ContentCopy, "Copy", tint = SddPink)
                            }
                        }
                        if (showCopied) {
                            Spacer(Modifier.height(4.dp))
                            Text("Copied to clipboard!", color = SuccessGreen, style = MaterialTheme.typography.labelSmall)
                        }
                        Spacer(Modifier.height(20.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, "Join Sdd Marketplace - India's best marketplace! Use my code $referralCode to get ₹100 wallet credit. Download now: https://sddmarket.app")
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share via"))
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SddPink), shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.Share, "Share", tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Share Invite Link", color = Color.White)
                            }
                        }
                    }
                }
            }

            item {
                Card(Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(20.dp)) {
                        Text("How it Works", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(16.dp))
                        steps.forEachIndexed { index, (icon, title, desc) ->
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(Modifier.size(40.dp).clip(CircleShape).background(SddPink), contentAlignment = Alignment.Center) {
                                        Text("${index + 1}", color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    if (index < steps.size - 1) {
                                        Box(Modifier.width(2.dp).height(32.dp).background(SddPink.copy(alpha = 0.3f)))
                                    }
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f).padding(top = 10.dp)) {
                                    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                    Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(20.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("My Referrals", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Surface(color = SddPink.copy(alpha = 0.1f), shape = RoundedCornerShape(20.dp)) {
                                Text("${referralHistory.size} friends", color = SddPink, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${referralHistory.size}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = SddPink)
                                Text("Invited", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("₹${referralHistory.size * 100}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = SuccessGreen)
                                Text("Earned", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("₹${referralHistory.size * 100}", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = SddPink)
                                Text("Credited", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            if (referralHistory.isNotEmpty()) {
                item {
                    Column(Modifier.padding(horizontal = 16.dp)) {
                        Text("Referral History", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp))
                        referralHistory.forEach { (name, date, reward) ->
                            ListItem(
                                headlineContent = { Text(name, fontWeight = FontWeight.Medium) },
                                supportingContent = { Text(date, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                leadingContent = {
                                    Box(Modifier.size(40.dp).clip(CircleShape).background(SddPink.copy(0.15f)), contentAlignment = Alignment.Center) {
                                        Text(name.first().toString(), fontWeight = FontWeight.Bold, color = SddPink)
                                    }
                                },
                                trailingContent = { Text(reward, color = SuccessGreen, fontWeight = FontWeight.Bold) }
                            )
                            Divider(Modifier.padding(horizontal = 56.dp))
                        }
                    }
                }
            }

            item {
                Card(Modifier.fillMaxWidth().padding(16.dp), colors = CardDefaults.cardColors(containerColor = SddPink.copy(alpha = 0.05f)), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Terms & Conditions", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        listOf("Referral credit is valid for 90 days from the date of credit", "Only new users who haven't signed up before are eligible", "Both you and your friend must complete profile setup to earn rewards", "Maximum 50 referrals per account per month", "Sdd reserves the right to modify or cancel the referral program at any time").forEach { term ->
                            Row(Modifier.padding(vertical = 2.dp)) {
                                Text("• ", fontSize = 12.sp, color = SddPink)
                                Text(term, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
