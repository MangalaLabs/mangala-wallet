package com.mangala.wallet.features.chains.antelope_base.data.repository.actions.mapper

import com.mangala.antelope.base.api.model.EosAction
import com.mangala.wallet.features.chains.antelopebase.AntelopeActionTraceEntity
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.ext.toBoolean
import com.mangala.wallet.utils.ext.toLong
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun AntelopeActionTraceEntity.toEosAction(
    decodingJson: Json
) = EosAction(
    timestampSimple = timestamp,
    blockNum = block_num,
    blockId = block_id,
    trxId = trx_id,
    act = decodingJson.decodeFromString(act),
    receipts = decodingJson.decodeFromString(receipts),
    globalSequence = global_sequence,
    producer = producer,
    actionOrdinal = action_ordinal.toInt(),
    creatorActionOrdinal = creator_action_ordinal.toInt(),
    cpuUsageUs = cpu_usage_us.toInt(),
    netUsageWords = net_usage_words.toInt(),
    signatures = decodingJson.decodeFromString(signatures),
    accountActionSeq = account_action_seq,
    actionTrace = decodingJson.decodeFromString(action_trace),
    blockTime = block_time,
    globalActionSeq = global_action_sequence,
    irreversible = irreversible.toBoolean()
)

fun List<EosAction>.toAntelopeActionTraceEntity(
    accountName: String,
    blockchainUid: String,
    transactionType: AntelopeActionTraceTransactionType,
    serializingJson: Json
) = map {
    AntelopeActionTraceEntity(
        timestamp = it.timestampSimple.orEmpty(),
        block_num = it.blockNum.orZero(),
        block_id = it.blockId.orEmpty(),
        trx_id = it.trxId.orEmpty(),
        act = serializingJson.encodeToString(it.act),
        receipts = serializingJson.encodeToString(it.receipts),
        global_sequence = it.globalSequence.orZero(),
        producer = it.producer.orEmpty(),
        action_ordinal = it.actionOrdinal?.toLong().orZero(),
        creator_action_ordinal = it.creatorActionOrdinal?.toLong().orZero(),
        cpu_usage_us = it.cpuUsageUs?.toLong().orZero(),
        net_usage_words = it.netUsageWords?.toLong().orZero(),
        signatures = serializingJson.encodeToString(it.signatures),
        account_action_seq = it.accountActionSeq.orZero(),
        action_trace = serializingJson.encodeToString(it.actionTrace),
        block_time = it.blockTime.orEmpty(),
        global_action_sequence = it.globalActionSeq.orZero(),
        irreversible = it.irreversible?.toLong() ?: 0,
        account_name = accountName,
        blockchain_uid = blockchainUid,
        transaction_type = transactionType.name
    )
}

enum class AntelopeActionTraceTransactionType {
    RAM_FEE_TRANSFER,
    RAM_BUY_TRANSFER,
    RAM_SELL_TRANSFER,
    LOG_RAM,
    LOG_RAM_CHANGE
}