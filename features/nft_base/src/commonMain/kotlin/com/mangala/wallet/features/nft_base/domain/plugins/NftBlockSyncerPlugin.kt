package com.mangala.wallet.features.nft_base.domain.plugins

import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.features.chains.BlockSyncerPlugin
import com.mangala.wallet.features.nft_base.domain.usecases.DeleteNftByIdUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class NftBlockSyncerPlugin(
    private val deleteNftByIdUseCase: DeleteNftByIdUseCase
): BlockSyncerPlugin {

    override suspend fun onTransactionStatusUpdatedForAccount(
        blockchainType: BlockchainType,
        successfulTransactionsByAccount: Map<String, List<Transaction>>,
        accountId: String
    ) {
        val successfulTransactionsForCurrentAccount = successfulTransactionsByAccount[accountId]
        val nftTransactions = successfulTransactionsForCurrentAccount?.filter { it.isNftTransaction }
        if (nftTransactions.isNullOrEmpty().not()) {
            nftTransactions?.forEach { transaction ->
                val paramValue =
                    transaction.logEvents?.firstOrNull()?.decoded?.params?.find { it?.name == "tokenId" && it.value is Transaction.LogEvent.Decoded.ParamValue.Primitive }?.value
                val tokenId = paramValue as? Transaction.LogEvent.Decoded.ParamValue.Primitive

                tokenId?.let {
                    deleteNftByIdUseCase(
                        accountId = accountId,
                        blockchainUid = blockchainType.uid,
                        collectionContractAddress = transaction.toAddress,
                        tokenId = tokenId.value
                    )
                }
            }
        }
    }
}