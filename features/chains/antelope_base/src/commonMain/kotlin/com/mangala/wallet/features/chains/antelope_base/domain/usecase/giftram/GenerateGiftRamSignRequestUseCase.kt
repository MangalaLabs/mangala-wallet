package com.mangala.wallet.features.chains.antelope_base.domain.usecase.giftram

import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionType
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.BaseGenerateSignRequestUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class GenerateGiftRamSignRequestUseCase(
    getInfoUseCase: GetInfoUseCase
) : BaseGenerateSignRequestUseCase(getInfoUseCase) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        senderAccountName: String,
        signingPermissionName: String,
        bytes: Long,
        memo: String,
        recipientAccountName: String,
    ): SignTransactionRequest? {
        return constructSignRequest(
            blockchainType,
            constructAuthorization = {
                constructAuthorization(senderAccountName, signingPermissionName)
            },
            constructActions = { authorization ->
                val giftRamAction = SignTransactionRequest.Action.GiftRam(
                    authorization = authorization,
                    ramBytes = bytes,
                    from = senderAccountName,
                    memo = memo,
                    to = recipientAccountName
                )
                listOf(giftRamAction)
            },
            signTransactionType = SignTransactionType.GIFT_RAM
        )
    }
}