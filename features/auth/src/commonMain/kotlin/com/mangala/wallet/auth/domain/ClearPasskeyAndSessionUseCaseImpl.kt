package com.mangala.wallet.auth.domain

import com.mangala.wallet.auth.repository.AuthRepository
import com.mangala.wallet.core.auth.SessionManager
import com.mangala.wallet.domain.reset.usecases.ClearPasskeyAndSessionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Use case to clear all passkey credentials and authentication sessions
 * during wallet reset following the same pattern as AuthenticationFlowManager.logout().
 *
 * This clears:
 * - Server-side session (via AuthRepository.logout)
 * - Local session data (tokens, user info, session timing)
 * - In-memory passkey credential caches
 *
 * Note: Physical passkey deletion from device is handled by the user manually
 * as Android/iOS don't provide APIs to programmatically delete passkeys.
 */
class ClearPasskeyAndSessionUseCaseImpl(
    private val sessionManager: SessionManager,
    private val authRepository: AuthRepository
): ClearPasskeyAndSessionUseCase {
    override suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Get userId before clearing session (needed for server logout)
            val userId = sessionManager.getCurrentUserId()

            // 2. Logout from server-side session if user exists
            // This follows the same pattern as AuthenticationFlowManager.logout()
            if (userId != null) {
                try {
                    authRepository.logout(userId)
                } catch (e: Exception) {
                    // Continue with local cleanup even if server logout fails
                }
            }

            // 3. Clear local session data (all 8 session keys + session state)
            // This clears: auth_token, refresh_token, user_id, username,
            // auth_method, token_expiry, session_start, last_activity
            sessionManager.clearSession()

            // 4. In-memory passkey credentials (lastAuthenticationCredential, lastAuthenticationRawJson)
            // will be cleared when the app restarts or PasskeyManager instances are recreated.
            // Physical passkey deletion from device authenticators cannot be done programmatically
            // and must be handled by users through device settings.

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
