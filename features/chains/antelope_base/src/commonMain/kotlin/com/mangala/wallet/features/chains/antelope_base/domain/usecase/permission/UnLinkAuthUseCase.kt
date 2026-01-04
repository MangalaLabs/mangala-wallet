package com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission

import com.mangala.antelope.base.api.model.PushTransactionRequest
import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.antelope.base.domain.usecase.PushTransactionUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.CONTRACT_EOSIO
import com.mangala.wallet.features.chains.antelope_base.domain.FUNCTION_UN_LINK_AUTH
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPrivateKeyUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.BaseConstructTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignTransactionUseCase
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.permission.UnLinkAuthAbi
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter

class UnLinkAuthUseCase(
    getInfoUseCase: GetInfoUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val pushTransactionUseCase: PushTransactionUseCase,
    private val signTransactionUseCase: SignTransactionUseCase,
    private val getAccountPrivateKeyUseCase: GetAccountPrivateKeyUseCase
) : BaseConstructTransactionUseCase(getInfoUseCase) {
    suspend operator fun invoke(
        accountName: String,
        accountPermissionExecuted: String,
        contract: String,
        action: String
    ): Result<String> {
        val accountPrivateKey = getAccountPrivateKeyUseCase(accountName, accountPermissionExecuted)
            ?: run {
                return Result.failure(Throwable("get account key failed"))
            }
        val blockchainType = getSelectedNetworkUseCase().blockchainType

        val chainInfo =
            getInfoUseCase(blockchainType)
                ?: run { return Result.failure(Throwable("get chain info failed")) }

        val authorization =
            listOf(TransactionAuthorizationAbi(accountName, accountPermissionExecuted))
        val unLinkAuthAbi = UnLinkAuthAbi(
            accountName,
            contract,
            action
        )
        val actions = listOf(
            ActionAbi(
                CONTRACT_EOSIO,
                FUNCTION_UN_LINK_AUTH,
                authorization,
                constructUnLinkAuthData(unLinkAuthAbi)
            )
        )
        val transaction = constructTransactionAbi(chainInfo.headBlockId.orEmpty(), actions)
        val signature = signTransactionUseCase(chainInfo.chainId, transaction, accountPrivateKey)

        return pushTransactionUseCase.withResult(
            blockchainType,
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

    private fun constructUnLinkAuthData(
        unLinkAuthAbi: UnLinkAuthAbi
    ): String {
        return AbiBinaryGenTransactionWriter(CompressionType.NONE).apply {
            squishUnLinkAuthAbi(
                unLinkAuthAbi
            )
        }.toHex()
    }
}