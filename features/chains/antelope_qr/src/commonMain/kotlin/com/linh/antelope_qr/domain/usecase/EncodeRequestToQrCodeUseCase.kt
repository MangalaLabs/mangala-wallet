package com.linh.antelope_qr.domain.usecase

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class EncodeRequestToQrCodeUseCase(
    val json: Json
) {
    inline operator fun <reified T> invoke(request: T): String {
        return json.encodeToString(request)
    }
}