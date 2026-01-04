package com.mangala.wallet.features.chains.bitcoin.data.local.transaction.mapper

import com.mangala.wallet.features.chains.bitcoin.BitcoinTransactionEntity
import com.mangala.wallet.features.chains.bitcoin.data.remote.transaction.response.BitcoinTransactionResponse
import com.mangala.wallet.features.chains.bitcoin.data.repository.transaction.mapper.toTransactionInput
import com.mangala.wallet.features.chains.bitcoin.data.repository.transaction.mapper.toTransactionOutput
import com.mangala.wallet.features.chains.bitcoin.domain.model.transaction.BitcoinTransaction
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

fun BitcoinTransaction.toEntity(blockchainType: BlockchainType): BitcoinTransactionEntity {
    val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
        encodeDefaults = true
    }
    
    return BitcoinTransactionEntity(
        txid = txid,
        blockchainType = blockchainType.uid,
        hash = null, // Optional field not present in domain model
        version = version.toLong(),
        size = size.toLong(),
        weight = weight.toLong(),
        locktime = locktime,
        fee = fee,
        confirmed = status.confirmed,
        block_height = status.block_height?.toLong(),
        block_hash = status.block_hash,
        block_time = status.block_time,
        vin = json.encodeToString(vin),
        vout = json.encodeToString(vout),
        lastUpdated = Clock.System.now().toEpochMilliseconds()
    )
}

fun BitcoinTransactionEntity.toDomain(): BitcoinTransaction {
    val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
    }
    
    val inputs = json.decodeFromString<List<BitcoinTransactionResponse.Vin>>(vin)
    val outputs = json.decodeFromString<List<BitcoinTransactionResponse.Vout>>(vout)
    
    return BitcoinTransaction(
        txid = txid,
        version = version.toInt(),
        locktime = locktime,
        size = size.toInt(),
        weight = weight.toInt(),
        fee = fee,
        status = BitcoinTransaction.TransactionStatus(
            confirmed = confirmed,
            block_height = block_height?.toInt(),
            block_hash = block_hash,
            block_time = block_time
        ),
        vin = inputs.mapNotNull { it.toTransactionInput() },
        vout = outputs.mapNotNull { it.toTransactionOutput() }
    )
}

fun createEmptyBitcoinTransactionEntity(txId: String, blockchainType: BlockchainType): BitcoinTransactionEntity {
    return BitcoinTransactionEntity(
        txid = txId,
        blockchainType = blockchainType.uid,
        hash = null,
        version = 0,
        size = 0,
        weight = 0,
        locktime = 0,
        fee = 0,
        confirmed = false,
        block_height = null,
        block_hash = null,
        block_time = null,
        vin = "[]",
        vout = "[]",
        lastUpdated = 0 // Set to 0 so it will be refreshed immediately
    )
}
