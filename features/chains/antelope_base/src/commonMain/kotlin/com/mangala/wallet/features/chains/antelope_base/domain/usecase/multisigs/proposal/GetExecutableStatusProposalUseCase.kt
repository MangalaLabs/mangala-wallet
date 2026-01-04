package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal

import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.AccountWeight
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigExecutableStatus


//TODO
@Suppress("NAME_SHADOWING", "DEPRECATION")
class GetExecutableStatusProposalUseCase {
    operator fun invoke(
        accountProvided: List<MultisigActionAuthorization>,
        accountWeight: AccountWeight
    ): MultisigExecutableStatus {
        var thresholdReached = 0L
        val accountsMap = accountWeight.accountWeightMap
        val threshold = accountWeight.threshold

        accountProvided.forEach { accountProvided ->
            val key = accountProvided.formatted
            val weight = accountsMap[key] ?: 0L
            thresholdReached += weight
        }

        return MultisigExecutableStatus(thresholdReached >= threshold, accountsMap)
    }

}