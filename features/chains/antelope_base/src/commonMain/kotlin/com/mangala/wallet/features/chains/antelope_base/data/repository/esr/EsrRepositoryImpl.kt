package com.mangala.wallet.features.chains.antelope_base.data.repository.esr

import com.mangala.wallet.features.chains.antelope_base.data.remote.esr.EsrRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.data.remote.esr.model.EsrCallbackRequest
import com.mangala.wallet.features.chains.antelope_base.domain.model.esr.EsrCallbackData
import com.mangala.wallet.features.chains.antelope_base.domain.repository.esr.EsrRepository

class EsrRepositoryImpl(private val esrRemoteDataSource: EsrRemoteDataSource) : EsrRepository {

    override suspend fun postCallback(url: String, request: EsrCallbackData) {
        esrRemoteDataSource.postCallback(
            url,
            EsrCallbackRequest(
                firstSignature = request.signatures.first(),
                transactionId = request.transactionId,
                blockNumber = request.blockNumber,
                signerAccount = request.signerAccount,
                signerPermission = request.signerPermission,
                referenceBlockNum = request.referenceBlockNum,
                referenceBlockId = request.referenceBlockId,
                request = request.request,
                expirationTime = request.expirationTime,
                resolvedChainId = request.resolvedChainId,
                linkChannel = request.anchorLinkChannel,
                linkKey = request.anchorLinkReceivePublicKey,
                linkName = request.anchorLinkName,
                sig0 = request.signatures.getOrNull(1),
                sig1 = request.signatures.getOrNull(2),
                sig2 = request.signatures.getOrNull(3),
                sig3 = request.signatures.getOrNull(4),
            )
        )
    }
}