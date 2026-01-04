package com.mangala.wallet.features.addressbook.data.model.avatar

import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.time.Duration.Companion.days

/**
 * Avatar History Entity - Domain model for avatar history
 */
data class AvatarHistoryEntity(
    val id: Long = 0,
    val avatarSourceHash: String,
    val avatarSourceType: String,
    val avatarSourceValue: String,
    val firstUsedAt: Long,
    val lastUsedAt: Long,
    val usageCount: Long,
    val isGlobalFavorite: Boolean
) {
    // Computed properties
    val isRecent: Boolean get() = 
        Clock.System.now().toEpochMilliseconds() - lastUsedAt < 7.days.inWholeMilliseconds
    
    val isPopular: Boolean get() = 
        usageCount >= 5 || isGlobalFavorite
    
    val avatarSource: AvatarSource get() {
        println("AvatarHistoryEntity: Converting type='$avatarSourceType', value='$avatarSourceValue'")
        return when (avatarSourceType) {
            "emoji" -> AvatarSource.Emoji(avatarSourceValue)
            "image" -> AvatarSource.ImageUrl(avatarSourceValue)
            "default" -> AvatarSource.DefaultAvatar(avatarSourceValue)
            "none" -> AvatarSource.None
            else -> {
                println("AvatarHistoryEntity: Unknown type '$avatarSourceType', fallback to None")
                AvatarSource.None
            }
        }
    }
    
    companion object {
        /**
         * Create AvatarHistoryEntity from AvatarSource
         */
        fun fromAvatarSource(
            avatarSource: AvatarSource,
            existingEntity: AvatarHistoryEntity? = null
        ): AvatarHistoryEntity {
            val now = Clock.System.now().toEpochMilliseconds()
            val (type, value) = when (avatarSource) {
                is AvatarSource.Emoji -> "emoji" to avatarSource.emoji
                is AvatarSource.ImageUrl -> "image" to avatarSource.url
                is AvatarSource.DefaultAvatar -> "default" to avatarSource.resourceName
                is AvatarSource.None -> "none" to ""
            }
            
            return if (existingEntity != null) {
                // Update existing
                existingEntity.copy(
                    lastUsedAt = now,
                    usageCount = existingEntity.usageCount + 1
                )
            } else {
                // Create new
                AvatarHistoryEntity(
                    avatarSourceHash = avatarSource.toHash(),
                    avatarSourceType = type,
                    avatarSourceValue = value,
                    firstUsedAt = now,
                    lastUsedAt = now,
                    usageCount = 1,
                    isGlobalFavorite = false
                )
            }
        }
    }
}

/**
 * Extensions for AvatarSource
 */
fun AvatarSource.toHash(): String {
    val content = when (this) {
        is AvatarSource.Emoji -> "emoji_$emoji"
        is AvatarSource.ImageUrl -> "image_$url"
        is AvatarSource.DefaultAvatar -> "default_$resourceName"
        is AvatarSource.None -> "none"
    }
    
    return content.toSimpleHash()
}

fun AvatarSource.getType(): String = when (this) {
    is AvatarSource.Emoji -> "emoji"
    is AvatarSource.ImageUrl -> "image"
    is AvatarSource.DefaultAvatar -> "default"
    is AvatarSource.None -> "none"
}

fun AvatarSource.getValue(): String = when (this) {
    is AvatarSource.Emoji -> emoji
    is AvatarSource.ImageUrl -> url
    is AvatarSource.DefaultAvatar -> resourceName
    is AvatarSource.None -> ""
}

/**
 * Utility function to generate simple hash (không dùng MD5 cho cross-platform)
 */
private fun String.toSimpleHash(): String {
    return this.hashCode().toString()
}