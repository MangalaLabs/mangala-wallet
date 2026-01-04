package com.mangala.wallet.local.cache

import commangalawalletdatabase.TransactionMetadataEntity

interface TransactionMetadataLocalDataSource {

    suspend fun insertTransactionMetadata(metadata: TransactionMetadataEntity)
    suspend fun getLastUpdatedTimestamp(address: String, blockchainUid: String): Long?
    suspend fun clearAllTransactionMetadata()
}