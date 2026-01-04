package com.mangala.wallet.features.chains.antelope_base.domain.usecase.rentViaRex

import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionType
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.BaseGenerateSignRequestUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class GenerateRentViaRexUseCase(
    getInfoUseCase: GetInfoUseCase
) : BaseGenerateSignRequestUseCase(getInfoUseCase) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        senderAccountName: String,
        signingPermissionName: String,
        receiver: String,
        loadFund: String,
        loadAmount: String,
        isCpu: Boolean
    ): SignTransactionRequest? {
        return constructSignRequest(
            blockchainType,
            constructAuthorization = {
                constructAuthorization(senderAccountName, signingPermissionName)
            },
            constructActions = { authorization ->
                val transferAction = SignTransactionRequest.Action.DepositRex(
                    authorization = authorization,
                    authorizingAccountName = senderAccountName,
                    amount = loadAmount
                )
                val actions: MutableList<SignTransactionRequest.Action> = mutableListOf(transferAction)

                actions.add(
                    if (isCpu) {
                        SignTransactionRequest.Action.RentViaRexCpu(
                            authorization = authorization,
                            authorizingAccountName = senderAccountName,
                            receiver = receiver,
                            loadFund = loadFund.replace("A", "EOS"),
                            loadAmount = loadAmount.replace("A", "EOS")
                        )
                    } else {
                        SignTransactionRequest.Action.RentViaRexNet(
                            authorization = authorization,
                            authorizingAccountName = senderAccountName,
                            receiver = receiver,
                            loadFund = loadFund.replace("A", "EOS"),
                            loadAmount = loadAmount.replace("A", "EOS")
                        )
                    }
                )
                actions
            },
            signTransactionType = SignTransactionType.RENT_VIA_REX
        )
    }
}
