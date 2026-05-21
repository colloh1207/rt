package com.sdd.marketplace.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sdd.marketplace.domain.model.User
import com.sdd.marketplace.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isAuthenticated: Boolean = false,
    val otpSent: Boolean = false,
    val phone: String = "",
    val needsEmailVerification: Boolean = false,
    val successMessage: String? = null
)

sealed class AuthEvent {
    data class ShowError(val message: String) : AuthEvent()
    object NavigateToHome : AuthEvent()
    object NavigateToOtp : AuthEvent()
    data class ShowSuccess(val message: String) : AuthEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            authRepository.isAuthenticated.collect { isAuth ->
                _uiState.update { it.copy(isAuthenticated = isAuth) }
            }
        }
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }
    }

    fun signInWithEmail(email: String, password: String) = viewModelScope.launch {
        if (email.isBlank()) { _uiState.update { it.copy(error = "Please enter your email") }; return@launch }
        if (password.isBlank()) { _uiState.update { it.copy(error = "Please enter your password") }; return@launch }
        _uiState.update { it.copy(isLoading = true, error = null) }
        authRepository.signInWithEmail(email, password)
            .onSuccess { _events.emit(AuthEvent.NavigateToHome) }
            .onFailure { e ->
                val msg = when {
                    e.message?.contains("Invalid login credentials") == true -> "Invalid email or password. Please try again."
                    e.message?.contains("Email not confirmed") == true -> "Please verify your email before signing in."
                    e.message?.contains("network") == true -> "Network error. Please check your connection."
                    else -> e.message ?: "Sign in failed. Please try again."
                }
                _uiState.update { it.copy(error = msg) }
            }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun signInWithPhone(phone: String) = viewModelScope.launch {
        if (phone.isBlank()) { _uiState.update { it.copy(error = "Please enter your phone number") }; return@launch }
        _uiState.update { it.copy(isLoading = true, error = null, phone = phone) }
        authRepository.signInWithPhone(phone)
            .onSuccess { _events.emit(AuthEvent.NavigateToOtp) }
            .onFailure { e ->
                _uiState.update { it.copy(error = e.message ?: "Failed to send OTP. Please try again.") }
            }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun verifyOtp(otp: String) = viewModelScope.launch {
        val phone = _uiState.value.phone
        if (otp.isBlank()) { _uiState.update { it.copy(error = "Please enter the OTP") }; return@launch }
        _uiState.update { it.copy(isLoading = true, error = null) }
        authRepository.verifyOtp(phone, otp)
            .onSuccess { _events.emit(AuthEvent.NavigateToHome) }
            .onFailure { e ->
                val msg = when {
                    e.message?.contains("Token has expired") == true -> "OTP has expired. Please request a new one."
                    e.message?.contains("invalid") == true -> "Invalid OTP. Please check and try again."
                    else -> e.message ?: "OTP verification failed."
                }
                _uiState.update { it.copy(error = msg) }
            }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun signUp(fullName: String, email: String, phone: String, password: String) = viewModelScope.launch {
        if (fullName.isBlank()) { _uiState.update { it.copy(error = "Please enter your full name") }; return@launch }
        if (email.isBlank()) { _uiState.update { it.copy(error = "Please enter your email") }; return@launch }
        if (password.length < 6) { _uiState.update { it.copy(error = "Password must be at least 6 characters") }; return@launch }
        _uiState.update { it.copy(isLoading = true, error = null) }
        authRepository.signUpWithEmail(fullName, email, phone, password)
            .onSuccess { user ->
                if (user.id == "pending_verification") {
                    _uiState.update { it.copy(needsEmailVerification = true, successMessage = "Account created! Please check your email to verify your account.") }
                    _events.emit(AuthEvent.ShowSuccess("Please check your email to verify your account, then sign in."))
                } else {
                    _events.emit(AuthEvent.NavigateToHome)
                }
            }
            .onFailure { e ->
                val msg = when {
                    e.message?.contains("already registered") == true -> "An account with this email already exists. Please sign in."
                    e.message?.contains("invalid email") == true -> "Please enter a valid email address."
                    e.message?.contains("password") == true -> "Password is too weak. Please use at least 6 characters."
                    else -> e.message ?: "Registration failed. Please try again."
                }
                _uiState.update { it.copy(error = msg) }
            }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun signUpWithPhone(fullName: String, phone: String) = viewModelScope.launch {
        if (fullName.isBlank()) { _uiState.update { it.copy(error = "Please enter your full name") }; return@launch }
        if (phone.isBlank()) { _uiState.update { it.copy(error = "Please enter your phone number") }; return@launch }
        _uiState.update { it.copy(isLoading = true, error = null, phone = phone) }
        authRepository.signUpWithPhone(fullName, phone)
            .onSuccess { _events.emit(AuthEvent.NavigateToOtp) }
            .onFailure { e ->
                _uiState.update { it.copy(error = e.message ?: "Failed to send OTP. Please try again.") }
            }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun continueAsGuest() = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, error = null) }
        authRepository.signInAnonymously()
            .onSuccess { _events.emit(AuthEvent.NavigateToHome) }
            .onFailure { _events.emit(AuthEvent.NavigateToHome) }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun sendPasswordReset(email: String) = viewModelScope.launch {
        if (email.isBlank()) { _uiState.update { it.copy(error = "Please enter your email") }; return@launch }
        _uiState.update { it.copy(isLoading = true, error = null) }
        authRepository.sendPasswordResetEmail(email)
            .onSuccess { _uiState.update { s -> s.copy(otpSent = true, successMessage = "Reset link sent! Check your email.") } }
            .onFailure { _uiState.update { s -> s.copy(error = it.message) } }
        _uiState.update { it.copy(isLoading = false) }
    }

    fun signOut() = viewModelScope.launch { authRepository.signOut() }
    fun clearError() = _uiState.update { it.copy(error = null) }
    fun clearSuccess() = _uiState.update { it.copy(successMessage = null, needsEmailVerification = false) }
}
