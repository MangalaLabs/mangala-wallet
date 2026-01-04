package com.mangala.wallet.features.addressbook.domain.model


/**
 * Interface định nghĩa các thuộc tính cần thiết để hỗ trợ tính năng avatar.
 * Bất kỳ entity nào muốn có khả năng hiển thị avatar cần implement interface này.
 */
data class Avatar(
    /**
     * Tên hiển thị của thực thể, dùng để tạo fallback avatar (chữ cái đầu)
     * khi không có emoji hoặc hình ảnh được chỉ định.
     */
    val name: String,
    /**
     * Nguồn dữ liệu avatar cho thực thể. Có thể là null nếu chưa được gán.
     * Khi null hoặc AvatarSource.None, UI sẽ hiển thị chữ cái đầu của name.
     */
    val avatarSource: AvatarSource
)