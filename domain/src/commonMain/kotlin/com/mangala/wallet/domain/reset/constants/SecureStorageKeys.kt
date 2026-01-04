package com.mangala.wallet.domain.reset.constants

object SecureStorageKeys {
    // PIN related keys (verified in codebase)
    const val UNLOCK_PIN_STATE = "unlock_pin_state"
    const val INCORRECT_ATTEMPTS_PIN = "incorrect_attempts_pin"
    
    // Biometric settings (verified in codebase)
    const val ENABLE_BIOMETRIC_ANDROID = "enable_biometric"
    const val ENABLE_BIOMETRIC_IOS = "enable_biometric_iphone"
    
    // Wallet words pattern: "wallet_${walletId}_words" (verified in WalletLocalDataSourceImpl)
    fun getWalletWordsKey(walletId: String) = "wallet_${walletId}_words"
    
    // EOS private keys pattern: "EOS_TAG" + keyAlias (verified in EosKeyManagerImpl)
    // Note: EOS_TAG constant is defined in EosKeyManagerImpl, keyAlias varies
}