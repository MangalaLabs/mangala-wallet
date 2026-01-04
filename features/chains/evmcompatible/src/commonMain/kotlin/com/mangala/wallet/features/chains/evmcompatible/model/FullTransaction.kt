package com.mangala.wallet.features.chains.evmcompatible.model

import com.mangala.wallet.features.chains.evmcompatible.decorations.TransactionDecoration

class FullTransaction(
    val transaction: Transaction,
    val decoration: TransactionDecoration
)