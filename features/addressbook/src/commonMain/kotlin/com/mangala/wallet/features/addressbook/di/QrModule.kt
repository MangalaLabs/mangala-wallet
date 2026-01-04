package com.mangala.wallet.features.addressbook.di

import com.mangala.wallet.features.addressbook.domain.qr.*
import com.mangala.wallet.features.addressbook.domain.qr.loaders.*
import com.mangala.wallet.features.addressbook.presentation.qr.QrScreenModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Dependency injection module for QR functionality
 */
val qrModule = module {
    
    // QR Cache
    singleOf(::QrCacheImpl) bind QrCache::class
    
    // QR Content Generator
    singleOf(::QrContentGeneratorImpl) bind QrContentGenerator::class
    
    // QR Data Loaders
    factoryOf(::ContactQrDataLoader)
    factoryOf(::GroupQrDataLoader)
    factoryOf(::TagQrDataLoader)
    factoryOf(::AddressQrDataLoader)
    
    // QR Service
    singleOf(::QrService)
    
    // QR Screen Model Factory
    factory { (dataType: QrDataType) ->
        QrScreenModel(dataType)
    }
}