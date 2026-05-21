package com.sdd.marketplace.domain.model

data class User(
    val id: String,
    val fullName: String,
    val email: String?,
    val phone: String?,
    val avatarUrl: String?,
    val bio: String?,
    val isVerified: Boolean,
    val isSeller: Boolean,
    val rating: Double,
    val reviewCount: Int,
    val followerCount: Int,
    val followingCount: Int,
    val productCount: Int,
    val soldCount: Int,
    val responseRate: Int,
    val location: String?,
    val joinedAt: String,
    val isOnline: Boolean,
    val lastSeen: String?,
    val kycStatus: String = "not_submitted",
    val isBlocked: Boolean = false,
    val accountAge: Long = 0L,
    val deviceId: String? = null,
    val preferredLanguage: String = "en"
)
