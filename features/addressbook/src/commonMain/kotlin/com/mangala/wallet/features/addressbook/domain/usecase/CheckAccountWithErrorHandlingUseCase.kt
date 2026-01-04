package com.mangala.wallet.features.addressbook.domain.usecase

import com.mangala.wallet.features.chains.antelope_base.domain.model.AccountCheckResult
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsByQueryUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import com.mangala.wallet.features.addressbook.domain.validation.ValidationLogger
import com.mangala.wallet.features.addressbook.domain.validation.NoOpValidationLogger
import com.mangala.wallet.features.addressbook.domain.validation.DebugValidationLogger

/**
 * Internal use case for addressbook module that provides enhanced error handling
 * without affecting other modules.
 */
internal class CheckAccountWithErrorHandlingUseCase(
    private val getAccountsByQueryUseCase: GetAccountsByQueryUseCase,
    private val logger: ValidationLogger = NoOpValidationLogger
) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        accountName: String,
        timeoutMillis: Long = 5000L,
        maxRetries: Int = 2
    ): AccountCheckResult {
        logger.debug("=== CHECK ACCOUNT API CALL ===")
        logger.debug("BlockchainType: $blockchainType")
        logger.debug("Account name: $accountName")
        logger.debug("Timeout: $timeoutMillis ms")
        
        return try {
            withTimeout(timeoutMillis) {
                var lastError: Throwable? = null
                var retryCount = 0
                
                while (retryCount <= maxRetries) {
                    logger.debug("Attempt ${retryCount + 1}/${maxRetries + 1} - Calling getAccountsByQueryUseCase")
                    val response = getAccountsByQueryUseCase(
                        blockchainType,
                        accountName
                    )
                    
                    if (response.isSuccess) {
                        val accounts = response.getOrNull() ?: emptyList()
                        logger.debug("API Success: Found ${accounts.size} accounts matching query")
                        logger.debug("Accounts found: $accounts")
                        val accountExists = accounts.any { it == accountName }
                        
                        return@withTimeout if (accountExists) {
                            logger.debug("Result: Account EXISTS - $accountName found in results")
                            AccountCheckResult.Exists(accountName)
                        } else {
                            logger.debug("Result: Account NOT EXISTS - $accountName not found in results")
                            AccountCheckResult.NotExists(accountName)
                        }
                    }
                    
                    // API call failed
                    lastError = response.exceptionOrNull()
                    logger.debug("Account check failed for $accountName: ${lastError?.message}")
                    
                    // Don't retry for non-network errors
                    // Note: This is a pragmatic approach for network detection
                    // In production, consider using specific exception types
                    if (lastError?.message?.contains("network", ignoreCase = true) != true &&
                        lastError?.message?.contains("timeout", ignoreCase = true) != true) {
                        break
                    }
                    
                    retryCount++
                    if (retryCount <= maxRetries) {
                        // Wait before retry with exponential backoff
                        kotlinx.coroutines.delay(500L * retryCount)
                    }
                }
                
                // All retries failed
                when {
                    lastError?.message?.contains("network", ignoreCase = true) == true -> 
                        AccountCheckResult.NetworkError
                    else -> 
                        AccountCheckResult.Error(
                            message = lastError?.message ?: "Failed to check account after $retryCount retries",
                            exception = lastError
                        )
                }
            }
        } catch (e: TimeoutCancellationException) {
            logger.debug("Account check timed out for $accountName")
            AccountCheckResult.Timeout
        } catch (e: Exception) {
            logger.error("Unexpected error checking account $accountName", e)
            AccountCheckResult.Error(
                message = "Unexpected error: ${e.message}",
                exception = e
            )
        }
    }
}