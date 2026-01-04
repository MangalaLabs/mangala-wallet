package com.mangala.wallet.features.addressbook.di

import com.mangala.wallet.features.addressbook.domain.model.AvatarPickerContract
import com.mangala.wallet.features.addressbook.domain.repository.avatar.AvatarHistoryRepository
import com.mangala.wallet.features.addressbook.domain.repository.avatar.AvatarRepository
import com.mangala.wallet.features.addressbook.presentation.avatar.AvatarPickerViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Triển khai Desktop cho AvatarFactory
 */
actual object AvatarFactory : KoinComponent {
    private val avatarHistoryRepository: AvatarHistoryRepository by inject()
    private val avatarRepository: AvatarRepository by inject()
    
    /**
     * Tạo AvatarPickerViewModel với DesktopAvatarPickerContract và history support
     */
    actual fun createAvatarPickerViewModel(onImageSelected: (String) -> Unit): AvatarPickerViewModel {
        val contract = createAvatarPickerContract(onImageSelected)
        return AvatarPickerViewModel(contract, avatarHistoryRepository, avatarRepository)
    }
    
    /**
     * Tạo một phiên bản của AvatarPickerContract dành cho desktop
     * Hiện tại desktop chưa hỗ trợ chọn ảnh từ hệ thống
     */
    actual fun createAvatarPickerContract(onImageSelected: (String) -> Unit): AvatarPickerContract {
        return object : AvatarPickerContract {
            override fun openImagePicker() {
                // Desktop chưa hỗ trợ chọn ảnh
            }

            override fun isImagePickerSupported(): Boolean {
                return false // Desktop chưa hỗ trợ
            }
        }
    }
}