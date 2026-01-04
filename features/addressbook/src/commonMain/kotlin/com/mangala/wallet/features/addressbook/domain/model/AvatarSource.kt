package com.mangala.wallet.features.addressbook.domain.model

sealed class AvatarSource {
    object None : AvatarSource()
    data class Emoji(val emoji: String, val backgroundColor: String? = null) : AvatarSource()
    data class ImageUrl(val url: String) : AvatarSource()
    data class DefaultAvatar(val resourceName: String) : AvatarSource()

    companion object {
        // Chuyển AvatarSource thành string để lưu vào entity
        fun toString(source: AvatarSource): String? = when(source) {
            is None -> null
            is Emoji -> if (source.backgroundColor != null) {
                "emoji:${source.emoji}:${source.backgroundColor}"
            } else {
                "emoji:${source.emoji}"
            }
            is ImageUrl -> source.url
            is DefaultAvatar -> "default:${source.resourceName}"
        }

        // Tạo AvatarSource từ string
        fun fromString(value: String?): AvatarSource = when {
            value.isNullOrEmpty() -> None
            value.startsWith("emoji:") -> {
                val parts = value.substring(6).split(":")
                if (parts.size >= 2) {
                    Emoji(parts[0], parts[1])
                } else {
                    Emoji(parts[0])
                }
            }
            value.startsWith("default:") -> DefaultAvatar(value.substring(8))
            else -> ImageUrl(value)
        }
    }
}