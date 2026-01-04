package com.mangala.wallet.features.chains.antelope_base.domain.model

/**
 * Result of checking account existence on Antelope chains
 */
sealed class AccountCheckResult {
    data class Exists(val accountName: String) : AccountCheckResult()
    data class NotExists(val accountName: String) : AccountCheckResult()
    data class Error(val message: String, val exception: Throwable? = null) : AccountCheckResult()
    object NetworkError : AccountCheckResult()
    object Timeout : AccountCheckResult()
}