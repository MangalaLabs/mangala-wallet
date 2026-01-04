package com.mangala.wallet.features.home.presentation

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.UpdateAccountStatusUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.launch

class CreateAccountNotificationScreenModel(
    private val updateAccountStatusUseCase: UpdateAccountStatusUseCase
): BaseScreenModel() {

    fun updateEosAccountStatus(
        accountName: String,
        chainId: String,
        createAccountState: AntelopeAccount.CreateAccountState
    ) {
        screenModelScope.launch {
            // TODO: Check when this finishes first before navigating
            val blockchainType = BlockchainType.fromChainId(chainId)
            println("blockchainType: $blockchainType")
            updateAccountStatusUseCase(
                accountName = accountName,
                isTemp = false,
                blockchainType = blockchainType,
                createAccountState = createAccountState
            )
        }
    }
}