package com.mangala.wallet.qrcode.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.qrcode.domain.qrcheckers.AddressQrCodeChecker
import com.mangala.wallet.qrcode.domain.qrcheckers.AnchorKeycertQrCodeChecker
import com.mangala.wallet.qrcode.domain.qrcheckers.AntelopeImportAccountQrCodeChecker
import com.mangala.wallet.qrcode.domain.qrcheckers.LoginQrCodeChecker
import com.mangala.wallet.qrcode.domain.usecase.ParseQRCodeResultUseCase
import com.mangala.wallet.qrcode.domain.qrcheckers.PaymentQrCodeChecker
import com.mangala.wallet.qrcode.domain.qrcheckers.QrCodeTypeChecker
import com.mangala.wallet.qrcode.domain.qrcheckers.QrCodeTypeRegistry
import com.mangala.wallet.qrcode.domain.qrcheckers.SyncAccountRequestQrCodeChecker
import com.mangala.wallet.qrcode.domain.qrcheckers.WalletConnectQrCodeChecker
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val qrCodeModule = module {
    factory {
        ParseQRCodeResultUseCase(
            qrCodeTypeRegistry = get()
        )
    }
    single(createdAtStart = true) { QrCodeTypeRegistry(getAll<QrCodeTypeChecker>()) }

    single<QrCodeTypeChecker>(named("AddressQrCodeChecker")) { AddressQrCodeChecker() }
    single<QrCodeTypeChecker>(named("AnchorKeycertQrCodeChecker")) { AnchorKeycertQrCodeChecker() }
    single<QrCodeTypeChecker>(named("AntelopeImportAccountQrCodeChecker")) { AntelopeImportAccountQrCodeChecker() }
    single<QrCodeTypeChecker>(named("LoginQrCodeChecker")) { LoginQrCodeChecker() }
    single<QrCodeTypeChecker>(named("PaymentQrCodeChecker")) { PaymentQrCodeChecker() }
    single<QrCodeTypeChecker>(named("WalletConnectQrCodeChecker")) { WalletConnectQrCodeChecker() }
    single<QrCodeTypeChecker>(named("SyncAccountRequestQrCodeChecker")) { SyncAccountRequestQrCodeChecker() }
}

val qrCodeScreenModule = screenModule {

}