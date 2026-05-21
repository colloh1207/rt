package com.sdd.marketplace.domain.repository

import com.sdd.marketplace.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    val isAuthenticated: Flow<Boolean>
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun signInWithPhone(phone: String): Result<Unit>
    suspend fun verifyOtp(phone: String, otp: String): Result<User>
    suspend fun signUpWithEmail(fullName: String, email: String, phone: String, password: String): Result<User>
    suspend fun signUpWithPhone(fullName: String, phone: String): Result<Unit>
    suspend fun signInAnonymously(): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun updatePassword(newPassword: String): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun refreshSession(): Result<Unit>
    fun getCurrentUserId(): String?
    fun isGuest(): Boolean
}
