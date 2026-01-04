package com.mangala.wallet.features.chains.bitcoin.domain.usecases.utxo

import com.mangala.wallet.features.chains.bitcoin.domain.model.utxo.BitcoinUtxo
import com.mangala.wallet.features.chains.bitcoin.domain.repository.utxo.BitcoinUtxoRepository
import com.mangala.wallet.features.chains.bitcoin.domain.usecases.account.GetBitcoinAccountUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetBitcoinWalletUtxosUseCase(
    private val bitcoinUtxoRepository: BitcoinUtxoRepository,
    private val getBitcoinAccountUseCase: GetBitcoinAccountUseCase
) {
    operator fun invoke(
        accountId: String,
        blockchainType: BlockchainType,
        confirmedOnly: Boolean
    ): Flow<List<BitcoinUtxo>> = flow {
        try {
            val accountDetails = getBitcoinAccountUseCase(blockchainType, accountId)
            if (accountDetails == null) {
                emit(emptyList())
                return@flow
            }
            
            val allUtxos = bitcoinUtxoRepository.getUtxo(
                accountDetails.bip84Address,
                blockchainType
            )
            
            val filteredUtxos = if (confirmedOnly) {
                allUtxos.filter { it.isConfirmed }
            } else {
                allUtxos
            }
            
            emit(filteredUtxos)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    suspend fun invokeSuspend(
        accountId: String, 
        blockchainType: BlockchainType,
        confirmedOnly: Boolean
    ): List<BitcoinUtxo> {
        val accountDetails = getBitcoinAccountUseCase(blockchainType, accountId) ?: return emptyList()

        val allUtxos = bitcoinUtxoRepository.getUtxo(
            accountDetails.bip84Address,
            blockchainType
        )
        
        return if (confirmedOnly) {
            allUtxos.filter { it.isConfirmed }
        } else {
            allUtxos
        }
    }
}