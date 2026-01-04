package com.mangala.wallet.features.chains.antelope.domain.usecase.account

import com.mangala.wallet.domain.datastore.usecases.SaveSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AntelopeAccountByAuthorizer
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.keycert.DecryptKeyCertUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.BuildEnvironmentProvider
import com.memtrip.eos.chain.actions.keycert.KeyCertArgs

class ImportAccountFromKeyCertUseCase(
    private val importAccountUseCase: ImportAccountUseCase,
    private val decryptKeyCertUseCase: DecryptKeyCertUseCase,
    private val getAccountInfoUseCase: GetAccountInfoUseCase,
    private val saveSelectedNetworkUseCase: SaveSelectedNetworkUseCase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider
) {
    suspend operator fun invoke(
        keyCert: KeyCertArgs,
        encryptionKeyWords: List<String>
    ): Result<Unit> {
        val blockchainType = BlockchainType.fromChainId(keyCert.chainId)

        val privateKey = decryptKeyCertUseCase.decryptKeyCert(
            keyCert,
            encryptionKeyWords
        )

        val accountInfo = getAccountInfoUseCase(blockchainType, keyCert.permissionLevel.actor)

        val permission =
            accountInfo?.permissions?.find { it.permName == keyCert.permissionLevel.permission && it.requiredAuth?.keys?.any { it.key == privateKey.publicKey.toString() || it.key == privateKey.publicKey.toLegacyString() } == true }
                ?: return Result.failure(Exception("Permission not found in account"))
        val key =
            permission.requiredAuth?.keys?.find { it.key == privateKey.publicKey.toString() || it.key == privateKey.publicKey.toLegacyString() }
                ?: return Result.failure(Exception("Key not found in permission"))

        importAccountUseCase(
            accountName = keyCert.permissionLevel.actor,
            privateKey = privateKey.toString(),
            authorizers = listOf(
                AntelopeAccountByAuthorizer(
                    accountName = keyCert.permissionLevel.actor,
                    permissionName = keyCert.permissionLevel.permission,
                    authorizingKey = key.key.orEmpty(),
                    weight = key.weight ?: 0,
                    threshold = permission.requiredAuth?.threshold ?: 0,
                    blockchainUid = blockchainType.uid
                )
            ),
            isTemp = false,
            blockchainType = blockchainType
        )

        val isDevelopmentEnvironment = buildEnvironmentProvider.isDevelopmentEnvironment()
        BlockchainNetworkData.getBlockchainByUid(blockchainType.uid, isDevelopmentEnvironment)
            ?.let { saveSelectedNetworkUseCase(it) }

        return Result.success(Unit)
    }
}