package com.mangala.wallet.features.chains.evmcompatible.model

//@Entity
class TransactionSyncerState(
//    @PrimaryKey
    val syncerId: String,
    val lastBlockNumber: Long
)
