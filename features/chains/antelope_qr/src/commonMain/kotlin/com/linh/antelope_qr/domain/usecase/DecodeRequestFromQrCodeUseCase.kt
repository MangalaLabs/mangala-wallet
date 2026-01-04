package com.linh.antelope_qr.domain.usecase

import kotlinx.serialization.json.Json

class DecodeRequestFromQrCodeUseCase(
    val json: Json
) {
    inline operator fun <reified T> invoke(request: String): T {
        return json.decodeFromString(request)
    }
}