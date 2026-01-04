package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.soywiz.krypto.sha256
import io.ktor.utils.io.core.toByteArray
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class GetAccountNameHashUseCase {

    @OptIn(ExperimentalUuidApi::class)
    operator fun invoke(accountName: String): String {
        val accountNameHash = accountName.toByteArray().sha256().toString()

        val first16BytesOfHash = accountNameHash.encodeToByteArray().sliceArray(0 until 16)
        val uuid = Uuid.fromByteArray(first16BytesOfHash).toString()

        println("GetAccountNameHashUseCase $accountName -> $uuid")

        return uuid
    }
}