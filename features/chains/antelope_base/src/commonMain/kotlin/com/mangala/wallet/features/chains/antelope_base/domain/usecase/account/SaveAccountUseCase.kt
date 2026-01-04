package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.antelope_key_manager.EosKeyManager
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountPermissionRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountRepository
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.notification.RegisterAntelopeNotificationUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.core.crypto.EosPrivateKey
import kotlinx.datetime.Clock

class SaveAccountUseCase(
    private val accountRepository: AccountRepository,
    private val accountPermissionRepository: AccountPermissionRepository,
    private val eosKeyManager: EosKeyManager,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val registerAntelopeNotificationUseCase: RegisterAntelopeNotificationUseCase
) {

    suspend operator fun invoke(
        accountName: String,
        createAccountState: AntelopeAccount.CreateAccountState,
        purchaseToken: String?,
        purchaseId: String?,
        isReplace: Boolean = false
    ) {
        invoke(
            accountName = accountName,
            activePublicKey = null,
            ownerPublicKey = null,
            activeKeySynced = false,
            ownerKeySynced = false,
            isTemp = true,
            createAccountState = createAccountState,
            purchaseToken = purchaseToken,
            purchaseId = purchaseId,
            isReplace = isReplace
        )
    }

    suspend operator fun invoke(
        accountName: String,
        activePrivateKey: EosPrivateKey?,
        ownerPrivateKey: EosPrivateKey?,
        isTemp: Boolean = false,
        createAccountState: AntelopeAccount.CreateAccountState,
        purchaseToken: String? = null,
        isReplace: Boolean = false,
        blockchainType: BlockchainType? = null,
    ) {
        val activeKeySynced = activePrivateKey?.let { eosKeyManager.importPrivateKey(it) }
        val ownerKeySynced = ownerPrivateKey?.let { eosKeyManager.importPrivateKey(it) }

        invoke(
            accountName = accountName,
            activePublicKey = activePrivateKey?.publicKey?.toString(),
            ownerPublicKey = ownerPrivateKey?.publicKey?.toString(),
            activeKeySynced = activeKeySynced != null,
            ownerKeySynced = ownerKeySynced != null,
            isTemp = isTemp,
            createAccountState = createAccountState,
            purchaseToken = purchaseToken,
            blockchainType = blockchainType,
            isReplace = isReplace
        )
    }

    suspend operator fun invoke(
        accountName: String,
        activePublicKey: String?,
        ownerPublicKey: String? = null,
        activeKeySynced: Boolean = false,
        ownerKeySynced: Boolean = false,
        isTemp: Boolean,
        createAccountState: AntelopeAccount.CreateAccountState,
        purchaseToken: String? = null,
        purchaseId: String? = null,
        isReplace: Boolean = false,
        blockchainType: BlockchainType? = null,
    ) {
        val resolvedBlockchainType = blockchainType ?: getSelectedNetworkUseCase().blockchainType

        // TODO: Change param to EosPublicKey so that key is validated and preventing passing invalid keys
        val permissions = mutableListOf<AntelopeAccountPermission>()

        getActiveKeyPermission(activePublicKey, isSynced = activeKeySynced)?.let {
            permissions.add(it)
        }
        getOwnerKeyPermission(ownerPublicKey, isSynced = ownerKeySynced)?.let {
            permissions.add(it)
        }

        val account = AntelopeAccount(
            accountName.trim(),
            permissions,
            isActive = true,
            isTemp = isTemp,
            createAccountState = createAccountState,
            purchaseToken = purchaseToken,
            purchaseId = purchaseId,
            coreLiquidBalance = null,
            cpuLimit = null,
            netLimit = null,
            ramQuota = null,
            ramUsage = null,
            rexBalance = null,
            selfDelegatedBandwidthCpuWeight = null,
            selfDelegatedBandwidthNetWeight = null,
            totalResources = null,
            lastUpdated = Clock.System.now(),
            isNotificationRegistered = false
        )
        accountRepository.insertAccount(account, resolvedBlockchainType, isReplace)
        accountPermissionRepository.insertAccountPermissions(
            account.permissions,
            account.accountName,
            resolvedBlockchainType.uid
        )

        if (isTemp.not())
            registerAntelopeNotificationUseCase(accountName, resolvedBlockchainType)
    }

    suspend operator fun invoke(
        accountName: String,
        activePrivateKey: EosPrivateKey?,
        ownerPrivateKey: EosPrivateKey?,
        blockchainType: BlockchainType,
        isTemp: Boolean = false,
        createAccountState: AntelopeAccount.CreateAccountState
    ) {
        val activeKeySynced = activePrivateKey?.let { eosKeyManager.importPrivateKey(it) }
        val ownerKeySynced = ownerPrivateKey?.let { eosKeyManager.importPrivateKey(it) }

        // TODO: Change param to EosPublicKey so that key is validated and preventing passing invalid keys
        val permissions = mutableListOf<AntelopeAccountPermission>()
        val activePublicKey = activePrivateKey?.publicKey?.toString()
        val ownerPublicKey = ownerPrivateKey?.publicKey?.toString()

        getActiveKeyPermission(activePublicKey, isSynced = activeKeySynced != null)?.let {
            permissions.add(it)
        }
        getOwnerKeyPermission(ownerPublicKey, isSynced = ownerKeySynced != null)?.let {
            permissions.add(it)
        }

        val account = AntelopeAccount(
            accountName.trim(),
            permissions,
            isActive = true,
            isTemp = isTemp,
            coreLiquidBalance = null,
            cpuLimit = null,
            netLimit = null,
            ramQuota = null,
            ramUsage = null,
            rexBalance = null,
            selfDelegatedBandwidthCpuWeight = null,
            selfDelegatedBandwidthNetWeight = null,
            totalResources = null,
            lastUpdated = Clock.System.now(),
            createAccountState = createAccountState,
            isNotificationRegistered = false
        )
        accountRepository.insertAccount(
            account = account,
            blockchainType = blockchainType,
            isReplace = true
        )
        accountPermissionRepository.insertAccountPermissions(
            account.permissions,
            account.accountName,
            blockchainType.uid
        )

        if (isTemp.not())
            registerAntelopeNotificationUseCase(accountName, blockchainType)

    }

    suspend operator fun invoke(
        accountName: String,
        privateKey: EosPrivateKey,
        permissions: List<AntelopeAccountPermission>,
        blockchainType: BlockchainType? = null,
        isTemp: Boolean = false,
    ) {
        val resolvedBlockchainType = blockchainType ?: getSelectedNetworkUseCase().blockchainType
        eosKeyManager.importPrivateKey(privateKey)

        val account = AntelopeAccount(
            accountName.trim(),
            permissions,
            isActive = true,
            isTemp = isTemp,
            coreLiquidBalance = null,
            cpuLimit = null,
            netLimit = null,
            ramQuota = null,
            ramUsage = null,
            rexBalance = null,
            selfDelegatedBandwidthCpuWeight = null,
            selfDelegatedBandwidthNetWeight = null,
            totalResources = null,
            lastUpdated = Clock.System.now(),
            createAccountState = AntelopeAccount.CreateAccountState.DONE,
            isNotificationRegistered = false
        )
        accountRepository.insertAccount(
            account = account,
            blockchainType = resolvedBlockchainType,
            isReplace = false
        )
        accountPermissionRepository.insertAccountPermissions(
            account.permissions,
            account.accountName,
            resolvedBlockchainType.uid
        )

        if (isTemp.not())
            registerAntelopeNotificationUseCase(accountName, resolvedBlockchainType)

    }

    private fun getActiveKeyPermission(
        activePublicKey: String?,
        isSynced: Boolean
    ): AntelopeAccountPermission? {
        return activePublicKey?.let {
            AntelopeAccountPermission.createInitialActivePermission(it, isSynced)
        }
    }

    private fun getOwnerKeyPermission(
        ownerPublicKey: String?,
        isSynced: Boolean
    ): AntelopeAccountPermission? {
        return ownerPublicKey?.let {
            AntelopeAccountPermission.createInitialOwnerPermission(it, isSynced)
        }
    }
}