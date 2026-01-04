package com.mangala.wallet.features.chains.antelope.ram.domain.usecase

import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionType
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.BaseGenerateSignRequestUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class GenerateSellRamSignRequestUseCase(
    getInfoUseCase: GetInfoUseCase
): BaseGenerateSignRequestUseCase(getInfoUseCase) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        senderAccountName: String,
        signingPermissionName: String,
        bytes: Long
    ): SignTransactionRequest? {
        return constructSignRequest(
            blockchainType,
            constructAuthorization = {
                constructAuthorization(senderAccountName, signingPermissionName)
            },
            constructActions = { authorization ->
                val transferAction = SignTransactionRequest.Action.SellRam(
                    authorization = authorization,
                    sellAccount = senderAccountName,
                    bytes = bytes
                )
                listOf(transferAction)
            },
            signTransactionType = SignTransactionType.SELL_RAM
        )
    }
}