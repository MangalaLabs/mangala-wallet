package com.mangala.wallet.features.evm_snap.domain.usecase

import com.mangala.wallet.antelope_key_manager.domain.model.AccountKeyPairs
import com.mangala.wallet.core.hdwallet.domain.usecases.GenerateHDKeyUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.Blockchain
import com.mangala.wallet.model.wallet.domain.WalletModel
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.KeyType

class GetEosPrivateKeyFromEvmUseCase(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val generateHDKeyUseCase: GenerateHDKeyUseCase
) {

    suspend fun getEosKeyPairs(walletModel: WalletModel): AccountKeyPairs {
        val ownerPrivateKey = getEosPrivateKey(walletModel, EOS_OWNER_PRIVATE_KEY_INDEX)
        val activePrivateKey = getEosPrivateKey(walletModel, EOS_ACTIVE_PRIVATE_KEY_INDEX)
        return AccountKeyPairs(ownerPrivateKey, activePrivateKey)
    }

    suspend fun getOwnerPrivateKey(walletModel: WalletModel): String {
        return getEosPrivateKey(walletModel, EOS_OWNER_PRIVATE_KEY_INDEX).toLegacyString()
    }

    suspend fun getActivePrivateKey(walletModel: WalletModel): String {
        return getEosPrivateKey(walletModel, EOS_ACTIVE_PRIVATE_KEY_INDEX).toLegacyString()
    }

    private suspend fun getEosPrivateKey(walletModel: WalletModel, eosPrivateKeyTypeIndex: Int): EosPrivateKey {
        val seedPhrase = walletModel.words.split(" ")
        val currentNetwork = getSelectedNetworkUseCase.invoke().blockchainType
        val hdKey = generateHDKeyUseCase.invoke(
            seedPhrase = seedPhrase,
            blockchain = Blockchain(currentNetwork, "", null),
            addressType = AddressType.Bip44,
            derivationPathIndex = eosPrivateKeyTypeIndex
        )
        return EosPrivateKey(hdKey.privateKey, keyType = KeyType.LEGACY)
    }

    companion object {
        private const val EOS_OWNER_PRIVATE_KEY_INDEX = 0
        private const val EOS_ACTIVE_PRIVATE_KEY_INDEX = 1
    }
}