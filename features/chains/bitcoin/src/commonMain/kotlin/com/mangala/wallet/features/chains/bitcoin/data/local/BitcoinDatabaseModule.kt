package com.mangala.wallet.features.chains.bitcoin.data.local

import org.koin.core.module.Module

internal const val BITCOIN_DATABASE_NAME = "bitcoin_database.db"

internal expect fun bitcoinDatabaseModule(): Module