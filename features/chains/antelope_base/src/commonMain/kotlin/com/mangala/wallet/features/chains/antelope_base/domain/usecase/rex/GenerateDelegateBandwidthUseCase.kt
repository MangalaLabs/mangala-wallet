package com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex

import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionType
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.BaseGenerateSignRequestUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class GenerateDelegateBandwidthUseCase(
    getInfoUseCase: GetInfoUseCase
) : BaseGenerateSignRequestUseCase(getInfoUseCase) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        senderAccountName: String,
        signingPermissionName: String,
        receiverAccountName: String,
        stakeCpuQuantity: String,
        stakeNetQuantity: String,
        transfer: Int
    ): SignTransactionRequest? {
        return constructSignRequest(
            blockchainType,
            constructAuthorization = {
                constructAuthorization(senderAccountName, signingPermissionName)
            },
            constructActions = { authorization ->
                val transferAction = SignTransactionRequest.Action.DelegateRex(
                    authorization = authorization,
                    authorizingAccountName = senderAccountName,
                    receiver = receiverAccountName,
                    stakeCpuQuantity = stakeCpuQuantity,
                    stakeNetQuantity = stakeNetQuantity,
                    transfer = transfer
                )
                listOf(transferAction)
            },
            signTransactionType = SignTransactionType.DELEGATE_BANDWIDTH
        )
    }
}