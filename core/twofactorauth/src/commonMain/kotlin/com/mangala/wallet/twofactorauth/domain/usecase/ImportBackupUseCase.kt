package com.mangala.wallet.twofactorauth.domain.usecase

import com.mangala.wallet.twofactorauth.domain.repository.TwoFactorRepository

class ImportBackupUseCase(private val repository: TwoFactorRepository) {
    suspend operator fun invoke(data: ByteArray, password: String): Boolean {
        return repository.importBackup(data, password)
    }
}
