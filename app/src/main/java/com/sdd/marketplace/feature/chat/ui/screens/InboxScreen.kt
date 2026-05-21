package com.sdd.marketplace.feature.chat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.sdd.marketplace.core.navigation.Screen
import com.sdd.marketplace.core.ui.components.*
import com.sdd.marketplace.core.ui.theme.*
import com.sdd.marketplace.domain.model.*
import com.sdd.marketplace.feature.chat.viewmodel.InboxViewModel

@Composable
fun InboxScreen(navController: NavController, viewModel: InboxViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var showBlockConfirm by remember { mutableStateOf<String?>(null) }
    var showReportSheet by remember { mutableStateOf<String?>(null) }

    showBlockConfirm?.let { userId ->
        AlertDialog(
            onDismissRequest = { showBlockConfirm = null },
            icon = { Icon(Icons.Filled.Block, "Block", tint = MaterialTheme.colorScheme.error) },
            title = { Text("Block User?") },
            text = { Text("They won't be able to message you or see your listings. You can unblock them in Settings.") },
            confirmButton = {
                Button(
                    onClick = { viewModel.blockUser(userId); showBlockConfirm = null },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Block") }
            },
            dismissButton = { TextButton(onClick = { showBlockConfirm = null }) { Text("Cancel") } }
        )
    }

    showReportSheet?.let { userId ->
        ReportUserDialog(userId = userId, onReport = { cat, desc -> viewModel.reportUser(userId, cat, desc); showReportSheet = null }, onDismiss = { showReportSheet = null })
    }

    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = { Text("Inbox", fontWeight = FontWeight.Bold) },
            actions = {
                IconButton(onClick = { }) { Icon(Icons.Outlined.Search, "Search") }
                IconButton(onClick = { }) { Icon(Icons.Outlined.FilterList, "Filter") }
            }
        )
        OutlinedTextField(
            value = uiState.searchQuery, onValueChange = { viewModel.onSearchQueryChanged(it) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            placeholder = { Text("Search messages or users...") },
            leadingIcon = { Icon(Icons.Outlined.Search, "Search") },
            singleLine = true, shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant, focusedBorderColor = SddPink)
        )
        val filters = listOf("All", "Unread", "Orders", "Offers")
        ScrollableTabRow(selectedTabIndex = filters.indexOf(uiState.selectedFilter), edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.background, contentColor = SddPink) {
            filters.forEach { filter ->
                Tab(selected = uiState.selectedFilter == filter, onClick = { viewModel.setFilter(filter) }, text = { Text(filter) })
            }
        }
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = SddPink) }
        } else if (uiState.filteredChats.isEmpty()) {
            EmptyState("No messages yet", "Start a conversation with a seller")
        } else {
            LazyColumn {
                items(uiState.filteredChats, key = { it.id }) { chat ->
                    val otherUser = chat.participants.firstOrNull()
                    ChatListItem(
                        chat = chat,
                        onClick = { navController.navigate(Screen.ChatDetail.createRoute(chat.id)) },
                        onBlock = { otherUser?.id?.let { showBlockConfirm = it } },
                        onReport = { otherUser?.id?.let { showReportSheet = it } }
                    )
                    Divider(Modifier.padding(horizontal = 72.dp))
                }
            }
        }
    }
}

@Composable
fun ChatListItem(chat: Chat, onClick: () -> Unit, onBlock: () -> Unit, onReport: () -> Unit) {
    val otherUser = chat.participants.firstOrNull()
    var showMenu by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box {
            AsyncImage(model = otherUser?.avatarUrl, contentDescription = otherUser?.fullName,
                modifier = Modifier.size(52.dp).clip(CircleShape), contentScale = ContentScale.Crop)
            if (otherUser?.isOnline == true) {
                OnlineIndicator(true, Modifier.align(Alignment.BottomEnd).offset(x = 2.dp, y = 2.dp))
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(otherUser?.fullName ?: "User", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    if (otherUser?.isVerified == true) { Spacer(Modifier.width(4.dp)); VerifiedBadge() }
                }
                Text(chat.lastMessage?.sentAt?.take(5) ?: "", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    chat.lastMessage?.content ?: "No messages yet",
                    fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f)
                )
                if (chat.unreadCount > 0) {
                    Box(Modifier.size(20.dp).clip(CircleShape).background(SddPink), contentAlignment = Alignment.Center) {
                        Text("${chat.unreadCount}", color = androidx.compose.ui.graphics.Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Box {
            IconButton(onClick = { showMenu = true }, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Filled.MoreVert, "More", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(leadingIcon = { Icon(Icons.Filled.Block, "Block", tint = MaterialTheme.colorScheme.error) },
                    text = { Text("Block User", color = MaterialTheme.colorScheme.error) },
                    onClick = { showMenu = false; onBlock() })
                DropdownMenuItem(leadingIcon = { Icon(Icons.Filled.Flag, "Report", tint = MaterialTheme.colorScheme.error) },
                    text = { Text("Report User", color = MaterialTheme.colorScheme.error) },
                    onClick = { showMenu = false; onReport() })
                DropdownMenuItem(leadingIcon = { Icon(Icons.Filled.Archive, "Archive") }, text = { Text("Archive") }, onClick = { showMenu = false })
                DropdownMenuItem(leadingIcon = { Icon(Icons.Filled.Delete, "Delete", tint = MaterialTheme.colorScheme.error) },
                    text = { Text("Delete Chat", color = MaterialTheme.colorScheme.error) }, onClick = { showMenu = false })
            }
        }
    }
}

@Composable
fun ReportUserDialog(userId: String, onReport: (ReportCategory, String) -> Unit, onDismiss: () -> Unit) {
    var selectedCategory by remember { mutableStateOf(ReportCategory.SPAM) }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.Flag, "Report", tint = MaterialTheme.colorScheme.error) },
        title = { Text("Report User") },
        text = {
            Column {
                Text("Select reason:", fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(8.dp))
                ReportCategory.values().forEach { cat ->
                    Row(Modifier.clickable { selectedCategory = cat }.padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedCategory == cat, onClick = { selectedCategory = cat }, colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.error))
                        Text(cat.label, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(description, { description = it }, label = { Text("Additional details (optional)") },
                    modifier = Modifier.fillMaxWidth(), minLines = 2, maxLines = 4)
            }
        },
        confirmButton = {
            Button(onClick = { onReport(selectedCategory, description) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Submit Report") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
