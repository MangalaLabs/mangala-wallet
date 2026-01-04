package com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction

import com.mangala.wallet.antelope_key_manager.EosKeyManager
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AntelopeKeyRepository
import com.memtrip.eos.abi.writer.bytewriter.DefaultByteWriter
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.transaction.abi.SignedTransactionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAbi
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter
import com.memtrip.eos.core.crypto.EosPrivateKey
import com.memtrip.eos.core.crypto.signature.PrivateKeySigning
import com.memtrip.eos.core.hex.DefaultHexWriter

class SignTransactionUseCase(
    private val eosKeyManager: EosKeyManager,
    private val antelopeKeyRepository: AntelopeKeyRepository
) {
    suspend operator fun invoke(chainId: String?, transactionAbi: TransactionAbi, actor: String, permissionName: String, blockchainUid: String): String {
        val firstSyncedKey = antelopeKeyRepository.getSyncedKeysByAccountNameAndPermissionName(actor, permissionName, blockchainUid).firstOrNull()
        val publicKey = firstSyncedKey?.key.orEmpty()
        val privateKey = eosKeyManager.getPrivateKey(publicKey)
        return PrivateKeySigning().sign(
            AbiBinaryGenTransactionWriter(
                DefaultByteWriter(),
                DefaultHexWriter(),
                CompressionType.NONE
            ).squishSignedTransactionAbi(
                SignedTransactionAbi(
                    chainId.orEmpty(),
                    transactionAbi,
                    emptyList()
                )
            ).toBytes(), privateKey
        )
    }

    operator fun invoke(chainId: String?, transactionAbi: TransactionAbi, privateKey: EosPrivateKey): String {
        return PrivateKeySigning().sign(
            AbiBinaryGenTransactionWriter(
                DefaultByteWriter(),
                DefaultHexWriter(),
                CompressionType.NONE
            ).squishSignedTransactionAbi(
                SignedTransactionAbi(
                    chainId.orEmpty(),
                    transactionAbi,
                    emptyList()
                )
            ).toBytes(), privateKey
        )
    }
}