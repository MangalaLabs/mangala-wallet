package com.mangala.wallet.features.addressbook.domain.usecase.setting

import com.mangala.wallet.features.addressbook.data.model.setting.UserSettingEntity
import com.mangala.wallet.features.addressbook.domain.repository.setting.SettingsRepository

class UpdateCurrentSettingUseCase(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(userSetting: UserSettingEntity): Result<Boolean> {
        return settingsRepository.saveUserSettings(userSetting)
    }
}