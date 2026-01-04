package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.model.blockchain.BlockchainType

class SwapTokenUseCase(
    private val signAndSendTransactionDataUseCase: SignAndSendTransactionDataUseCase
) {

    suspend operator fun invoke(
        blockchainType: BlockchainType,
        from: Address,
        transactionData: TransactionData,
        gasPrice: GasPrice,
        gas: Long?
    ): String? {
//        TODO: Implement logic log transaction
        return signAndSendTransactionDataUseCase(
            blockchainType,
            from,
            transactionData,
            gasPrice,
            gas
        )
    }
}