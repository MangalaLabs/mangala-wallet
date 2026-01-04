package com.mangala.wallet.features.addressbook.utils

import androidx.compose.ui.graphics.Color
import com.mangala.wallet.common.mokoresources.ColorsNew

/**
 * Optimized and centralized color conversion utilities for tags
 * Provides consistent color handling with proper fallbacks
 */
object OptimizedColorUtils {
    
    // Cache for color conversions to improve performance
    private val colorCache = mutableMapOf<String, Color>()
    private val indexCache = mutableMapOf<Color, Int>()
    
    /**
     * Convert color string to Color with optimized caching and fallbacks
     */
    fun stringToColorOptimized(colorString: String?, fallback: Color = ColorsNew.tagTeal): Color {
        if (colorString.isNullOrBlank()) return fallback
        
        // Check cache first
        colorCache[colorString]?.let { return it }
        
        val result = try {
            when {
                // Handle index values (e.g., "0", "1", "2")
                colorString.toIntOrNull() != null -> {
                    val index = colorString.toInt()
                    if (index >= 0) {
                        ColorsNew.indexToColor(index)
                    } else {
                        fallback
                    }
                }
                
                // Handle hex colors (e.g., "#FF0000", "FF0000")
                colorString.startsWith("#") && colorString.length == 7 -> {
                    val hexValue = colorString.substring(1).toLong(16)
                    Color(hexValue or 0xFF000000)
                }
                
                colorString.length == 6 -> {
                    val hexValue = colorString.toLong(16)
                    Color(hexValue or 0xFF000000)
                }
                
                else -> fallback
            }
        } catch (e: Exception) {
            println("WARNING: Failed to parse color '$colorString', using fallback")
            fallback
        }
        
        // Cache the result
        colorCache[colorString] = result
        return result
    }
    
    /**
     * Convert Color to index string with caching
     */
    fun colorToIndexOptimized(color: Color): String {
        // Check cache first
        indexCache[color]?.let { return it.toString() }
        
        val index = ColorsNew.colorToIndex(color)
        return if (index != -1) {
            indexCache[color] = index
            index.toString()
        } else {
            // Convert to hex if not found in ColorsNew
            colorToHex(color)
        }
    }
    
    /**
     * Convert Color to hex string
     */
    private fun colorToHex(color: Color): String {
        return try {
            val rgb = ((color.red * 255).toInt() shl 16) or
                ((color.green * 255).toInt() shl 8) or
                (color.blue * 255).toInt()

            // Convert to hex string with leading zero
            val hex = rgb.toString(16).uppercase().padStart(6, '0')
            "#$hex"
        } catch (e: Exception) {
            "#000000" // Fallback to black
        }
    }
    
    /**
     * Clear color caches (call when memory pressure is detected)
     */
    fun clearCaches() {
        colorCache.clear()
        indexCache.clear()
    }
    
    /**
     * Get cache sizes for debugging
     */
    fun getCacheStats(): Pair<Int, Int> {
        return colorCache.size to indexCache.size
    }
}