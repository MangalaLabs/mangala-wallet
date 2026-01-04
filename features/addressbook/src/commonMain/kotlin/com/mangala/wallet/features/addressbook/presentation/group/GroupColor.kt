package com.mangala.wallet.features.addressbook.presentation.group

import androidx.compose.ui.graphics.Color
import com.mangala.wallet.common.mokoresources.ColorsNew


/**
 * Helper function to convert a group color string to a Color
 * Handles both numeric color indices and hex color strings
 * 
 * @param colorString The color string to parse (can be index, hex, or null)
 * @return Parsed Color or default color if parsing fails
 */
fun getColorFromGroupColor(colorString: String?): Color {
    if (colorString.isNullOrBlank()) {
        println("[GroupColor] Color string is null or blank, using default")
        return ColorsNew.primary_500
    }

    return try {
        // First try to parse as an integer index
        val colorIndex = colorString.toInt()
        if (colorIndex < 0) {
            println("[GroupColor] WARNING: Negative color index: $colorIndex, using default")
            return ColorsNew.primary_500
        }
        ColorsNew.indexToColor(colorIndex)
    } catch (e: NumberFormatException) {
        println("[GroupColor] Not a numeric index, trying hex parsing for: $colorString")
        parseHexColor(colorString)
    } catch (e: Exception) {
        println("[GroupColor] ERROR: Unexpected error parsing color index: $colorString - ${e.message}")
        ColorsNew.primary_500
    }
}

/**
 * Parse hex color string with proper validation
 */
private fun parseHexColor(colorString: String): Color {
    return try {
        val cleanedColorString = colorString.trim()
        when {
            cleanedColorString.startsWith("#") -> hexToColor(cleanedColorString)
            isValidHexPattern(cleanedColorString) -> hexToColor("#$cleanedColorString")
            else -> {
                println("[GroupColor] WARNING: Invalid color format: $colorString, using default")
                ColorsNew.avatarA
            }
        }
    } catch (e: Exception) {
        println("[GroupColor] ERROR: Failed to parse hex color: $colorString - ${e.message}")
        ColorsNew.avatarA
    }
}

/**
 * Validate if string matches hex color pattern
 */
private fun isValidHexPattern(colorString: String): Boolean {
    return colorString.matches(Regex("^[0-9A-Fa-f]{6}([0-9A-Fa-f]{2})?$"))
}

/**
 * Convert a hex color string to a Compose Color
 * Works across platforms in Kotlin Multiplatform
 */
private fun hexToColor(colorString: String): Color {
    val colorStr = colorString.removePrefix("#")

    return try {
        val red = colorStr.substring(0, 2).toInt(16)
        val green = colorStr.substring(2, 4).toInt(16)
        val blue = colorStr.substring(4, 6).toInt(16)

        // If the string has 8 characters, the last 2 are for alpha
        val alpha = if (colorStr.length >= 8) {
            colorStr.substring(6, 8).toInt(16)
        } else {
            255 // Default alpha is fully opaque
        }

        Color(red = red / 255f, green = green / 255f, blue = blue / 255f, alpha = alpha / 255f)
    } catch (e: Exception) {
        // For any parsing error, use a default color
        println("Error parsing hex color $colorString: ${e.message}")
        ColorsNew.avatarA
    }
}