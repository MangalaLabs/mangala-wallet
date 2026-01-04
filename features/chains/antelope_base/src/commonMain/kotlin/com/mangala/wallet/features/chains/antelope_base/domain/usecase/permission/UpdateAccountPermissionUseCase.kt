package com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission

import com.mangala.antelope.base.api.model.PushTransactionRequest
import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.antelope.base.domain.usecase.PushTransactionUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.CONTRACT_EOSIO
import com.mangala.wallet.features.chains.antelope_base.domain.FUNCTION_UPDATE_AUTH
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPrivateKeyUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.BaseConstructTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignTransactionUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.newaccount.AccountKeyAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.permission.AccountRequiredAuthAccountAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.permission.AccountRequiredAuthWaitAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.permission.AuthDataBody
import com.memtrip.eos.chain.actions.transaction.account.actions.permission.RequiredAuthAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.permission.RequiredAuthAccountPermissionAbi
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter

class UpdateAccountPermissionUseCase(
    getInfoUseCase: GetInfoUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val pushTransactionUseCase: PushTransactionUseCase,
    private val signTransactionUseCase: SignTransactionUseCase,
    private val getAccountPrivateKeyUseCase: GetAccountPrivateKeyUseCase
) : BaseConstructTransactionUseCase(getInfoUseCase) {

    suspend operator fun invoke(
        accountName: String,
        accountPermissionExecuted: String,
        permissionUpdated: String,
        permissionParentUpdated: String,
        threshold: Int,
        keys: List<AccountKey>,
        accounts: List<AccountAuthAccount>,
        waits: List<AccountAuthWait>,
        blockchainType: BlockchainType? = null
    ): Result<String> {
        val resolvedBlockchainType = blockchainType ?: getSelectedNetworkUseCase().blockchainType
        val accountPrivateKey = getAccountPrivateKeyUseCase(
            accountName,
            accountPermissionExecuted,
            resolvedBlockchainType
        )
            ?: run {
                return Result.failure(Throwable("get account key failed"))
            }

        val chainInfo =
            getInfoUseCase(resolvedBlockchainType)
                ?: run { return Result.failure(Throwable("get chain info failed")) }

        val authorization =
            listOf(TransactionAuthorizationAbi(accountName, accountPermissionExecuted))
        val authDataBody = buildAuthDataBody(
            accountName,
            permissionUpdated,
            permissionParentUpdated,
            threshold,
            keys,
            accounts,
            waits
        )
        val actions = listOf(
            ActionAbi(
                CONTRACT_EOSIO,
                FUNCTION_UPDATE_AUTH,
                authorization,
                constructUpdatePermissionData(authDataBody)
            )
        )

        val transaction = constructTransactionAbi(chainInfo.headBlockId.orEmpty(), actions)
        val signature = signTransactionUseCase(chainInfo.chainId, transaction, accountPrivateKey)
        return pushTransactionUseCase.withResult(
            resolvedBlockchainType,
            PushTransactionRequest(
                listOf(signature),
                "none",
                "",
                AbiBinaryGenTransactionWriter(CompressionType.NONE).squishTransactionAbi(
                    transaction
                ).toHex()
            )
        )
    }

    private fun buildAuthDataBody(
        accountName: String,
        permissionUpdated: String,
        permissionParentUpdated: String,
        threshold: Int,
        keys: List<AccountKey>,
        accounts: List<AccountAuthAccount>,
        waits: List<AccountAuthWait>
    ) = AuthDataBody(
        accountName,
        permissionUpdated,
        permissionParentUpdated,
        RequiredAuthAbi(
            threshold,
            keys.map { AccountKeyAbi(it.key, it.weight.toShort()) },
            accounts.map {
                AccountRequiredAuthAccountAbi(
                    RequiredAuthAccountPermissionAbi(
                        it.account,
                        it.permission
                    ), it.weight.toShort()
                )
            },
            waits.map { AccountRequiredAuthWaitAbi(it.waitSec, it.weight.toShort()) }
        )
    )

    private fun constructUpdatePermissionData(
        updatePermissionBody: AuthDataBody
    ): String {
        return AbiBinaryGenTransactionWriter(CompressionType.NONE).apply {
            squishAuthDataBody(
                updatePermissionBody
            )
        }.toHex()
    }
}

data class AccountKey(val key: String, val weight: Long)
data class AccountAuthAccount(val account: String, val permission: String, val weight: Long)
data class AccountAuthWait(val waitSec: Int, val weight: Long)