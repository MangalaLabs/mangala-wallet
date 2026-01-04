package com.mangala.wallet.features.chains.antelope.domain.usecase

import com.linh.antelope_qr.domain.model.SignedTransactionResponse
import com.mangala.antelope.base.api.model.PushTransactionRequest
import com.mangala.antelope.base.domain.usecase.PushTransactionUseCase
import com.memtrip.eos.abi.writer.compression.CompressionType
import com.memtrip.eos.chain.actions.transaction.gen.AbiBinaryGenTransactionWriter

class PushSignedTransactionUseCase(
    private val pushTransactionUseCase: PushTransactionUseCase
) {
    suspend operator fun invoke(signedTransactionResponse: SignedTransactionResponse): Result<String> {
        val result = pushTransactionUseCase.invoke(
            PushTransactionRequest(
                listOf(signedTransactionResponse.signature),
                "none",
                "",
                AbiBinaryGenTransactionWriter(CompressionType.NONE).squishTransactionAbi(
                    signedTransactionResponse.signTransactionRequest.toTransactionAbi()
                ).toHex()
            )
        ) ?: return Result.failure(Exception("Failed to push transaction"))
        return Result.success("")
    }
}