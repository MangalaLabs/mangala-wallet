package com.mangala.wallet.features.chains.antelope_base.domain.model.multisig

data class MultisigExecutableStatus(
    val isExecutable: Boolean,
    val accountWeightMap: Map<String?, Long>
)
