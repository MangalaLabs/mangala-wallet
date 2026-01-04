package com.mangala.wallet.features.conversationui.di

import org.koin.core.module.Module

internal const val CONVERSATION_UI_DATABASE_NAME = "conversationuidatabase.db"

internal expect fun conversationUiPlatformSpecificModule(): Module