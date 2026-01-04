package com.mangala.eticket.domain.usecases.wallet

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.core.address.domain.usecases.DeriveEthereumAddressUseCase
import com.mangala.wallet.core.hdwallet.domain.model.HDKey
import com.mangala.wallet.core.hdwallet.domain.usecases.GenerateHDKeyUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.Blockchain
import com.mangala.wallet.model.blockchain.BlockchainType

class GetCurrentAddressUseCase(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val deriveAddressUseCase: DeriveEthereumAddressUseCase,
    private val generateHDKeyUseCase: GenerateHDKeyUseCase
) {
    suspend fun invoke(): Address {
        val hdKey = restrictHDKey()
        hdKey.let {
            return Address(deriveAddressUseCase.invoke(it.publicKey))
        }
    }

    private suspend fun restrictHDKey(): HDKey {
        val wallet = getSelectedWalletUseCase()
        val words = wallet?.words?.split(" ")
        val chainNetwork = getSelectedNetworkUseCase.invoke()
        val blockchainType = BlockchainType.fromUid(chainNetwork.blockChainUid)
        return generateHDKeyUseCase.invoke(
            words ?: listOf(),
            "",
            Blockchain(blockchainType, blockchainType.uid, ""),
            AddressType.Bip44
        )
    }
}