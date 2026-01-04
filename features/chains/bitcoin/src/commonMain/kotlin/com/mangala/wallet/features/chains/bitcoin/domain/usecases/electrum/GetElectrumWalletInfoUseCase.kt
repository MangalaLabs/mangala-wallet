package com.mangala.wallet.features.chains.bitcoin.domain.usecases.electrum

import com.mangala.wallet.features.chains.bitcoin.domain.model.balance.BitcoinBalance
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.account.GetBitcoinAccountUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

data class ElectrumWalletInfo(
    val address: String,
    val balanceSats: BitcoinBalance?,
)

class GetElectrumWalletInfoUseCase(
    private val getElectrumBalanceUseCase: GetElectrumBalanceUseCase
) {
    operator fun invoke(
        forceRefresh: Boolean,
        accountId: String,
        bip84Address: String,
        blockchainType: BlockchainType
    ): Flow<ElectrumWalletInfo?> =
        getElectrumBalanceUseCase(forceRefresh, accountId, bip84Address, blockchainType).map {
            ElectrumWalletInfo(
                address = bip84Address,
                balanceSats = it.data,
            )
        }
}