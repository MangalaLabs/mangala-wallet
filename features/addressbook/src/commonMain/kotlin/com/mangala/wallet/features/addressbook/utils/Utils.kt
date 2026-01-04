package com.mangala.wallet.features.addressbook.utils

import androidx.compose.ui.graphics.Color
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.enum.TransactionStatus
import com.mangala.wallet.features.addressbook.presentation.contact.recent.BlockchainIcons
import com.mangala.wallet.mokoresources.MR
import dev.icerock.moko.resources.ImageResource
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.absoluteValue

fun getAvatarBackgroundColor(identifier: String): Color {
    // Generate a consistent color based on first characters
    val seed = identifier.hashCode()
    val colorSet = listOf(
        Color(0xFF1976D2), // Blue
        Color(0xFF388E3C), // Green
        Color(0xFFD32F2F), // Red
        Color(0xFF7B1FA2), // Purple
        Color(0xFF689F38), // Light Green
        Color(0xFFEF6C00), // Orange
        Color(0xFF0097A7), // Cyan
        Color(0xFF303F9F), // Indigo
        Color(0xFFC2185B), // Pink
        Color(0xFF455A64)  // Blue Grey
    )

    return colorSet.getOrNull(seed.absoluteValue % colorSet.size) ?: Color(0xFF1976D2)
}

fun getImageResourceForSymbol(symbol: String): ImageResource? {
    return when (symbol.uppercase()) {
        "ETH", "ETHEREUM" -> MR.images.ethereum
        "BTC", "BITCOIN" -> MR.images.bitcoin
        "BSC", "BNB", "BINANCE" -> MR.images.binance_smart_chain
        "SOL", "SOLANA" -> MR.images.solana
        "MATIC", "POLYGON" -> MR.images.polygon
        "AVAX", "AVALANCHE" -> MR.images.avalanche
        "FTM", "FANTOM" -> MR.images.fantom
        "A", "EOS", "VAULTA" -> MR.images.vaulta // Vaulta uses 'A' as symbol
        // Add more common blockchain names
        "OPT", "OPTIMISM" -> MR.images.optimism
        "ARB", "ARBITRUM" -> MR.images.arbitrum
        "GNOSIS", "GNO", "XDAI" -> MR.images.gnosis
        // Default fallback - show vaulta icon for unknown blockchains
        else -> MR.images.vaulta // Default to vaulta icon instead of null
    }
}

/**
 * Get image resource from database icon path string
 * Converts database icon path like "MR.images.vaulta" to actual ImageResource
 */
fun getImageResourceFromPath(iconPath: String): ImageResource? {
    return when (iconPath) {
        "MR.images.ethereum" -> MR.images.ethereum
        "MR.images.bitcoin" -> MR.images.bitcoin
        "MR.images.binance_smart_chain" -> MR.images.binance_smart_chain
        "MR.images.solana" -> MR.images.solana
        "MR.images.polygon" -> MR.images.polygon
        "MR.images.avalanche" -> MR.images.avalanche
        "MR.images.fantom" -> MR.images.fantom
        "MR.images.vaulta" -> MR.images.vaulta
        "MR.images.optimism" -> MR.images.optimism
        "MR.images.arbitrum" -> MR.images.arbitrum
        "MR.images.gnosis" -> MR.images.gnosis
        // Additional networks that might be added later
        "MR.images.bitcoin_cash_circle" -> MR.images.bitcoin_cash_circle
        "MR.images.ecash" -> MR.images.ecash
        "MR.images.litecoin" -> MR.images.litecoin
        "MR.images.dash" -> MR.images.dash
        "MR.images.zcash" -> MR.images.zcash
        else -> null
    }
}

fun imageResourceToPath(imageResource: ImageResource?): String {
    return when (imageResource) {
        MR.images.ethereum -> "MR.images.ethereum"
        MR.images.bitcoin -> "MR.images.bitcoin"
        MR.images.binance_smart_chain -> "MR.images.binance_smart_chain"
        MR.images.solana -> "MR.images.solana"
        MR.images.polygon -> "MR.images.polygon"
        MR.images.avalanche -> "MR.images.avalanche"
        MR.images.fantom -> "MR.images.fantom"
        MR.images.vaulta -> "MR.images.vaulta"
        MR.images.optimism -> "MR.images.optimism"
        MR.images.arbitrum -> "MR.images.arbitrum"
        MR.images.gnosis -> "MR.images.gnosis"
        // Additional networks that might be added later
        MR.images.bitcoin_cash_circle -> "MR.images.bitcoin_cash_circle"
        MR.images.ecash -> "MR.images.ecash"
        MR.images.litecoin -> "MR.images.litecoin"
        MR.images.dash -> "MR.images.dash"
        MR.images.zcash -> "MR.images.zcash"
        else -> ""
    }
}

fun getDefaultAvatar(contactId: String): ImageResource {
    val hashCode = contactId.hashCode().absoluteValue
    return when (hashCode % 5) {
        0 -> MR.images.AvatarA
        1 -> MR.images.AvatarB
        2 -> MR.images.AvatarC
        3 -> MR.images.AvatarD
        4 -> MR.images.AvatarE
        else -> MR.images.AvatarA // Fallback
    }
}

fun getTimeAgo(timestamp: Long): String {
    val now = Clock.System.now().toEpochMilliseconds()
    val diffMillis = now - timestamp

    return when {
        diffMillis < 60_000 -> "Just now"
        diffMillis < 3_600_000 -> "${(diffMillis / 60_000).toInt()} minutes ago"
        diffMillis < 7_200_000 -> "1 hour ago"
        diffMillis < 86_400_000 -> "${(diffMillis / 3_600_000).toInt()} hours ago"
        diffMillis < 172_800_000 -> "Yesterday"
        diffMillis < 604_800_000 -> "${(diffMillis / 86_400_000).toInt()} days ago"
        else -> {
            val instant = Instant.fromEpochMilliseconds(timestamp)
            val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
            "${
                localDate.month.name.lowercase().replaceFirstChar { it.uppercase() }
            } ${localDate.dayOfMonth}, ${localDate.year}"
        }
    }
}


/**
 * Các hàm tiện ích để chuyển đổi giữa Instant và String cho trường Important Date
 */
object DateUtils {
    /**
     * Chuyển đổi từ Instant sang String định dạng "yyyy-MM-dd"
     * @param instant Instant cần chuyển đổi
     * @return String theo định dạng "yyyy-MM-dd"
     */
    fun instantToString(instant: Instant): String {
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date
        return "${localDate.year}-${localDate.monthNumber.toString().padStart(2, '0')}-${localDate.dayOfMonth.toString().padStart(2, '0')}"
    }

    /**
     * Chuyển đổi từ String định dạng "yyyy-MM-dd" sang Instant
     * @param dateString String theo định dạng "yyyy-MM-dd"
     * @return Instant hoặc null nếu chuỗi không hợp lệ
     */
    fun stringToInstant(dateString: String): Instant? {
        return try {
            // Phân tích chuỗi "yyyy-MM-dd"
            val parts = dateString.split("-")
            if (parts.size != 3) return null

            val year = parts[0].toIntOrNull() ?: return null
            val month = parts[1].toIntOrNull() ?: return null
            val day = parts[2].toIntOrNull() ?: return null

            // Tạo đối tượng LocalDate
            val localDate = kotlinx.datetime.LocalDate(year, month, day)

            // Chuyển đổi sang Instant lúc 00:00:00 tại múi giờ hiện tại
            val localDateTime = kotlinx.datetime.LocalDateTime(
                year = localDate.year,
                monthNumber = localDate.monthNumber,
                dayOfMonth = localDate.dayOfMonth,
                hour = 0,
                minute = 0,
                second = 0,
                nanosecond = 0
            )

            localDateTime.toInstant(TimeZone.currentSystemDefault())
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Định dạng hiển thị thân thiện với người dùng cho ngày tháng
     * @param dateString String theo định dạng "yyyy-MM-dd"
     * @return String theo định dạng "MMM dd, yyyy" (VD: "May 12, 2025")
     */
    fun formatDateForDisplay(dateString: String): String {
        val instant = stringToInstant(dateString) ?: return dateString
        val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault()).date

        val month = when (localDate.monthNumber) {
            1 -> "Jan"
            2 -> "Feb"
            3 -> "Mar"
            4 -> "Apr"
            5 -> "May"
            6 -> "Jun"
            7 -> "Jul"
            8 -> "Aug"
            9 -> "Sep"
            10 -> "Oct"
            11 -> "Nov"
            12 -> "Dec"
            else -> localDate.monthNumber.toString()
        }

        return "$month ${localDate.dayOfMonth}, ${localDate.year}"
    }

    /**
     * ✅ NEW: Format ImportantDate for display based on calendar type
     * @param importantDate ImportantDate object with calendar type info
     * @return Formatted string showing either solar or lunar date
     */
    fun formatImportantDateForDisplay(importantDate: com.mangala.wallet.features.addressbook.domain.model.ImportantDate): String {
        // Format display based on calendar type
        
        return when (importantDate.calendarType) {
            com.mangala.wallet.features.addressbook.domain.model.CalendarType.SOLAR -> {
                // Display solar date
                val date = importantDate.date
                val month = when (date.monthNumber) {
                    1 -> "Tháng 1"
                    2 -> "Tháng 2"
                    3 -> "Tháng 3"
                    4 -> "Tháng 4"
                    5 -> "Tháng 5"
                    6 -> "Tháng 6"
                    7 -> "Tháng 7"
                    8 -> "Tháng 8"
                    9 -> "Tháng 9"
                    10 -> "Tháng 10"
                    11 -> "Tháng 11"
                    12 -> "Tháng 12"
                    else -> "Tháng ${date.monthNumber}"
                }
                "${date.dayOfMonth} $month, ${date.year} (Dương lịch)"
            }
            com.mangala.wallet.features.addressbook.domain.model.CalendarType.LUNAR -> {
                // Display lunar date
                val lunarDate = importantDate.lunarDate
                if (lunarDate != null) {
                    val lunarDayName = getLunarDayDisplayName(lunarDate.day)
                    val lunarMonthName = when (lunarDate.month) {
                        1 -> "Giêng"
                        2 -> "Hai"
                        3 -> "Ba"
                        4 -> "Tư"
                        5 -> "Năm"
                        6 -> "Sáu"
                        7 -> "Bảy"
                        8 -> "Tám"
                        9 -> "Chín"
                        10 -> "Mười"
                        11 -> "Mười một"
                        12 -> "Chạp"
                        else -> "Tháng ${lunarDate.month}"
                    }
                    val leapText = if (lunarDate.isLeapMonth) " nhuận" else ""
                    val result = "$lunarDayName $lunarMonthName$leapText ${lunarDate.yearCycle} (Âm lịch)"
                    result
                } else {
                    // Compute lunar date from solar date if not available
                    try {
                        val computedLunar = AccurateLunarCalendar.toLunar(importantDate.date)
                        val lunarDayName = getLunarDayDisplayName(computedLunar.day)
                        val lunarMonthName = when (computedLunar.month) {
                            1 -> "Tháng Giêng"
                            2 -> "Tháng Hai"
                            3 -> "Tháng Ba"
                            4 -> "Tháng Tư"
                            5 -> "Tháng Năm"
                            6 -> "Tháng Sáu"
                            7 -> "Tháng Bảy"
                            8 -> "Tháng Tám"
                            9 -> "Tháng Chín"
                            10 -> "Tháng Mười"
                            11 -> "Tháng Mười một"
                            12 -> "Tháng Chạp"
                            else -> "Tháng ${computedLunar.month}"
                        }
                        val leapText = if (computedLunar.isLeapMonth) " nhuận" else ""
                        val result = "$lunarDayName $lunarMonthName$leapText, ${computedLunar.yearCycle} (Âm lịch)"
                        result
                    } catch (e: Exception) {
                        // Ultimate fallback to solar date with lunar label
                        val date = importantDate.date
                        "${date.dayOfMonth}/${date.monthNumber}/${date.year} (Âm lịch - lỗi)"
                    }
                }
            }
        }
    }

    /**
     * Get Vietnamese lunar day display name
     */
    private fun getLunarDayDisplayName(day: Int): String {
        return when (day) {
            1 -> "Mồng 1"
            2 -> "Mồng 2"
            3 -> "Mồng 3"
            4 -> "Mồng 4"
            5 -> "Mồng 5"
            6 -> "Mồng 6"
            7 -> "Mồng 7"
            8 -> "Mồng 8"
            9 -> "Mồng 9"
            10 -> "Mồng 10"
            15 -> "Rằm"
            in 11..19 -> "Ngày ${day}"
            in 20..30 -> "Ngày ${day}"
            else -> "Ngày $day"
        }
    }
}

fun getStatusText(status: String): String {
    return when (status) {
        TransactionStatus.PENDING.name -> "Processing"
        TransactionStatus.DRAFT.name -> "Queued"
        TransactionStatus.CONFIRMED.name -> "Completed"
        TransactionStatus.FAILED.name -> "Failed"
        else -> {
            "Unknown"
        }
    }
}

fun getStatusColor(status: String) = when (status) {
    TransactionStatus.PENDING.name -> ColorsNew.warning_500
    TransactionStatus.DRAFT.name -> ColorsNew.primary_600
    TransactionStatus.CONFIRMED.name -> ColorsNew.success_500
    TransactionStatus.FAILED.name -> ColorsNew.error_500
    else -> {
        ColorsNew.primary_500
    }
}


fun hexStringToColor(hexString: String): Color {
    val normalizedHex = hexString.trim().replace("#", "")

    return try {
        when (normalizedHex.length) {
            // Full hex: #RRGGBB or #AARRGGBB
            6 -> {
                val red = normalizedHex.substring(0, 2).toInt(16)
                val green = normalizedHex.substring(2, 4).toInt(16)
                val blue = normalizedHex.substring(4, 6).toInt(16)
                Color(red, green, blue)
            }
            8 -> {
                val alpha = normalizedHex.substring(0, 2).toInt(16)
                val red = normalizedHex.substring(2, 4).toInt(16)
                val green = normalizedHex.substring(4, 6).toInt(16)
                val blue = normalizedHex.substring(6, 8).toInt(16)
                Color(red, green, blue, alpha)
            }
            // Short hex: #RGB
            3 -> {
                val red = normalizedHex.substring(0, 1).repeat(2).toInt(16)
                val green = normalizedHex.substring(1, 2).repeat(2).toInt(16)
                val blue = normalizedHex.substring(2, 3).repeat(2).toInt(16)
                Color(red, green, blue)
            }
            // Invalid format, fallback to a default color
            else -> ColorsNew.avatarA
        }
    } catch (e: Exception) {
        // Fallback color if parsing fails
        ColorsNew.avatarA
    }
}