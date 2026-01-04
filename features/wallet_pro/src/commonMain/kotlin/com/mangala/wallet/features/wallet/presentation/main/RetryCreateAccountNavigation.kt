package com.mangala.wallet.features.wallet.presentation.main

sealed interface RetryCreateAccountNavigation {
    data class SetupPinAndBackupAccount(val accountName: String, val blockchainUid: String) :
        RetryCreateAccountNavigation

    data class BackupAccount(val accountName: String, val blockchainUid: String) :
        RetryCreateAccountNavigation
}