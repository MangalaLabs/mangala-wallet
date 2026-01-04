package com.mangala.wallet.core.ai.data.remote.providers.gemini

import kotlinx.serialization.json.*

/**
 * Convert a JsonObject to a Map<String, Any?>
 */
fun JsonObject.toMap(): Map<String, Any?> {
    return entries.associate { (key, value) ->
        key to value.toAny()
    }
}

/**
 * Convert a JsonElement to its corresponding Kotlin type
 */
private fun JsonElement.toAny(): Any? {
    return when (this) {
        is JsonObject -> toMap()
        is JsonArray -> map { it.toAny() }
        is JsonPrimitive -> when {
            isString -> content
            content == "null" -> null
            content == "true" -> true
            content == "false" -> false
            else -> try {
                content.toDoubleOrNull() ?: content.toLongOrNull() ?: content
            } catch (e: NumberFormatException) {
                content
            }
        }
        else -> null
    }
}