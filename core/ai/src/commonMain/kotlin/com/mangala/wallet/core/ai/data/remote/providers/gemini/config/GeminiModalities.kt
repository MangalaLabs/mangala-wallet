package com.mangala.wallet.core.ai.data.remote.providers.gemini.config

enum class GeminiModalities(val value: String) {
    TEXT("TEXT"),
    IMAGE("IMAGE");

    companion object {
        fun fromString(value: String): GeminiModalities? {
            return entries.find { it.name.equals(value, ignoreCase = true) }
        }
    }
}