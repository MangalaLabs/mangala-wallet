package com.mangala.wallet.features.chains.bitcoin.data.repository.transaction.mapper

import com.mangala.wallet.features.chains.bitcoin.BitcoinTransactionEntity
import com.mangala.wallet.features.chains.bitcoin.data.remote.transaction.response.BitcoinTransactionResponse
import com.mangala.wallet.features.chains.bitcoin.domain.model.transaction.BitcoinTransaction
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

fun BitcoinTransactionResponse.responseToBitcoinTransaction(): BitcoinTransaction? {
    return try {
        BitcoinTransaction(
            txid = txid ?: return null,
            version = version ?: 0,
            locktime = locktime?.toLong() ?: 0,
            size = size ?: 0,
            weight = weight ?: 0,
            fee = fee?.toLong() ?: 0,
            status = BitcoinTransaction.TransactionStatus(
                confirmed = status?.confirmed ?: false,
                block_height = status?.blockHeight,
                block_hash = status?.blockHash,
                block_time = status?.blockTime?.toLong()
            ),
            vin = vin?.mapNotNull { it?.toTransactionInput() } ?: emptyList(),
            vout = vout?.mapNotNull { it?.toTransactionOutput() } ?: emptyList()
        )
    } catch (e: Exception) {
        null
    }
}

fun BitcoinTransactionResponse.toTransactionEntity(blockchainType: BlockchainType): BitcoinTransactionEntity? {
    val json = Json { 
        ignoreUnknownKeys = true 
        isLenient = true
        encodeDefaults = true
    }
    
    return try {
        val domainInputs = vin
        val domainOutputs = vout

        val vinJson = json.encodeToString(domainInputs)
        val voutJson = json.encodeToString(domainOutputs)
        
        BitcoinTransactionEntity(
            txid = txid ?: return null,
            blockchainType = blockchainType.uid,
            hash = null,
            version = version?.toLong() ?: 0,
            size = size?.toLong() ?: 0,
            weight = weight?.toLong() ?: 0,
            locktime = locktime?.toLong() ?: 0,
            fee = fee?.toLong() ?: 0,
            confirmed = status?.confirmed ?: false,
            block_height = status?.blockHeight?.toLong(),
            block_hash = status?.blockHash,
            block_time = status?.blockTime?.toLong(),
            vin = vinJson,
            vout = voutJson,
            lastUpdated = Clock.System.now().toEpochMilliseconds()
        )
    } catch (e: Exception) {
        null
    }
}

fun BitcoinTransactionResponse.Vin.toTransactionInput(): BitcoinTransaction.TransactionInput? {
    return try {
        BitcoinTransaction.TransactionInput(
            txid = txid ?: return null,
            vout = vout ?: 0,
            prevout = prevout?.toTransactionOutput(),
            scriptsig = scriptsig ?: "",
            scriptsig_asm = scriptsigAsm ?: "",
            witness = witness?.map { it.orEmpty() },
            sequence = sequence ?: 0,
            is_coinbase = isCoinbase ?: false
        )
    } catch (e: Exception) {
        null
    }
}

fun BitcoinTransactionResponse.Vout.toTransactionOutput(): BitcoinTransaction.TransactionOutput? {
    return try {
        BitcoinTransaction.TransactionOutput(
            scriptpubkey = scriptpubkey ?: "",
            scriptpubkeyAsm = scriptpubkeyAsm ?: "",
            scriptpubkeyType = scriptpubkeyType ?: "",
            scriptpubkeyAddress = scriptpubkeyAddress,
            value = value ?: 0
        )
    } catch (e: Exception) {
        null
    }
}

fun BitcoinTransactionResponse.Vin.Prevout.toTransactionOutput(): BitcoinTransaction.TransactionOutput? {
    return try {
        BitcoinTransaction.TransactionOutput(
            scriptpubkey = scriptpubkey ?: "",
            scriptpubkeyAsm = scriptpubkeyAsm ?: "",
            scriptpubkeyType = scriptpubkeyType ?: "",
            scriptpubkeyAddress = scriptpubkeyAddress,
            value = value ?: 0
        )
    } catch (e: Exception) {
        null
    }
}