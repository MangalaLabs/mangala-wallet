package com.mangala.wallet.features.addressbook.di

import com.mangala.wallet.features.addressbook.presentation.contact.qr.ShowContactQrScreenModel
import com.mangala.wallet.features.addressbook.presentation.contact.qr.QrDataType
import com.mangala.wallet.features.addressbook.presentation.contact.scan.ScanToAddContactScreenModel
import com.mangala.wallet.features.addressbook.domain.cache.QrCodeCache
import com.mangala.wallet.features.addressbook.domain.validation.QrCodeValidator
import com.mangala.wallet.features.addressbook.domain.sharing.ImageSharingHelper
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val contactQrModule = module {
    // Screen models
    factory { params -> 
        ShowContactQrScreenModel(
            dataType = params.get<QrDataType>(),
            qrGenerator = get()
        ) 
    }
    
    factory<ScanToAddContactScreenModel> { ScanToAddContactScreenModel() }
}

fun Module.includeContactQrModule() {
    includes(contactQrModule)
}
