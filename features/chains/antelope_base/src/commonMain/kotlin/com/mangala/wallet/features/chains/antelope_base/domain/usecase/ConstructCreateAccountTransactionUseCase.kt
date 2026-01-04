package com.mangala.wallet.features.chains.antelope_base.domain.usecase

import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.CreateAccountConstants.NEW_ACCOUNT_RAM_BYTES
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.BaseConstructTransactionUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.buyram.BuyRamBytesArgs
import com.memtrip.eos.chain.actions.transaction.account.actions.newaccount.AccountKeyAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.newaccount.AccountRequiredAuthAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.newaccount.NewAccountArgs
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter

class ConstructCreateAccountTransactionUseCase(
    getInfoUseCase: GetInfoUseCase
): BaseConstructTransactionUseCase(getInfoUseCase) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        ownerPublicKey: String,
        activePublicKey: String,
        newAccountName: String,
        creatorAccountName: String,
        permissionName: String
    ): TransactionAbi? {
        return getInfoUseCase(blockchainType)?.let {
            val trimmedAccountName = newAccountName.trim()
            val authorization = createAuthorization(creatorAccountName, permissionName)
            constructTransactionAbi(
                it.headBlockId.orEmpty(),
                createActionsList(
                    authorization,
                    creatorAccountName,
                    trimmedAccountName,
                    ownerPublicKey,
                    activePublicKey
                )
            )
        }
    }

    fun createAuthorization(
        creatorAccountName: String,
        permissionName: String
    ): List<TransactionAuthorizationAbi> {
        return listOf(
            TransactionAuthorizationAbi(
                creatorAccountName,
                permissionName
            )
        )
    }

    fun createActionsList(
        authorization: List<TransactionAuthorizationAbi>,
        accountCreator: String,
        trimmedAccountName: String,
        ownerPublicKey: String,
        activePublicKey: String
    ): List<ActionAbi> {
        return listOf(
            constructCreateAccountActionAbi(
                authorization,
                accountCreator,
                trimmedAccountName,
                ownerPublicKey,
                activePublicKey
            ),
            constructBuyRamBytesActionAbi(
                authorization,
                accountCreator,
                trimmedAccountName
            )
        )
    }

    private fun constructCreateAccountActionAbi(
        authorization: List<TransactionAuthorizationAbi>,
        accountCreator: String,
        trimmedAccountName: String,
        ownerPublicKey: String,
        activePublicKey: String
    ): ActionAbi {
        return ActionAbi(
            CREATE_ACCOUNT_CONTRACT,
            "newaccount",
            authorization,
            constructCreateAccountData(
                creator = accountCreator,
                accountName = trimmedAccountName,
                ownerPublicKey = ownerPublicKey,
                activePublicKey = activePublicKey
            )
        )
    }

    private fun constructBuyRamBytesActionAbi(
        authorization: List<TransactionAuthorizationAbi>,
        accountCreator: String,
        trimmedAccountName: String
    ): ActionAbi {
        return ActionAbi(
            CREATE_ACCOUNT_CONTRACT,
            "buyrambytes",
            authorization,
            constructBuyRamBytesData(
                creator = accountCreator,
                accountName = trimmedAccountName,
                amountRamInBytes = NEW_ACCOUNT_RAM_BYTES
            )
        )
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

    companion object {
        private const val CREATE_ACCOUNT_CONTRACT = "eosio"
    }
}