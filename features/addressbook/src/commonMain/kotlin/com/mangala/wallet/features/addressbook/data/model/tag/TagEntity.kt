package com.mangala.wallet.features.addressbook.data.model.tag

import com.benasher44.uuid.uuid4
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant


/**
 * Entity cho TagEntity (nhãn)
 * Tương ứng với bảng 'TagEntitys' trong database
 */
data class TagEntity(
    val id: String, // UUID
    val name: String,
    val color: String,
    val textColor: String? = null, // Mã màu văn bản (Hex), nếu null sẽ tự động tính toán dựa trên màu nền
    val icon: String? = null, // Icon có thể là emoji hoặc đường dẫn ảnh, tương tự như Group
    val isDeleted: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant,
    val contactCount: Int? = null, // Số lượng contact được gắn với tag này
    val addressCount: Int? = null  // Số lượng address được gắn với tag này
) {
    /**
     * Lấy màu văn bản phù hợp với màu nền (đen hoặc trắng)
     * @return Mã màu văn bản phù hợp
     */
    fun calculateTextColor(): String {
        // Nếu đã có textColor được chỉ định, sử dụng nó
        textColor?.let {
            return it
        }
        
        // Xử lý cả color index và hex color
        val actualHexColor = if (color.startsWith("#")) {
            // Nếu là hex color, sử dụng trực tiếp
            color
        } else {
            // Nếu là index, fallback to white (UI layer sẽ xử lý đúng)
            try {
                color.toInt() // Validate it's a number
                return "#FFFFFF" // Let UI layer handle index conversion
            } catch (e: NumberFormatException) {
                return "#000000"
            }
        }
        
        // Chuyển đổi mã màu HEX thành RGB
        val hexColor = actualHexColor.replace("#", "")
        if (hexColor.length != 6) {
            return "#000000" // Mặc định là đen nếu không phải mã HEX hợp lệ
        }

        try {
            val r = hexColor.substring(0, 2).toInt(16)
            val g = hexColor.substring(2, 4).toInt(16)
            val b = hexColor.substring(4, 6).toInt(16)

            // Tính độ sáng của màu nền theo công thức YIQ
            // Nếu màu nền tối, dùng chữ trắng, ngược lại dùng chữ đen
            val brightness = ((r * 299) + (g * 587) + (b * 114)) / 1000
            return if (brightness >= 128) "#000000" else "#FFFFFF"
        } catch (e: Exception) {
            return "#000000" // Mặc định là đen nếu có lỗi
        }
    }

    override fun toString(): String {
        return "TagEntity(id='$id', name='$name', color='$color', textColor='$textColor', icon='$icon')"
    }

    companion object {
        /**
         * Danh sách các màu TagEntity mặc định
         * @return Danh sách mã màu HEX
         */
        fun getDefaultColors(): List<String> {
            return listOf(
                "#F44336", // Red
                "#E91E63", // Pink
                "#9C27B0", // Purple
                "#673AB7", // Deep Purple
                "#3F51B5", // Indigo
                "#2196F3", // Blue
                "#03A9F4", // Light Blue
                "#00BCD4", // Cyan
                "#009688", // Teal
                "#4CAF50", // Green
                "#8BC34A", // Light Green
                "#CDDC39", // Lime
                "#FFEB3B", // Yellow
                "#FFC107", // Amber
                "#FF9800", // Orange
                "#FF5722"  // Deep Orange
            )
        }

        /**
         * Tạo một đối tượng TagEntity mới
         */
        fun create(
            id: String = uuid4().toString(), // UUID được tạo từ repository
            name: String,
            isDeleted: Boolean = false,
            color: String = "#CCCCCC", // Màu mặc định
            textColor: String? = null, // Màu văn bản, nếu null sẽ tự động tính toán dựa trên màu nền
            icon: String? = null, // Icon mới thêm vào
            contactCount: Int? = null,
            addressCount: Int? = null
        ): TagEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return TagEntity(
                id = id,
                name = name,
                color = color,
                textColor = textColor,
                icon = icon,
                isDeleted = isDeleted,
                createdAt = now,
                updatedAt = now,
                contactCount = contactCount,
                addressCount = addressCount
            )
        }
    }
}