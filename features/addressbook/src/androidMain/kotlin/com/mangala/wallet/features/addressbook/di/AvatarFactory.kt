package com.mangala.wallet.features.addressbook.di

import android.content.Context
import com.mangala.wallet.features.addressbook.data.local.avatar.AndroidAvatarPickerContract
import com.mangala.wallet.features.addressbook.domain.model.AvatarPickerContract
import com.mangala.wallet.features.addressbook.domain.repository.avatar.AvatarHistoryRepository
import com.mangala.wallet.features.addressbook.domain.repository.avatar.AvatarRepository
import com.mangala.wallet.features.addressbook.presentation.avatar.AvatarPickerViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Triển khai Android cho AvatarFactory
 */
actual object AvatarFactory : KoinComponent {
    // Inject context từ Koin
    private val context: Context by inject()
    private val avatarHistoryRepository: AvatarHistoryRepository by inject()
    private val avatarRepository: AvatarRepository by inject()
    
    /**
     * Tạo AvatarPickerViewModel với AndroidAvatarPickerContract và history support
     */
    actual fun createAvatarPickerViewModel(onImageSelected: (String) -> Unit): AvatarPickerViewModel {
        val contract = createAvatarPickerContract(onImageSelected)
        return AvatarPickerViewModel(contract, avatarHistoryRepository, avatarRepository)
    }
    
    /**
     * Tạo AndroidAvatarPickerContract
     */
    actual fun createAvatarPickerContract(onImageSelected: (String) -> Unit): AvatarPickerContract {
        return AndroidAvatarPickerContract(context, onImageSelected)
    }
}