package com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.abi

import com.mangala.wallet.features.chains.evmcompatible.contract.ContractMethod

class GetTokenSupportedListAbi() : ContractMethod() {

    override val methodSignature = Companion.methodSignature

    companion object {
        const val methodSignature = "getTokenSupportList()"
    }

}
