package com.mangala.wallet.features.chains.bitcoin.domain.usecases.account

import com.mangala.wallet.features.chains.bitcoin.domain.model.account.BitcoinAccount
import com.mangala.wallet.features.chains.bitcoin.domain.repository.account.BitcoinAccountRepository
import com.mangala.wallet.model.blockchain.BlockchainType

class GetBitcoinAccountUseCase(
    private val bitcoinAccountRepository: BitcoinAccountRepository
) {
    suspend operator fun invoke(blockchainType: BlockchainType, accountId: String): BitcoinAccount? {
        return bitcoinAccountRepository.getAccount(blockchainType, accountId)
    }
}