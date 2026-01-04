package com.mangala.wallet.features.addressbook.di

import org.koin.core.module.Module

internal const val ADDRESS_BOOK_DATABASE_NAME = "addressbookdatabase.db"

internal expect fun addressBookPlatformSpecificModule(): Module