package com.mangala.wallet.twofactorauth.domain.usecase

import com.mangala.wallet.twofactorauth.data.model.AuthResult
import com.mangala.wallet.twofactorauth.data.model.Transaction
import com.mangala.wallet.twofactorauth.domain.repository.TwoFactorRepository

/**
 * Use case: Authenticate a transaction with 2FA
 */
class AuthenticateTransactionUseCase(private val repository: TwoFactorRepository) {
    suspend operator fun invoke(transaction: Transaction, code: String): AuthResult {
        return repository.authenticateTransaction(transaction, code)
    }
}