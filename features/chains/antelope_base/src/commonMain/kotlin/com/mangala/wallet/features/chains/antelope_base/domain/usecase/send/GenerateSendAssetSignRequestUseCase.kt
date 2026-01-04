package com.mangala.wallet.features.chains.antelope_base.domain.usecase.send

import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.antelope.base.model.SystemContracts
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.transaction.SignTransactionType
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class GenerateSendAssetSignRequestUseCase(
    private val getInfoUseCase: GetInfoUseCase
) {

    suspend operator fun invoke(
        blockchainType: BlockchainType,
        senderAccountName: String,
        signingPermissionName: String,
        recipientAccountName: String,
        quantity: Balance,
        memo: String,
        contract: String
    ): SignTransactionRequest? {
        val info = getInfoUseCase(blockchainType)

        return info?.let {
            val authorization = listOf(
                SignTransactionRequest.Authorization(
                    actor = senderAccountName,
                    permission = signingPermissionName
                )
            )
            val transferAction = SignTransactionRequest.Action.TransferToken(
                authorization = authorization,
                from = senderAccountName,
                to = recipientAccountName,
                quantity = BalanceFormatter.formatEosBalance(quantity, ignoreLocale = true),
                memo = memo,
                account = contract.ifEmpty { SystemContracts.TOKEN }
            )
            val actions: MutableList<SignTransactionRequest.Action> =
                mutableListOf(transferAction)

            SignTransactionRequest(
                signTransactionType = SignTransactionType.SEND_ASSET,
                chainId = it.chainId.orEmpty(),
                expiryTimestamp = transactionDefaultExpiry().toEpochMilliseconds(),
                authorization = authorization,
                headBlockId = it.headBlockId.orEmpty(),
                actions = actions
            )
        }
    }

    private fun transactionDefaultExpiry(): Instant = with(Clock.System.now()) {
        plus(COLD_WALLET_TRANSACTION_EXPIRY_MINUTES.toDuration(DurationUnit.MINUTES))
    }

    companion object {
        private const val COLD_WALLET_TRANSACTION_EXPIRY_MINUTES =
            5 // Needs a longer time than the default value so that users have time to scan QR
    }
}