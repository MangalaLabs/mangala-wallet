package com.mangala.wallet.features.chains.antelope.domain.usecase.account

import com.benasher44.uuid.uuid4
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.portfolio.usecases.EnsureAccountInPortfolioUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeKey
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopePermissionType
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeRequiredAuth
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AntelopeAccountByAuthorizer
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.core.crypto.EosPrivateKey

class ImportAccountUseCase(
    private val saveAccountUseCase: SaveAccountUseCase,
    private val getAccountInfoUseCase: GetAccountInfoUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val ensureAccountInPortfolioUseCase: EnsureAccountInPortfolioUseCase
) {

    suspend operator fun invoke(
        accountName: String,
        privateKey: String,
        authorizers: List<AntelopeAccountByAuthorizer>,
        isTemp: Boolean,
        blockchainType: BlockchainType? = null
    ): Result<Unit> {
        val filteredAuthorizers = authorizers.filter { it.accountName == accountName }

        if (filteredAuthorizers.isEmpty()) return Result.failure(Throwable("No authorizer associated with account name"))

        val resolvedBlockchainType = blockchainType ?: getSelectedNetworkUseCase().blockchainType

        val privateKeyLegacyString = EosPrivateKey.fromString(privateKey).toLegacyString()
        val privateKeyLegacy = EosPrivateKey.fromString(privateKeyLegacyString)

        val permissions = mutableListOf<AntelopeAccountPermission>()
        val permissionMap =
            if (authorizers.any {
                    it.permissionName != AntelopePermissionType.Owner.permissionName
                            && it.permissionName != AntelopePermissionType.Active.permissionName
                })
                getAccountInfoUseCase(
                    accountName = accountName,
                    blockchainType = resolvedBlockchainType
                )?.permissions?.associateBy { it.permName } ?: emptyMap()
            else emptyMap()

        authorizers.forEach {
            when (it.permissionName) {
                AntelopePermissionType.Owner.permissionName -> {
                    permissions.add(
                        AntelopeAccountPermission(
                            AntelopePermissionType.Owner,
                            AntelopePermissionType.fromName(""),
                            AntelopeRequiredAuth(
                                threshold = 0,
                                keys = listOf(
                                    AntelopeKey(
                                        uuid4().toString(),
                                        key = privateKeyLegacy.publicKey.toString(),
                                        it.weight,
                                        isSynced = true
                                    )
                                ),
                                accounts = emptyList(),
                                waits = emptyList()
                            ),
                            emptyList()
                        )
                    )
                }

                AntelopePermissionType.Active.permissionName -> {
                    permissions.add(
                        AntelopeAccountPermission(
                            AntelopePermissionType.Active,
                            AntelopePermissionType.Owner,
                            AntelopeRequiredAuth(
                                threshold = 0,
                                keys = listOf(
                                    AntelopeKey(
                                        uuid4().toString(),
                                        key = privateKeyLegacy.publicKey.toString(),
                                        it.weight,
                                        isSynced = true
                                    )
                                ),
                                accounts = emptyList(),
                                waits = emptyList()
                            ),
                            emptyList()
                        )
                    )
                }

                else -> {
                    val permission = permissionMap[it.permissionName]
                    if (permission != null) {
                        permissions.add(
                            AntelopeAccountPermission(
                                AntelopePermissionType.fromName(it.permissionName),
                                AntelopePermissionType.fromName(permission.parent ?: ""),
                                AntelopeRequiredAuth(
                                    threshold = 0,
                                    keys = listOf(
                                        AntelopeKey(
                                            uuid4().toString(),
                                            key = privateKeyLegacy.publicKey.toString(),
                                            it.weight,
                                            isSynced = true
                                        )
                                    ),
                                    accounts = emptyList(),
                                    waits = emptyList()
                                ),
                                emptyList()
                            )
                        )
                    }
                }
            }
        }

        saveAccountUseCase(
            accountName = accountName,
            privateKey = privateKeyLegacy,
            blockchainType = blockchainType,
            isTemp = isTemp,
            permissions = permissions
        )

        // Add account to portfolio after successful import (only for non-temp accounts)
        if (!isTemp) {
            ensureAccountInPortfolioUseCase(
                accountName = accountName,
                blockchainType = resolvedBlockchainType
            )
        }

        return Result.success(Unit)
    }
}