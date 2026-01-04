package com.mangala.wallet.features.chains.antelope_base.domain.repository.esr

import com.mangala.wallet.features.chains.antelope_base.domain.model.esr.EsrCallbackData
import com.memtrip.eos.chain.actions.transaction.esr.EsrSigningRequestArgs

interface EsrRepository {
    suspend fun postCallback(url: String, request: EsrCallbackData)
}