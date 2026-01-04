package com.mangala.wallet.domain.blockchain.usecases

import com.mangala.wallet.domain.blockchain.repository.BlockchainRepository
import com.mangala.wallet.model.blockchain.BlockchainEntity
import com.mangala.wallet.model.blockchain.BlockchainType

class GetBlockchainExplorerLinkUseCase(private val blockchainRepository: BlockchainRepository) {

    suspend fun getTxLink(blockchainUid: String, txHash: String): String {
        when (BlockchainType.fromUid(blockchainUid)) {
            BlockchainType.Eos -> {
                return getBlockchainEip3091Url(blockchainUid) + "transaction/$txHash"
            }
            BlockchainType.EosJungleTestnet -> {
                return getBlockchainEip3091Url(blockchainUid) + "transaction/$txHash"
            }
            else -> {
                val eip3091Url = getBlockchainEip3091Url(blockchainUid)
                return eip3091Url + "tx/$txHash"
            }
        }
    }

    private suspend fun getBlockchainEip3091Url(blockchainUid: String): String {
        val blockchain = blockchainRepository.getBlockchainById(blockchainUid).firstOrNull()
            ?: throw IllegalArgumentException("Invalid blockchain uid")

        if (blockchain.eip3091url.isNullOrBlank()) {
            throw NotImplementedError("EIP-3091 url is not implemented for this blockchain")
        }

        return blockchain.eip3091url!!
    }
}