package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.BlockSyncer
import com.mangala.wallet.features.chains.erc20.contract.ApproveMethod
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.model.blockchain.BlockchainType

class ApproveUseCase(
    private val signAndSendTransactionDataUseCase: SignAndSendTransactionDataUseCase,
    private val blockSyncer: BlockSyncer,
) {

    suspend operator fun invoke(
        blockchainType: BlockchainType,
        from: Address,
        gasPrice: GasPrice,
        transactionData: TransactionData,
        gas: Long?
    ): String? {
        val txHash = signAndSendTransactionDataUseCase(
            blockchainType,
            from,
            transactionData,
            gasPrice,
            gas
        ) ?: return null

        return blockSyncer.waitUntilTransactionConfirmed(
            blockchainType = blockchainType,
            txHash = txHash
        )

    }

    fun approveTransactionData(
        contractAddress: Address,
        spenderAddress: Address,
        amount: BigInteger
    ): TransactionData {
        return TransactionData(
            contractAddress,
            BigInteger.ZERO,
            ApproveMethod(spenderAddress, amount).encodedABI()
        )
    }
}