package com.mangala.wallet.features.chains.bitcoin.domain.usecases.electrum

import com.mangala.wallet.features.chains.bitcoin.domain.model.balance.BitcoinBalance
import com.mangala.wallet.features.chains.bitcoin.domain.repository.electrum.ElectrumRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

class GetElectrumBalanceUseCase(
    private val electrumRepository: ElectrumRepository
) {
    operator fun invoke(
        forceRefresh: Boolean,
        accountId: String,
        bip84Address: String,
        blockchainType: BlockchainType
    ): Flow<Resource<BitcoinBalance?>> = electrumRepository.getBalance(
        forceRefresh = forceRefresh,
        accountId = accountId,
        bip84Address = bip84Address,
        blockchainType = blockchainType
    )
}