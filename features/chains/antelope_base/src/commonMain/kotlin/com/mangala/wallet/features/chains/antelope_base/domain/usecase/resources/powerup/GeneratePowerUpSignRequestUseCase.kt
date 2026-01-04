package com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.powerup

import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionType
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.BaseGenerateSignRequestUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class GeneratePowerUpSignRequestUseCase(
    getInfoUseCase: GetInfoUseCase
): BaseGenerateSignRequestUseCase(getInfoUseCase) {

    suspend operator fun invoke(
        blockchainType: BlockchainType,
        senderAccountName: String,
        signingPermissionName: String,
        receiverAccountName: String,
        days: Int,
        netFrac: Long,
        cpuFrac: Long,
        maxPayment: Balance
    ): SignTransactionRequest? {
        return constructSignRequest(
            blockchainType,
            constructAuthorization = {
                constructAuthorization(senderAccountName, signingPermissionName)
            },
            constructActions = { authorization ->
                val transferAction = SignTransactionRequest.Action.PowerUp(
                    authorization = authorization,
                    authorizingAccountName = senderAccountName,
                    receiver = receiverAccountName,
                    days = days,
                    netFrac = netFrac,
                    cpuFrac = cpuFrac,
                    maxPayment = BalanceFormatter.formatEosBalance(maxPayment, ignoreLocale = true)
                )
                listOf(transferAction)
            },
            signTransactionType = SignTransactionType.POWER_UP
        )
    }
}