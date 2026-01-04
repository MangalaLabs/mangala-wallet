package com.mangala.wallet.features.addressbook.domain.validation

object ValidationConstants {
    // General validation
    const val MAX_ADDRESS_LENGTH = 150
    const val MAX_DOMAIN_LENGTH = 255
    
    // Contact and Group validation
    const val MAX_CONTACT_NAME_LENGTH = 100
    const val MAX_GROUP_NAME_LENGTH = 50
    
    // Antelope/EOS/VAULTA specific
    const val MAX_ANTELOPE_ACCOUNT_LENGTH = 12
    const val MIN_ANTELOPE_API_CHECK_LENGTH = 4
    const val ANTELOPE_PREMIUM_ACCOUNT_LENGTH_1 = 1
    const val ANTELOPE_PREMIUM_ACCOUNT_LENGTH_2 = 2
    const val ANTELOPE_PREMIUM_ACCOUNT_LENGTH_3 = 3
    
    // Timeouts (in milliseconds)
    const val DEFAULT_VALIDATION_TIMEOUT = 10000L // 10 seconds
    const val API_CALL_TIMEOUT = 2000L // Reduced from 5s to 2s for better UX
    const val DOMAIN_RESOLUTION_TIMEOUT = 2000L // Reduced from 5s to 2s for better UX
    
    // Debounce delays (in milliseconds)
    const val TYPING_DEBOUNCE_MS = 300L
    const val PASTE_DEBOUNCE_MS = 0L
    const val DELETION_DEBOUNCE_MS = 50L
    const val ANTELOPE_STANDARD_DELAY_MS = 400L
    const val ANTELOPE_LONG_NAME_DELAY_MS = 600L
    
    // Retry configuration
    const val DEFAULT_MAX_RETRIES = 2
    const val RETRY_BACKOFF_BASE_MS = 500L
    
    // Cache configuration
    const val DEFAULT_CACHE_SIZE = 100
    const val DEFAULT_CACHE_DURATION_MINUTES = 5L
    
    // Regex patterns
    val VAULTA_ACCOUNT_REGEX = Regex("^[a-z1-5.]+$")
    val ETHEREUM_ADDRESS_REGEX = Regex("^0x[a-fA-F0-9]{40}$")
    val BITCOIN_LEGACY_REGEX = Regex("^[13][a-km-zA-HJ-NP-Z1-9]{25,34}$")
    val BITCOIN_SEGWIT_REGEX = Regex("^bc1[a-z0-9]{39,59}$")
    
    // System accounts
    val ANTELOPE_SYSTEM_ACCOUNTS = setOf(
        "eosio", "eosio.token", "eosio.ram", "eosio.ramfee",
        "eosio.stake", "eosio.names", "eosio.saving", "eosio.bpay",
        "eosio.msig", "eosio.vpay", "eosio.rex", "eosio.wrap"
    )
    
    val ANTELOPE_BURN_ADDRESSES = setOf(
        "vaulta.null", "eosio.null"
    )
    
    // Blockchain symbol mappings
    val ANTELOPE_CHAINS = setOf("EOS", "TELOS", "WAX", "FIO", "VAULTA")
    val EVM_CHAINS = setOf("ETH", "BSC", "POLYGON", "AVAX", "FTM", "ARB", "OP")
}