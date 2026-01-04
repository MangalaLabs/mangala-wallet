package com.mangala.wallet.features.addressbook.domain.usecase.setting

import com.mangala.wallet.features.addressbook.data.model.setting.UserSettingEntity
import com.mangala.wallet.features.addressbook.domain.repository.setting.SettingsRepository

class GetCurrentUserSettingUseCase(
    private val userSettingsRepository: SettingsRepository
) {
    suspend operator fun invoke(): UserSettingEntity? {
        return userSettingsRepository.getUserSettings()
    }
}