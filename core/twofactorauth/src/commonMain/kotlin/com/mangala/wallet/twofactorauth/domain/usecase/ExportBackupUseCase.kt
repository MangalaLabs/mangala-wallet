package com.mangala.wallet.twofactorauth.domain.usecase

import com.mangala.wallet.twofactorauth.domain.repository.TwoFactorRepository

class ExportBackupUseCase(private val repository: TwoFactorRepository) {
    suspend operator fun invoke(password: String): ByteArray {
        return repository.exportBackup(password)
    }
}