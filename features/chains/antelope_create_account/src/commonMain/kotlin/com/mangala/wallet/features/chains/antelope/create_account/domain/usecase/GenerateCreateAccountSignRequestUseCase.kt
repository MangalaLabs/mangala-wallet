package com.mangala.wallet.features.chains.antelope.create_account.domain.usecase

import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionType
import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.CreateAccountConstants.NEW_ACCOUNT_RAM_BYTES
import com.mangala.wallet.features.chains.antelope_base.domain.model.CreateAccountRamOption
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class GenerateCreateAccountSignRequestUseCase(
    private val getInfoUseCase: GetInfoUseCase
) {

    suspend operator fun invoke(
        blockchainType: BlockchainType,
        creatorAccountName: String,
        signingPermissionName: String,
        newAccountOwnerPublicKey: String,
        newAccountActivePublicKey: String,
        accountName: String,
        createAccountRamOption: CreateAccountRamOption = CreateAccountRamOption.BUY_RAM
    ): SignTransactionRequest? {
        val info = getInfoUseCase(blockchainType)

        return info?.let {
            val authorization = listOf(
                SignTransactionRequest.Authorization(
                    actor = creatorAccountName,
                    permission = signingPermissionName
                )
            )
            val createAccountAction = SignTransactionRequest.Action.CreateAccount(
                authorization = authorization,
                accountCreator = creatorAccountName,
                newAccountName = accountName,
                ownerPublicKey = newAccountOwnerPublicKey,
                activePublicKey = newAccountActivePublicKey
            )
            val actions: MutableList<SignTransactionRequest.Action> = mutableListOf(createAccountAction)
            if (createAccountRamOption == CreateAccountRamOption.BUY_RAM) {
                actions.add(
                    SignTransactionRequest.Action.BuyRamBytes(
                        authorization = authorization,
                        payer = creatorAccountName,
                        receiver = accountName,
                        bytes = NEW_ACCOUNT_RAM_BYTES
                    )
                )
            } else {
                actions.add(
                    SignTransactionRequest.Action.TransferRam(
                        authorization = authorization,
                        from = creatorAccountName,
                        memo = "",
                        to = accountName,
                        bytes = NEW_ACCOUNT_RAM_BYTES
                    )
                )
            }
            SignTransactionRequest(
                signTransactionType = SignTransactionType.CREATE_ACCOUNT,
                chainId = it.chainId.orEmpty(),
                expiryTimestamp = transactionDefaultExpiry().toEpochMilliseconds(),
                authorization = authorization,
                headBlockId = it.headBlockId.orEmpty(),
                actions = actions
            )
        }
    }

    private fun transactionDefaultExpiry(): Instant = with(Clock.System.now()) {
        plus(com.mangala.wallet.features.chains.antelope.create_account.domain.usecase.GenerateCreateAccountSignRequestUseCase.COLD_WALLET_TRANSACTION_EXPIRY_MINUTES.toDuration(DurationUnit.MINUTES))
    }

    companion object {
        private const val COLD_WALLET_TRANSACTION_EXPIRY_MINUTES = 5 // Needs a longer time than the default value so that users have time to scan QR
    }
}