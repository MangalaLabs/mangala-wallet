package com.linh.antelope_qr.di

import com.linh.antelope_qr.domain.qrcheckers.CreateAccountForFriendQrCodeChecker
import com.linh.antelope_qr.domain.usecase.AntelopeEsrQrCodeChecker
import com.linh.antelope_qr.domain.usecase.DecodeEsrUseCase
import com.linh.antelope_qr.domain.usecase.DecodeRequestFromQrCodeUseCase
import com.linh.antelope_qr.domain.usecase.EncodeRequestToQrCodeUseCase
import com.linh.antelope_qr.domain.usecase.EncodeSyncAccountRequestUseCase
import com.linh.antelope_qr.domain.usecase.EncodeSyncPublicKeyPairsRequestUseCase
import com.mangala.wallet.qrcode.domain.qrcheckers.QrCodeTypeChecker
import com.mangala.wallet.utils.di.JSON
import org.koin.core.qualifier.named
import org.koin.dsl.module

val antelopeQrModule = module {
    factory { EncodeSyncPublicKeyPairsRequestUseCase(get()) }
    factory { EncodeSyncAccountRequestUseCase(get(named(JSON))) }
    factory { EncodeRequestToQrCodeUseCase(get(named(JSON))) }
    factory { DecodeRequestFromQrCodeUseCase(get(named(JSON))) }
    factory { DecodeEsrUseCase(get()) }

    single<QrCodeTypeChecker>(named("AntelopeEsrQrCodeChecker")) { AntelopeEsrQrCodeChecker(get()) }
    single<QrCodeTypeChecker>(named("CreateAccountForFriendQrCodeChecker")) { CreateAccountForFriendQrCodeChecker() }
}