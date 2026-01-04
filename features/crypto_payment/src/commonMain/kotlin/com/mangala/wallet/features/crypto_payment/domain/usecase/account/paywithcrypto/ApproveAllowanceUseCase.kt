package com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.erc20.contract.ApproveMethod
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.model.blockchain.BlockchainType

class ApproveAllowanceUseCase(
    private val signAndSendTransactionDataUseCase: SignAndSendTransactionDataUseCase
) {

    suspend operator fun invoke(
        blockchainType: BlockchainType,
        from: Address,
        gasPrice: GasPrice,
        gas: Long?,
        transactionData: TransactionData
    ): String? {
        return signAndSendTransactionDataUseCase(blockchainType, from, transactionData, gasPrice, gas)
    }

    fun buildTransactionData(
        spender: Address,
        amount: BigInteger,
        tokenAddress: Address
    ): TransactionData {
        return TransactionData(
            to = tokenAddress,
            value = BigInteger.ZERO,
            input = ApproveMethod(spender, amount).encodedABI()
        )
    }
}