package com.mangala.wallet.features.addressbook.di

import com.mangala.wallet.features.addressbook.domain.model.AvatarPickerContract
import com.mangala.wallet.features.addressbook.domain.repository.avatar.AvatarHistoryRepository
import com.mangala.wallet.features.addressbook.domain.repository.avatar.AvatarRepository
import com.mangala.wallet.features.addressbook.presentation.avatar.AvatarPickerViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Factory cung cấp các instance liên quan đến avatar
 * đảm bảo tính đa nền tảng
 */
actual object AvatarFactory : KoinComponent {
    private val avatarHistoryRepository: AvatarHistoryRepository by inject()
    private val avatarRepository: AvatarRepository by inject()
    
    /**
     * Tạo AvatarPickerViewModel
     */
    actual fun createAvatarPickerViewModel(onImageSelected: (String) -> Unit): AvatarPickerViewModel {
        val contract = createAvatarPickerContract(onImageSelected)
        return AvatarPickerViewModel(contract, avatarHistoryRepository, avatarRepository)
    }

    /**
     * Tạo AvatarPickerContract
     */
    actual fun createAvatarPickerContract(onImageSelected: (String) -> Unit): AvatarPickerContract {
        return object : AvatarPickerContract {
            override fun openImagePicker() {
                // iOS chưa hỗ trợ chọn ảnh
            }

            override fun isImagePickerSupported(): Boolean {
                return false // iOS chưa hỗ trợ
            }
        }
    }
}