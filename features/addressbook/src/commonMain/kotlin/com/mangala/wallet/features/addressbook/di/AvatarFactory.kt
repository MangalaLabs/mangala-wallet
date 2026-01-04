package com.mangala.wallet.features.addressbook.di

import com.mangala.wallet.features.addressbook.domain.model.AvatarPickerContract
import com.mangala.wallet.features.addressbook.presentation.avatar.AvatarPickerViewModel

/**
 * Factory cung cấp các instance liên quan đến avatar
 * đảm bảo tính đa nền tảng
 */
expect object AvatarFactory {
    /**
     * Tạo AvatarPickerViewModel với history support
     */
    fun createAvatarPickerViewModel(onImageSelected: (String) -> Unit): AvatarPickerViewModel
    
    /**
     * Tạo AvatarPickerContract
     */
    fun createAvatarPickerContract(onImageSelected: (String) -> Unit): AvatarPickerContract
}