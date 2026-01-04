package com.mangala.wallet.features.addressbook.utils

import androidx.compose.ui.graphics.Color
import com.mangala.wallet.common.mokoresources.ColorsNew

/**
 * Chuyển đổi chuỗi hex thành Compose Color
 * 
 * @param defaultColor Màu mặc định nếu không thể chuyển đổi
 * @return Color tương ứng
 */
fun hexStringToColor(hexString: String, defaultColor: Color = Color.Gray): Color {
    return try {
        val cleanHex = hexString.removePrefix("#")
        if (cleanHex.length == 6) {
            Color(cleanHex.toLong(16) or 0xFF000000L)
        } else if (cleanHex.length == 8) {
            Color(cleanHex.toLong(16))
        } else {
            defaultColor
        }
    } catch (e: Exception) {
        defaultColor
    }
}

/**
 * Chuyển đổi màu sắc thành định dạng chuỗi index
 * Thay vì lưu màu dưới dạng hex, sẽ lưu dưới dạng index trong ColorsNew
 * 
 * @return Chuỗi index chứa màu sắc
 */
fun colorToIndexString(color: Color): String {
    // Thử tìm index chính xác trong ColorsNew
    val index = ColorsNew.colorToIndex(color)
    
    // Nếu tìm thấy index (khác -1), trả về index dưới dạng chuỗi
    if (index != -1) {
        return index.toString()
    }
    
    // Nếu không tìm thấy index chính xác, chuyển đổi thành chuỗi hex
    val colorInt = color.value.toLong().toInt() and 0xFFFFFF
    return "#" + colorInt.toString(16).padStart(6, '0').uppercase()
}

/**
 * Chuyển đổi chuỗi index hoặc hex thành Color
 * Hỗ trợ cả hai định dạng để tương thích ngược
 * 
 * @param colorString Chuỗi chứa màu sắc (index hoặc hex)
 * @param defaultColor Màu mặc định nếu không thể chuyển đổi
 * @return Color tương ứng
 */
fun stringToColor(colorString: String?, defaultColor: Color = Color.Gray): Color {
    if (colorString == null) return defaultColor
    
    return try {
        if (colorString.startsWith("#")) {
            // Nếu là chuỗi hex
            hexStringToColor(colorString, defaultColor)
        } else {
            try {
                // Nếu là index
                val index = colorString.toInt()
                ColorsNew.indexToColor(index)
            } catch (e: NumberFormatException) {
                // Nếu đây là một chuỗi khác không phải số hoặc mã hex
                defaultColor
            }
        }
    } catch (e: Exception) {
        defaultColor
    }
}