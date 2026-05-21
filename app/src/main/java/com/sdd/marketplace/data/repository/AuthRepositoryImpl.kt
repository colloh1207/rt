package com.sdd.marketplace.data.repository

import com.sdd.marketplace.data.local.dao.UserDao
import com.sdd.marketplace.data.mappers.toDomain
import com.sdd.marketplace.data.remote.dto.UserDto
import com.sdd.marketplace.domain.model.User
import com.sdd.marketplace.domain.repository.AuthRepository
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.Phone
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: Auth,
    private val postgrest: Postgrest,
    private val userDao: UserDao
) : AuthRepository {

    override val currentUser: Flow<User?> = auth.sessionStatus
        .map { _ ->
            try {
                val session = auth.currentSessionOrNull() ?: return@map null
                val userId = session.user?.id ?: return@map null
                fetchUserProfile(userId)
            } catch (e: Exception) {
                Timber.e(e, "Error getting current user")
                null
            }
        }

    override val isAuthenticated: Flow<Boolean> = auth.sessionStatus
        .map { auth.currentSessionOrNull() != null }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> = runCatching {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        val userId = auth.currentUserOrNull()?.id ?: throw Exception("Authentication failed. Please check your email and password.")
        fetchUserProfile(userId) ?: ensureUserProfile(userId, email = email)
    }

    override suspend fun signInWithPhone(phone: String): Result<Unit> = runCatching {
        auth.signInWith(Phone) { this.phone = phone }
    }

    override suspend fun verifyOtp(phone: String, otp: String): Result<User> = runCatching {
        auth.verifyPhoneOtp(type = OtpType.Phone.SMS, phone = phone, token = otp)
        val userId = auth.currentUserOrNull()?.id
            ?: throw Exception("OTP verification failed. Please try again.")
        fetchUserProfile(userId) ?: ensureUserProfile(userId, phone = phone)
    }

    override suspend fun signUpWithEmail(
        fullName: String, email: String, phone: String, password: String
    ): Result<User> = runCatching {
        val response = auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject {
                put("full_name", fullName)
                put("phone", phone)
            }
        }

        val userId = auth.currentUserOrNull()?.id
            ?: auth.currentSessionOrNull()?.user?.id

        if (userId != null) {
            ensureUserProfile(userId, fullName = fullName, email = email, phone = phone)
        } else {
            User(
                id = "pending_verification",
                fullName = fullName,
                email = email,
                phone = phone,
                avatarUrl = null, bio = null, isVerified = false, isSeller = true,
                rating = 0.0, reviewCount = 0, followerCount = 0, followingCount = 0,
                productCount = 0, soldCount = 0, responseRate = 100, location = null,
                joinedAt = "", isOnline = false, lastSeen = null
            )
        }
    }

    override suspend fun signUpWithPhone(fullName: String, phone: String): Result<Unit> = runCatching {
        auth.signUpWith(Phone) {
            this.phone = phone
            data = buildJsonObject {
                put("full_name", fullName)
            }
        }
    }

    override suspend fun signInAnonymously(): Result<User> = runCatching {
        auth.signInAnonymously()
        val userId = auth.currentUserOrNull()?.id ?: throw Exception("Anonymous sign in failed")
        ensureUserProfile(userId, fullName = "Guest")
    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        auth.signOut()
        userDao.clearAll()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
        auth.resetPasswordForEmail(email)
    }

    override suspend fun updatePassword(newPassword: String): Result<Unit> = runCatching {
        auth.updateUser { password = newPassword }
    }

    override suspend fun deleteAccount(): Result<Unit> = runCatching {
        val userId = getCurrentUserId() ?: throw Exception("Not authenticated")
        postgrest["users"].delete { filter { eq("id", userId) } }
        auth.signOut()
        userDao.clearAll()
    }

    override suspend fun refreshSession(): Result<Unit> = runCatching {
        auth.refreshCurrentSession()
    }

    override fun getCurrentUserId(): String? = auth.currentUserOrNull()?.id

    override fun isGuest(): Boolean {
        val user = auth.currentUserOrNull() ?: return false
        return user.email == null && user.phone == null
    }

    private suspend fun fetchUserProfile(userId: String): User? {
        return try {
            val dto = postgrest["users"].select {
                filter { eq("id", userId) }
            }.decodeSingleOrNull<UserDto>()
            dto?.toDomain()
        } catch (e: Exception) {
            Timber.e(e, "Error fetching user profile")
            null
        }
    }

    private suspend fun ensureUserProfile(
        userId: String,
        fullName: String = "User",
        email: String? = null,
        phone: String? = null
    ): User {
        val existing = fetchUserProfile(userId)
        if (existing != null) return existing

        val username = (email?.substringBefore("@") ?: phone?.takeLast(4) ?: "user").lowercase()
            .replace(Regex("[^a-z0-9_]"), "_")

        val newUser = mapOf(
            "id" to userId,
            "full_name" to fullName,
            "email" to email,
            "phone" to phone,
            "username" to username,
            "is_verified" to false,
            "is_seller" to true,
            "rating" to 0.0,
            "review_count" to 0,
            "follower_count" to 0,
            "following_count" to 0,
            "product_count" to 0,
            "sold_count" to 0,
            "response_rate" to 100
        ).filter { it.value != null }

        try {
            postgrest["users"].upsert(newUser)
        } catch (e: Exception) {
            Timber.e(e, "Error creating user profile")
        }
        return fetchUserProfile(userId) ?: User(
            id = userId, fullName = fullName, email = email, phone = phone,
            avatarUrl = null, bio = null, isVerified = false, isSeller = true,
            rating = 0.0, reviewCount = 0, followerCount = 0, followingCount = 0,
            productCount = 0, soldCount = 0, responseRate = 100, location = null,
            joinedAt = "", isOnline = false, lastSeen = null
        )
    }
}

private fun buildJsonObject(builder: kotlinx.serialization.json.JsonObjectBuilder.() -> Unit) =
    kotlinx.serialization.json.buildJsonObject(builder)

private fun kotlinx.serialization.json.JsonObjectBuilder.put(key: String, value: String?) {
    if (value != null) put(key, kotlinx.serialization.json.JsonPrimitive(value))
}

private fun kotlinx.serialization.json.JsonObjectBuilder.put(key: String, value: Boolean) {
    put(key, kotlinx.serialization.json.JsonPrimitive(value))
}

private fun kotlinx.serialization.json.JsonObjectBuilder.put(key: String, value: Double) {
    put(key, kotlinx.serialization.json.JsonPrimitive(value))
}

private fun kotlinx.serialization.json.JsonObjectBuilder.put(key: String, value: Int) {
    put(key, kotlinx.serialization.json.JsonPrimitive(value))
}
