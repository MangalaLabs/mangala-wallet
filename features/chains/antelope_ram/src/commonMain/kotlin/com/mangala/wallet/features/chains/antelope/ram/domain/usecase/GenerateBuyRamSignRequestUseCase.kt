package com.mangala.wallet.features.chains.antelope.ram.domain.usecase

import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionType
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.BaseGenerateSignRequestUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class GenerateBuyRamSignRequestUseCase(
    getInfoUseCase: GetInfoUseCase
): BaseGenerateSignRequestUseCase(getInfoUseCase) {
    suspend fun generateBuyRamRequest(
        blockchainType: BlockchainType,
        senderAccountName: String,
        signingPermissionName: String,
        receiver: String,
        quantity: String
    ): SignTransactionRequest? {
        return constructSignRequest(
            blockchainType,
            constructAuthorization = {
                constructAuthorization(senderAccountName, signingPermissionName)
            },
            constructActions = { authorization ->
                val transferAction = SignTransactionRequest.Action.BuyRam(
                    authorization = authorization,
                    receiver = receiver,
                    payer = senderAccountName,
                    quantity = quantity
                )
                listOf(transferAction)
            },
            signTransactionType = SignTransactionType.BUY_RAM
        )
    }

    suspend fun generateBuyRamBytesRequest(
        blockchainType: BlockchainType,
        senderAccountName: String,
        signingPermissionName: String,
        receiver: String,
        bytes: Long
    ): SignTransactionRequest? {
        return constructSignRequest(
            blockchainType,
            constructAuthorization = {
                constructAuthorization(senderAccountName, signingPermissionName)
            },
            constructActions = { authorization ->
                val transferAction = SignTransactionRequest.Action.BuyRamBytes(
                    authorization = authorization,
                    receiver = receiver,
                    payer = senderAccountName,
                    bytes = bytes
                )
                listOf(transferAction)
            },
            signTransactionType = SignTransactionType.BUY_RAM_BYTES
        )
    }
}