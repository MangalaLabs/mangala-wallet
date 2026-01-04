package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlin.random.Random

class GenerateRandomAccountNameUseCase {

    operator fun invoke(accountType: AccountNameType, blockchainType: BlockchainType): String {
        val premiumAccountSuffix = AntelopeAccount.getPremiumAccountSuffix(blockchainType)

        val prefixLength = if (accountType == AccountNameType.Premium) {
            AntelopeAccount.MAX_LENGTH_ACCOUNT_NAME - premiumAccountSuffix.length
        } else {
            AntelopeAccount.MAX_LENGTH_ACCOUNT_NAME
        }

        return buildString {
            while (length < prefixLength) {
                val randomIndex = Random.nextInt(AntelopeAccount.VALID_CHARACTERS.length)
                val char = AntelopeAccount.VALID_CHARACTERS[randomIndex]
                if (isEmpty() && char in '1'..'5') continue // First character cannot be a number
                append(char)
            }
        }
    }
}