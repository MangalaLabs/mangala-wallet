package com.mangala.wallet.features.chains.antelope_base.domain.usecase

import com.mangala.antelope.base.api.model.PushTransactionRequest
import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.antelope.base.domain.usecase.PushTransactionUseCase
import com.mangala.wallet.domain.portfolio.usecases.EnsureAccountInPortfolioUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.BaseConstructTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignTransactionUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.buyram.BuyRamBytesArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.newaccount.AccountKeyAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.newaccount.AccountRequiredAuthAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.newaccount.NewAccountArgs
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter
import com.memtrip.eos.core.crypto.EosPrivateKey

class CreateFreeJungleTestnetAccountUseCase(
    getInfoUseCase: GetInfoUseCase,
    private val pushTransactionUseCase: PushTransactionUseCase,
    private val signTransactionUseCase: SignTransactionUseCase,
    private val ensureAccountInPortfolioUseCase: EnsureAccountInPortfolioUseCase
): BaseConstructTransactionUseCase(getInfoUseCase) {

    suspend operator fun invoke(
        blockchainType: BlockchainType,
        ownerPublicKey: String,
        activePublicKey: String,
        accountName: String
    ): Result<String> {
        val trimmedAccountName = accountName.trim()

        val authorizingPrivateKey =
            EosPrivateKey("5J2egezDSwfrWdRMj515BdaG3o7WahnMBRAkxGteqKoSioGYaa4") // TODO: Remove when we have IAP implemented

        val response = getInfoUseCase.invoke(blockchainType)
        if (response != null) {
            val contract = "eosio"
            val accountCreator = "mangalaprovn"

            val authorization = listOf(
                TransactionAuthorizationAbi(
                    accountCreator,
                    "active"
                )
            )

            val actions = listOf(
                ActionAbi(
                    contract,
                    "newaccount",
                    authorization,
                    constructCreateAccountData(
                        creator = accountCreator,
                        accountName = trimmedAccountName,
                        ownerPublicKey = ownerPublicKey,
                        activePublicKey = activePublicKey
                    )
                ),
                ActionAbi(
                    contract,
                    "buyrambytes",
                    authorization,
                    constructBuyRamBytesData(
                        creator = accountCreator,
                        accountName = trimmedAccountName,
                        amountRamInBytes = 1600
                    )
                )
            )
            val transaction = constructTransactionAbi(response.headBlockId.orEmpty(), actions)

            val signature = signTransactionUseCase(response.chainId, transaction, authorizingPrivateKey)

            val pushResult = pushTransactionUseCase.invoke(
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
            
            // If transaction was successful, add account to portfolio
            if (pushResult != null) {
                ensureAccountInPortfolioUseCase(
                    accountName = trimmedAccountName,
                    blockchainType = blockchainType
                )
            }
        }

        return Result.success("Account created")
    }

    private fun constructCreateAccountData(
        creator: String,
        accountName: String,
        ownerPublicKey: String,
        activePublicKey: String
    ): String {
        return AbiBinaryGenTransactionWriter(CompressionType.NONE).apply {
            squishNewAccountArgs(
                constructNewAccountArgs(
                    creator,
                    accountName,
                    ownerPublicKey,
                    activePublicKey
                )
            )
        }.toHex()
    }

    private fun constructBuyRamBytesData(
        creator: String,
        accountName: String,
        amountRamInBytes: Long
    ): String {
        return AbiBinaryGenTransactionWriter(CompressionType.NONE).apply {
            squishBuyRamBytesArgs(BuyRamBytesArgs(creator, accountName, amountRamInBytes))
        }.toHex()
    }

    private fun constructNewAccountArgs(
        creator: String,
        accountName: String,
        ownerPublicKey: String,
        activePublicKey: String
    ) = NewAccountArgs(
        creator = creator,
        name = accountName,
        owner = AccountRequiredAuthAbi(
            threshold = 1,
            keys = listOf(
                AccountKeyAbi(
                    key = ownerPublicKey,
                    weight = 1
                )
            ),
            accounts = emptyList(),
            waits = emptyList()
        ),
        active = AccountRequiredAuthAbi(
            threshold = 1,
            keys = listOf(
                AccountKeyAbi(
                    key = activePublicKey,
                    weight = 1
                )
            ),
            accounts = emptyList(),
            waits = emptyList()
        )
    )
}