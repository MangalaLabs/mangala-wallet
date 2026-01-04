package com.linh.antelope_qr.domain.qrcheckers

import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.qrcode.domain.model.QrCodeData.AntelopeCreateAccountForFriend
import com.mangala.wallet.qrcode.domain.qrcheckers.QrCodeTypeChecker
import com.linh.antelope_qr.domain.model.CreateAccountForFriendRequest
import kotlinx.serialization.json.Json.Default.decodeFromString

class CreateAccountForFriendQrCodeChecker: QrCodeTypeChecker {
    override fun canHandle(rawData: String): Boolean {
        return try {
            val parseResult = parse(rawData).getOrNull()

            parseResult != null
        } catch (e: Exception) {
            false
        }
    }

    override fun parse(rawData: String): Result<QrCodeData> {
        val createAccountForFriendRequest: CreateAccountForFriendRequest = decodeFromString(rawData)
        return Result.success(AntelopeCreateAccountForFriend(rawData))
    }
}