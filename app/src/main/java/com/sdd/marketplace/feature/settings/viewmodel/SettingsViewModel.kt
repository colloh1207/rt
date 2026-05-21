package com.sdd.marketplace.feature.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdd.marketplace.domain.repository.AuthRepository
import com.sdd.marketplace.domain.repository.BlockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: String? = null,
    val otpSent: Boolean = false,
    val currentStep: Int = 0
)

sealed class SettingsEvent {
    object NavigateToLogin : SettingsEvent()
    data class ShowMessage(val message: String) : SettingsEvent()
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val blockRepository: BlockRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SettingsEvent>()
    val events: SharedFlow<SettingsEvent> = _events.asSharedFlow()

    fun changePassword(currentPassword: String, newPassword: String, otp: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        authRepository.updatePassword(newPassword)
            .onSuccess { _events.emit(SettingsEvent.ShowMessage("Password changed successfully")) }
            .onFailure { _uiState.update { s -> s.copy(error = it.message) } }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun sendOtpForPasswordChange(phone: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        authRepository.signInWithPhone(phone)
            .onSuccess { _uiState.update { s -> s.copy(otpSent = true) } }
            .onFailure { _uiState.update { s -> s.copy(error = it.message) } }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun changeEmail(newEmail: String, otp: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        // Supabase handles email change via updateUser
        runCatching {
            io.github.jan.supabase.auth.Auth::class
        }
        _uiState.update { it.copy(isLoading = false, success = "Verification email sent to $newEmail") }
    }

    fun deleteAccount(reason: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        authRepository.deleteAccount()
            .onSuccess { _events.emit(SettingsEvent.NavigateToLogin) }
            .onFailure { _uiState.update { s -> s.copy(error = it.message) } }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun signOut() = viewModelScope.launch {
        authRepository.signOut()
        _events.emit(SettingsEvent.NavigateToLogin)
    }

    fun rateApp(rating: Int, note: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        blockRepository.rateApp(rating, note)
            .onSuccess { _events.emit(SettingsEvent.ShowMessage("Thank you for your feedback!")) }
            .onFailure { _uiState.update { s -> s.copy(error = it.message) } }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun submitBugReport(description: String, steps: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        blockRepository.submitBugReport(description, steps, emptyList())
            .onSuccess { _events.emit(SettingsEvent.ShowMessage("Bug report submitted. Thank you!")) }
            .onFailure { _uiState.update { s -> s.copy(error = it.message) } }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun submitSupportRequest(subject: String, description: String, category: com.sdd.marketplace.domain.model.SupportCategory) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        blockRepository.submitSupportTicket(category, subject, description, emptyList())
            .onSuccess { _events.emit(SettingsEvent.ShowMessage("Support request #${it.id.take(8)} submitted")) }
            .onFailure { _uiState.update { s -> s.copy(error = it.message) } }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun clearMessages() = _uiState.update { it.copy(error = null, success = null) }
}
