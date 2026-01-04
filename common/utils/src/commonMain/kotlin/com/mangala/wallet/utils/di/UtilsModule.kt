package com.mangala.wallet.utils.di

import com.mangala.wallet.utils.ClipboardFactory
import com.mangala.wallet.utils.ShareFactory
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

expect fun utilsModule(): Module