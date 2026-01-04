package com.mangala.wallet.features.chains.bitcoin.domain.usecases.electrum

import com.mangala.wallet.features.chains.bitcoin.domain.model.utxo.BitcoinUtxo
import com.mangala.wallet.features.chains.bitcoin.domain.repository.electrum.ElectrumRepository
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.account.GetBitcoinAccountUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Use case for getting Bitcoin UTXOs via Electrum servers
 */
class GetElectrumUtxosUseCase(
    private val electrumRepository: ElectrumRepository,
    private val getBitcoinAccountUseCase: GetBitcoinAccountUseCase
) {
    /**
     * Get the Bitcoin UTXOs for the account using Electrum
     *
     * @param blockchainType The Bitcoin blockchain type (mainnet or testnet)
     * @return Flow emitting a list of UTXOs, or empty list on error
     */
    operator fun invoke(
        accountId: String,
        blockchainType: BlockchainType
    ): Flow<List<BitcoinUtxo>> = flow {
        electrumRepository.startConnection(blockchainType)

        val accountDetails = getBitcoinAccountUseCase(blockchainType, accountId) ?: run {
            emit(emptyList())
            return@flow
        }

        electrumRepository.addAddress(blockchainType, accountDetails.bip84Address)

        val utxos = electrumRepository.getAddressUtxos(blockchainType, accountDetails.bip84Address)

        emit(utxos)
    }
}