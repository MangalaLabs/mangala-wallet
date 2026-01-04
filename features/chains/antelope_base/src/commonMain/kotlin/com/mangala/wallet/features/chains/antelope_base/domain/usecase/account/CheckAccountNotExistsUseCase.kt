package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.model.blockchain.BlockchainType

class CheckAccountNotExistsUseCase(
    private val getAccountsByQueryUseCase: GetAccountsByQueryUseCase
) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        accountName: String
    ): Boolean {
        // Checking if account does not exist
        
        val response = getAccountsByQueryUseCase(
            blockchainType,
            accountName
        )
        
        // API response received

        if (response.isFailure) {
            // API call failed
            return false
        }

        val accounts = response.getOrNull() ?: emptyList()
        // Accounts retrieved
        
        val accountNotExists = accounts.firstOrNull { it == accountName } == null
        // Check completed
        
        return accountNotExists
    }
}