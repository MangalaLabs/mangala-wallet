package com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction

import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAbi
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter
import com.memtrip.eos.core.block.BlockIdDetails
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration

abstract class BaseConstructTransactionUseCase(
    val getInfoUseCase: GetInfoUseCase
) {
    fun constructTransactionAbi(headBlockId: String, actionsList: List<ActionAbi>): TransactionAbi {
        return transaction(
            transactionDefaultExpiry(),
            BlockIdDetails(headBlockId),
            actionsList
        )
    }

    private fun transactionDefaultExpiry(): Instant = with(Clock.System.now()) {
        plus(2.toDuration(DurationUnit.MINUTES))
    }

    private fun transaction(
        expirationDate: Instant,
        blockIdDetails: BlockIdDetails,
        actions: List<ActionAbi>
    ): TransactionAbi {
        return TransactionAbi(
            expirationDate,
            blockIdDetails.blockNum,
            blockIdDetails.blockPrefix,
            0,
            0,
            0,
            emptyList(),
            actions,
            emptyList(),
            emptyList(),
            emptyList()
        )
    }
}