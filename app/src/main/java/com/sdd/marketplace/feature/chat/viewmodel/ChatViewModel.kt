package com.sdd.marketplace.feature.chat.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdd.marketplace.domain.model.*
import com.sdd.marketplace.domain.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InboxUiState(
    val chats: List<Chat> = emptyList(),
    val filteredChats: List<Chat> = emptyList(),
    val unreadCount: Int = 0,
    val searchQuery: String = "",
    val selectedFilter: String = "All",
    val isLoading: Boolean = true,
    val error: String? = null
)

data class ChatUiState(
    val chat: Chat? = null,
    val messages: List<Message> = emptyList(),
    val currentUserId: String = "",
    val messageText: String = "",
    val isTyping: Boolean = false,
    val partnerTyping: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class InboxViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val blockRepository: BlockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InboxUiState())
    val uiState: StateFlow<InboxUiState> = _uiState.asStateFlow()

    init { loadChats() }

    private fun loadChats() = viewModelScope.launch {
        chatRepository.getChats().collect { chats ->
            _uiState.update { it.copy(chats = chats, filteredChats = chats, isLoading = false) }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) state.chats
            else state.chats.filter { chat ->
                chat.participants.any { it.fullName.contains(query, ignoreCase = true) } ||
                chat.lastMessage?.content?.contains(query, ignoreCase = true) == true
            }
            state.copy(searchQuery = query, filteredChats = filtered)
        }
    }

    fun setFilter(filter: String) {
        _uiState.update { state ->
            val filtered = when (filter) {
                "Unread" -> state.chats.filter { it.unreadCount > 0 }
                "Orders" -> state.chats.filter { it.productId != null }
                else -> state.chats
            }
            state.copy(selectedFilter = filter, filteredChats = filtered)
        }
    }

    fun blockUser(userId: String) = viewModelScope.launch {
        blockRepository.blockUser(userId, null)
            .onSuccess { loadChats() }
    }

    fun reportUser(userId: String, category: ReportCategory, description: String) = viewModelScope.launch {
        blockRepository.reportUser(userId, category, description, emptyList())
    }
}

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val blockRepository: BlockRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val chatId: String = savedStateHandle["chatId"] ?: ""

    private val _uiState = MutableStateFlow(ChatUiState(currentUserId = authRepository.getCurrentUserId() ?: ""))
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _typingDebounce = MutableStateFlow(false)

    init {
        if (chatId.isNotBlank()) {
            loadMessages()
            loadChat()
            observeTyping()
            markRead()
        }
        observeTypingDebounce()
    }

    private fun loadChat() = viewModelScope.launch {
        chatRepository.getChat(chatId).collect { chat ->
            _uiState.update { it.copy(chat = chat) }
        }
    }

    private fun loadMessages() = viewModelScope.launch {
        chatRepository.getMessages(chatId).collect { messages ->
            _uiState.update { it.copy(messages = messages, isLoading = false) }
        }
    }

    private fun observeTyping() = viewModelScope.launch {
        chatRepository.observeTypingStatus(chatId).collect { typingUserId ->
            val isPartnerTyping = typingUserId != null && typingUserId != _uiState.value.currentUserId
            _uiState.update { it.copy(partnerTyping = isPartnerTyping) }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeTypingDebounce() = viewModelScope.launch {
        _typingDebounce.debounce(1000).collect { isTyping ->
            if (!isTyping) chatRepository.sendTypingIndicator(chatId, false)
        }
    }

    private fun markRead() = viewModelScope.launch { chatRepository.markChatRead(chatId) }

    fun onMessageTextChanged(text: String) {
        _uiState.update { it.copy(messageText = text) }
        viewModelScope.launch {
            chatRepository.sendTypingIndicator(chatId, text.isNotBlank())
            _typingDebounce.value = text.isNotBlank()
        }
    }

    fun sendMessage() = viewModelScope.launch {
        val text = _uiState.value.messageText.trim()
        if (text.isBlank()) return@launch
        _uiState.update { it.copy(messageText = "") }
        chatRepository.sendMessage(chatId, text)
            .onFailure { _uiState.update { s -> s.copy(error = it.message) } }
    }

    fun sendLocation(lat: Double, lng: Double, address: String) = viewModelScope.launch {
        chatRepository.sendLocationMessage(chatId, lat, lng, address)
            .onFailure { _uiState.update { s -> s.copy(error = it.message) } }
    }

    fun sendImage() = viewModelScope.launch {
        // Image sending via gallery is handled by the screen
    }

    fun blockUser(userId: String) = viewModelScope.launch {
        blockRepository.blockUser(userId, null)
    }

    fun reportUser(userId: String, category: ReportCategory, description: String) = viewModelScope.launch {
        blockRepository.reportUser(userId, category, description, emptyList())
    }
}
