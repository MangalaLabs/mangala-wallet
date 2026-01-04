package com.mangala.wallet.features.addressbook.domain.usecase.setting

import com.mangala.wallet.features.addressbook.data.model.setting.UserSettingEntity
import com.mangala.wallet.features.addressbook.domain.repository.setting.SettingsRepository

class SaveUserSettingUseCase(
    private val repository: SettingsRepository,
) {
    suspend operator fun invoke(settingEntity: UserSettingEntity): Result<Boolean> {
        return try {
            repository.saveUserSettings(settingEntity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}