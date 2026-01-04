package com.mangala.wallet.twofactorauth.domain.repository

import com.mangala.wallet.twofactorauth.data.model.AuthResult
import com.mangala.wallet.twofactorauth.data.model.TotpSetupResult
import com.mangala.wallet.twofactorauth.data.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TwoFactorRepository {
    /**
     * Set up 2FA by generating a new TOTP secret and backup codes
     */
    suspend fun setup2FA(walletAddress: String): TotpSetupResult

    /**
     * Verify a TOTP code against the stored secret
     */
    suspend fun verifyCode(code: String): Boolean

    /**
     * Disable 2FA after verifying the confirmation code
     */
    suspend fun disable2FA(confirmationCode: String): Boolean

    /**
     * Authenticate a transaction using 2FA
     */
    suspend fun authenticateTransaction(transaction: Transaction, code: String): AuthResult

    /**
     * Check if 2FA is currently enabled
     */
    suspend fun is2FAEnabled(): Boolean

    /**
     * Export 2FA data as an encrypted backup
     */
    suspend fun exportBackup(password: String): ByteArray

    /**
     * Import 2FA data from an encrypted backup
     */
    suspend fun importBackup(data: ByteArray, password: String): Boolean

    /**
     * Verify a backup code
     */
    suspend fun verifyBackupCode(code: String): Boolean

    /**
     * Get a flow of the current authentication state
     */
    fun getAuthStateFlow(): Flow<Boolean>
}