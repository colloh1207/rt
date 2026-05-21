package com.sdd.marketplace.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sdd.marketplace.data.local.dao.*
import com.sdd.marketplace.data.local.entities.*

@Database(
    entities = [
        ProductEntity::class,
        UserEntity::class,
        MessageEntity::class,
        ChatEntity::class,
        NotificationEntity::class,
        FavoriteEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SddDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
    abstract fun notificationDao(): NotificationDao
    abstract fun favoriteDao(): FavoriteDao
}
