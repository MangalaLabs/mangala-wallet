package com.mangala.wallet.features.chains.antelope_base.domain.model.proposal

import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi

data class ActionAbi(
    val account: String,
    val name: String,
    val authorization: List<TransactionAuthorizationAbi>,
    val data: ActionData?,
    val dataDecoded: List<AntelopeActionAbi> = emptyList()
)