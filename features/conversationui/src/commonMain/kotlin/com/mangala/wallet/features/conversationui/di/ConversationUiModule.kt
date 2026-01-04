package com.mangala.wallet.features.conversationui.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.domain.reset.usecases.ClearConversationHistoryUseCase
import com.mangala.wallet.features.conversationui.data.local.ChatHistoryLocalDataSource
import com.mangala.wallet.features.conversationui.data.local.ConversationUiDatabaseWrapper
import com.mangala.wallet.features.conversationui.data.repository.ChatHistoryRepositoryImpl
import com.mangala.wallet.features.conversationui.data.service.WalletContextProviderImpl
import com.mangala.wallet.features.conversationui.domain.repository.ChatHistoryRepository
import com.mangala.wallet.features.conversationui.domain.usecase.CancelFunctionCallUseCase
import com.mangala.wallet.features.conversationui.domain.usecase.ClearChatContextUseCase
import com.mangala.wallet.features.conversationui.domain.usecase.ClearConversationHistoryUseCaseImpl
import com.mangala.wallet.features.conversationui.domain.usecase.ConfirmFunctionCallUseCase
import com.mangala.wallet.features.conversationui.domain.usecase.ExportChatLogUseCase
import com.mangala.wallet.features.conversationui.domain.usecase.GetSessionMessagesUseCase
import com.mangala.wallet.features.conversationui.domain.usecase.SendSocketMessageUseCase
import com.mangala.wallet.features.conversationui.domain.validation.AddressValidatorRegistry
import com.mangala.wallet.features.conversationui.domain.validation.validators.BlockchainAddressValidator
import com.mangala.wallet.features.conversationui.domain.service.StompWebSocketService
import com.mangala.wallet.features.conversationui.domain.service.WalletContextProvider
import com.mangala.wallet.features.conversationui.domain.service.WebSocketConfig
import com.mangala.wallet.features.conversationui.presentation.ConversationUiScreen
import com.mangala.wallet.features.conversationui.presentation.ConversationUiScreenModel
import com.mangala.wallet.features.conversationui.presentation.sessionlist.ConversationSessionListScreen
import com.mangala.wallet.features.conversationui.presentation.sessionlist.ConversationSessionListScreenModel
import com.mangala.wallet.features.conversationui.presentation.test.ConversationUiEntryPointTestScreen
import com.mangala.wallet.features.conversationui.presentation.test.ConversationUiEntryPointTestScreenModel
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.websocket.chat.di.webSocketChatModule
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

val conversationUiModule = module {
    includes(conversationUiPlatformSpecificModule())
    includes(webSocketChatModule)

    // HTTP client with WebSocket support for STOMP
    single<HttpClient>(named("stomp_websocket")) {
        HttpClient {
            install(WebSockets) {
                pingInterval = 20.seconds
                // Remove maxFrameSize to avoid OkHttp compatibility issue
                // maxFrameSize = Long.MAX_VALUE // This causes the error
            }
        }
    }

    // STOMP WebSocket service
    single<StompWebSocketService> {
        StompWebSocketService(
            httpClient = get(named("stomp_websocket")),
            sessionManager = get(),
            json = get(named("websocket")),
            environment = WebSocketConfig.Environment.PRODUCTION
        )
    }

    single<ChatHistoryLocalDataSource> { 
        get<ConversationUiDatabaseWrapper>().getChatHistoryLocalDataSource()
    }

    factory<ChatHistoryRepository> { ChatHistoryRepositoryImpl(get()) }

    // SendMessageUseCase is deprecated - use SendSocketMessageUseCase instead
    // factory { SendMessageUseCase(get(), get(), get(), get(), get()) }
    factory { GetSessionMessagesUseCase(get()) }
    factory { ConfirmFunctionCallUseCase(get(), get(), get(), get()) }
    factory { CancelFunctionCallUseCase(get(), get()) }
    factory { ClearChatContextUseCase(get()) }
    factory { ExportChatLogUseCase(get(), get()) }
    factory { SendSocketMessageUseCase(get(), get()) }

    // Reset use case binding
    factoryOf(::ClearConversationHistoryUseCaseImpl) bind ClearConversationHistoryUseCase::class

    // Address validation components
    single { AddressValidatorRegistry(getAll()) }
    single { BlockchainAddressValidator(get()) }

    factory { (sessionId: String?) -> ConversationUiScreenModel(
        sessionId,
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(),
        get(named("websocket")),
        get(),
        get(),
        get(),
        get(),
        get()
    ) }
    factory { ConversationSessionListScreenModel(get(), get()) }
    factory { ConversationUiEntryPointTestScreenModel(get(), get(), get()) }
    factory<WalletContextProvider> { WalletContextProviderImpl(get(), get()) }
}

val conversationUiScreenModule = screenModule {
    register<SharedScreen.ConversationUiScreen> { provider ->
        ConversationUiScreen(provider.sessionId)
    }
    register<SharedScreen.ConversationSessionListScreen> {
        ConversationSessionListScreen()
    }
    register<SharedScreen.ConversationUiEntryPointScreen> {
        ConversationUiEntryPointTestScreen()
    }
}