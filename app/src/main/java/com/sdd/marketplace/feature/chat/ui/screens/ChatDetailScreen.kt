package com.sdd.marketplace.feature.chat.ui.screens

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.sdd.marketplace.core.navigation.Screen
import com.sdd.marketplace.core.ui.components.VerifiedBadge
import com.sdd.marketplace.core.ui.theme.*
import com.sdd.marketplace.domain.model.*
import com.sdd.marketplace.feature.chat.viewmodel.ChatDetailViewModel

@Composable
fun ChatDetailScreen(navController: NavController, viewModel: ChatDetailViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var showMenu by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showAttachmentMenu by remember { mutableStateOf(false) }

    val otherUser = uiState.chat?.participants?.firstOrNull { it.id != uiState.currentUserId }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) listState.animateScrollToItem(uiState.messages.size - 1)
    }

    if (showBlockDialog) {
        otherUser?.let { user ->
            AlertDialog(
                onDismissRequest = { showBlockDialog = false },
                icon = { Icon(Icons.Filled.Block, "Block", tint = MaterialTheme.colorScheme.error) },
                title = { Text("Block ${user.fullName}?") },
                text = { Text("They won't be able to message you or see your listings. This will also end this conversation.") },
                confirmButton = {
                    Button(onClick = { viewModel.blockUser(user.id); showBlockDialog = false; navController.popBackStack() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Block") }
                },
                dismissButton = { TextButton(onClick = { showBlockDialog = false }) { Text("Cancel") } }
            )
        }
    }

    if (showReportDialog) {
        otherUser?.let { user ->
            ReportUserDialog(
                userId = user.id,
                onReport = { cat, desc -> viewModel.reportUser(user.id, cat, desc); showReportDialog = false },
                onDismiss = { showReportDialog = false }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Filled.ArrowBack, "Back") } },
                title = {
                    Row(Modifier.clickable(onClick = { otherUser?.id?.let { navController.navigate(Screen.Profile.createRoute(it)) } }), verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(model = otherUser?.avatarUrl, contentDescription = null, modifier = Modifier.size(36.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(otherUser?.fullName ?: "Chat", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                if (otherUser?.isVerified == true) { Spacer(Modifier.width(4.dp)); VerifiedBadge() }
                            }
                            Text(
                                if (uiState.partnerTyping) "Typing..." else if (otherUser?.isOnline == true) "Online" else "Offline",
                                color = if (otherUser?.isOnline == true) OnlineGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Outlined.Phone, "Call", tint = SddPink) }
                    Box {
                        IconButton(onClick = { showMenu = true }) { Icon(Icons.Filled.MoreVert, "More") }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(leadingIcon = { Icon(Icons.Filled.Person, "Profile") }, text = { Text("View Profile") },
                                onClick = { showMenu = false; otherUser?.id?.let { navController.navigate(Screen.Profile.createRoute(it)) } })
                            DropdownMenuItem(leadingIcon = { Icon(Icons.Filled.Block, "Block", tint = MaterialTheme.colorScheme.error) },
                                text = { Text("Block User", color = MaterialTheme.colorScheme.error) }, onClick = { showMenu = false; showBlockDialog = true })
                            DropdownMenuItem(leadingIcon = { Icon(Icons.Filled.Flag, "Report", tint = MaterialTheme.colorScheme.error) },
                                text = { Text("Report User", color = MaterialTheme.colorScheme.error) }, onClick = { showMenu = false; showReportDialog = true })
                            DropdownMenuItem(leadingIcon = { Icon(Icons.Outlined.Notifications, "Mute") }, text = { Text("Mute Notifications") }, onClick = { showMenu = false })
                            DropdownMenuItem(leadingIcon = { Icon(Icons.Outlined.Delete, "Delete", tint = MaterialTheme.colorScheme.error) },
                                text = { Text("Delete Chat", color = MaterialTheme.colorScheme.error) }, onClick = { showMenu = false })
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column {
                // Product attachment indicator
                uiState.chat?.product?.let { product ->
                    Surface(color = SddPink.copy(alpha = 0.1f)) {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.ShoppingBag, "Product", tint = SddPink, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("About: ${product.title}", fontSize = 12.sp, color = SddPink, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f), maxLines = 1)
                            Text("₹${String.format("%.0f", product.price)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SddPink)
                        }
                    }
                }
                if (showAttachmentMenu) {
                    Surface(tonalElevation = 4.dp) {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            AttachmentOption(Icons.Outlined.Image, "Gallery") { viewModel.sendImage(); showAttachmentMenu = false }
                            AttachmentOption(Icons.Outlined.CameraAlt, "Camera") { showAttachmentMenu = false }
                            AttachmentOption(Icons.Outlined.LocationOn, "Location") { viewModel.sendLocation(28.6139, 77.2090, "New Delhi"); showAttachmentMenu = false }
                            AttachmentOption(Icons.Outlined.AttachFile, "File") { showAttachmentMenu = false }
                            AttachmentOption(Icons.Outlined.ShoppingBag, "Product") { navController.navigate(Screen.Search.route); showAttachmentMenu = false }
                        }
                    }
                }
                ChatInputBar(
                    messageText = uiState.messageText,
                    onMessageChanged = { viewModel.onMessageTextChanged(it) },
                    onSend = { viewModel.sendMessage() },
                    onAttach = { showAttachmentMenu = !showAttachmentMenu },
                    onLocation = { viewModel.sendLocation(28.6139, 77.2090, "Connaught Place, New Delhi") }
                )
            }
        }
    ) { padding ->
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { Spacer(Modifier.height(8.dp)) }
            items(uiState.messages, key = { it.id }) { message ->
                MessageBubble(message = message, isMine = message.senderId == uiState.currentUserId)
            }
            if (uiState.partnerTyping) { item { TypingIndicator() } }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
fun AttachmentOption(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick).padding(8.dp)) {
        Box(Modifier.size(48.dp).clip(CircleShape).background(SddPink.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(icon, label, tint = SddPink, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun MessageBubble(message: Message, isMine: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start) {
        Column(horizontalAlignment = if (isMine) Alignment.End else Alignment.Start, modifier = Modifier.widthIn(max = 280.dp)) {
            when (message.type) {
                MessageType.LOCATION -> LocationMessageBubble(message, isMine)
                MessageType.IMAGE -> ImageMessageBubble(message, isMine)
                else -> TextMessageBubble(message, isMine)
            }
        }
    }
}

@Composable
fun TextMessageBubble(message: Message, isMine: Boolean) {
    Box(modifier = Modifier.background(
        color = if (isMine) SddPink else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = if (isMine) 16.dp else 4.dp, bottomEnd = if (isMine) 4.dp else 16.dp)
    ).padding(horizontal = 12.dp, vertical = 8.dp)) {
        Column {
            Text(message.content, color = if (isMine) Color.White else MaterialTheme.colorScheme.onSurface)
            Row(modifier = Modifier.align(Alignment.End), verticalAlignment = Alignment.CenterVertically) {
                Text(message.sentAt.takeLast(5), fontSize = 10.sp, color = if (isMine) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant)
                if (isMine) { Spacer(Modifier.width(2.dp)); Icon(if (message.isRead) Icons.Filled.DoneAll else Icons.Filled.Done, "Read", tint = if (message.isRead) Color.Cyan else Color.White.copy(alpha = 0.7f), modifier = Modifier.size(12.dp)) }
            }
        }
    }
}

@Composable
fun ImageMessageBubble(message: Message, isMine: Boolean) {
    Box(modifier = Modifier.background(if (isMine) SddPink else MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)).padding(4.dp)) {
        AsyncImage(model = message.imageUrl, contentDescription = "Image", modifier = Modifier.size(200.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
    }
}

@Composable
fun LocationMessageBubble(message: Message, isMine: Boolean) {
    val lat = message.latitude ?: 28.6139; val lng = message.longitude ?: 77.2090
    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.width(240.dp)) {
        Column {
            Row(Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, "Location", tint = SddPink)
                Text("Location", fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(1f))
                Text(message.sentAt.takeLast(5), fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Box(Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(0.dp))) {
                GoogleMap(modifier = Modifier.fillMaxSize(),
                    cameraPositionState = rememberCameraPositionState { position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(LatLng(lat, lng), 14f) },
                    uiSettings = MapUiSettings(zoomControlsEnabled = false, scrollGesturesEnabled = false, zoomGesturesEnabled = false)) {
                    Marker(state = MarkerState(position = LatLng(lat, lng)))
                }
            }
            Column(Modifier.padding(8.dp)) {
                Text(message.locationAddress ?: "Location", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text("Open in Maps", color = SddPink, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)).padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(3) { i ->
            val infiniteTransition = rememberInfiniteTransition(label = "typing$i")
            val alpha by infiniteTransition.animateFloat(initialValue = 0.3f, targetValue = 1f,
                animationSpec = infiniteRepeatable(animation = androidx.compose.animation.core.tween(500, delayMillis = i * 150),
                    repeatMode = androidx.compose.animation.core.RepeatMode.Reverse), label = "alpha$i")
            Box(Modifier.size(6.dp).clip(CircleShape).background(SddPink.copy(alpha = alpha)))
        }
    }
}

@Composable
fun ChatInputBar(messageText: String, onMessageChanged: (String) -> Unit, onSend: () -> Unit, onAttach: () -> Unit, onLocation: () -> Unit) {
    Surface(shadowElevation = 8.dp) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onAttach) { Icon(Icons.Filled.Add, "Attach", tint = SddPink) }
            OutlinedTextField(value = messageText, onValueChange = onMessageChanged, modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") }, singleLine = true, maxLines = 3,
                shape = RoundedCornerShape(25.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = SddPink))
            Spacer(Modifier.width(4.dp))
            IconButton(onClick = if (messageText.isBlank()) onLocation else onSend,
                modifier = Modifier.size(48.dp).clip(CircleShape).background(SddPink)) {
                Icon(Icons.Filled.Send, "Send", tint = Color.White)
            }
        }
    }
}
