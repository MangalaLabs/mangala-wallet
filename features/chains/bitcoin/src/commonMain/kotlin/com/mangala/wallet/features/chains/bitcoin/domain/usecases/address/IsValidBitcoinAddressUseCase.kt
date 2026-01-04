package com.mangala.wallet.features.chains.bitcoin.domain.usecases.address

import com.mangala.wallet.model.blockchain.BlockchainType
import fr.acinq.bitcoin.Bitcoin.addressToPublicKeyScript
import fr.acinq.bitcoin.Script
import fr.acinq.bitcoin.byteVector
import com.mangala.wallet.features.chains.bitcoin.domain.utils.getChainHash

class IsValidBitcoinAddressUseCase {

    operator fun invoke(blockchainType: BlockchainType, address: String): Boolean {
        return addressToPublicKeyScript(getChainHash(blockchainType), address).right?.let { Script.write(it) }?.byteVector() != null
    }
}