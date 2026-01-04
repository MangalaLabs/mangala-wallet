package com.mangala.wallet.core.ai.di

import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionPlugin
import com.mangala.wallet.core.ai.data.remote.AIServiceFactory
import com.mangala.wallet.core.ai.data.remote.AIServiceType
import com.mangala.wallet.core.ai.data.remote.providers.gemini.GeminiApi
import com.mangala.wallet.core.ai.data.remote.providers.gemini.createGeminiApi
import com.mangala.wallet.core.ai.data.remote.providers.mangala.MangalaApi
import com.mangala.wallet.core.ai.data.remote.providers.mangala.createMangalaApi
import com.mangala.wallet.core.ai.data.remote.providers.ollama.OllamaApi
import com.mangala.wallet.core.ai.data.remote.providers.ollama.createOllamaApi
import com.mangala.wallet.core.ai.data.remote.providers.openai.OpenAiApi
import com.mangala.wallet.core.ai.data.remote.providers.openai.createOpenAiApi
import com.mangala.wallet.core.ai.data.repository.AiRepositoryImpl
import com.mangala.wallet.core.ai.domain.model.function.config.ConfigurationManager
import com.mangala.wallet.core.ai.domain.repository.AiRepository
import com.mangala.wallet.core.ai.domain.model.function.handler.DefaultFunctionHandlerRegistry
import com.mangala.wallet.core.ai.domain.model.function.config.EnhancedFunctionRegistry
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandlerPlugin
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandlerRegistry
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionRegistry
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRendererPlugin
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRendererRegistry
import com.mangala.wallet.core.ai.domain.model.function.renderer.DefaultConfirmationRendererRegistry
import com.mangala.wallet.core.ai.domain.model.factory.MessageFactoryRegistry
import com.mangala.wallet.core.ai.domain.model.factory.DefaultMessageFactoryRegistry
import com.mangala.wallet.core.ai.domain.model.renderer.MessageRenderer
import com.mangala.wallet.core.ai.domain.model.renderer.MessageRendererRegistry
import com.mangala.wallet.core.ai.domain.model.renderer.DefaultMessageRendererRegistry
import com.mangala.wallet.core.ai.domain.model.action.ActionHandler
import com.mangala.wallet.core.ai.domain.model.action.ActionHandlerRegistry
import com.mangala.wallet.core.ai.domain.model.action.DefaultActionHandlerRegistry
import com.mangala.wallet.core.ai.domain.model.dialog.DialogProvider
import com.mangala.wallet.core.ai.domain.model.dialog.DialogProviderRegistry
import com.mangala.wallet.core.ai.domain.model.dialog.DefaultDialogProviderRegistry
import com.mangala.wallet.core.ai.domain.model.navigation.NavigationHandlerRegistry
import com.mangala.wallet.core.ai.domain.model.navigation.NavigationHandler
import de.jensklingenberg.ktorfit.Ktorfit
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coreAiModule = module {
    includes(coreAiPlatformSpecificModule())
    includes(configModule)

    single<HttpClient> {
        HttpClient(get<HttpClientEngine>()) {
            install(HttpTimeout) {
                requestTimeoutMillis = 60000
                connectTimeoutMillis = 60000
                socketTimeoutMillis = 60000
            }
            install(HttpRedirect) {
                checkHttpMethod = false
                allowHttpsDowngrade = true
            }
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = false
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(Logging) {
                level = LogLevel.BODY
                logger = object : Logger {
                    override fun log(message: String) {
                        Napier.i(tag = "Http Client", message = message)
                    }
                }
            }
        }
    }
    single<Ktorfit>(qualifier = named("gemini")) {
        Ktorfit.Builder()
//            .baseUrl("https://gateway.helicone.ai/v1beta/")
            .baseUrl("https://generativelanguage.googleapis.com/v1beta/")
            .httpClient(get<HttpClient>())
            .build()
    }
    single<Ktorfit>(qualifier = named("openai")) {
        Ktorfit.Builder()
//            .baseUrl("https://gateway.helicone.ai/v1beta/")
            .baseUrl("https://api.openai.com/v1/")
            .httpClient(get<HttpClient>())
            .build()
    }
    single<Ktorfit>(qualifier = named("ollama")) {
        Ktorfit.Builder()
            .baseUrl("http://192.168.100.168:11434/api/")
            .httpClient(get<HttpClient>())
            .build()
    }
    single<Ktorfit>(qualifier = named("mangala")) {
        Ktorfit.Builder()
            .baseUrl("https://ethanman.online/")
            .httpClient(get<HttpClient>())
            .build()
    }
    single<GeminiApi> { get<Ktorfit>(named("gemini")).createGeminiApi() }
    single<OllamaApi> { get<Ktorfit>(named("ollama")).createOllamaApi() }
    single<MangalaApi> { get<Ktorfit>(named("mangala")).createMangalaApi() }
    single<OpenAiApi> { get<Ktorfit>(named("openai")).createOpenAiApi() }

    factory<AiRepository> { AiRepositoryImpl(get(), AIServiceType.GEMINI) }

    // Handler registry needs to be created before function registry for validation
    single<FunctionHandlerRegistry> {
        DefaultFunctionHandlerRegistry(getAll<FunctionHandlerPlugin>())
    }
    
    // Use the enhanced function registry for improved configuration and validation
    // Allow functions without handlers for informational purposes
    single<FunctionRegistry> {
        EnhancedFunctionRegistry(
            plugins = getAll<FunctionPlugin>(),
            configSource = get(),
            handlerRegistry = get(),
            configValidationEnabled = true,
            allowFunctionsWithoutHandlers = true,
            logDebugInfo = true
        )
    }
    
    single<ConfirmationRendererRegistry> {
        DefaultConfirmationRendererRegistry(getAll<ConfirmationRendererPlugin>())
    }
    
    single<MessageFactoryRegistry> {
        DefaultMessageFactoryRegistry(getAll())
    }
    
    single<MessageRendererRegistry> {
        DefaultMessageRendererRegistry(getAll<MessageRenderer>())
    }
    
    single<ActionHandlerRegistry> {
        DefaultActionHandlerRegistry(getAll<ActionHandler>())
    }
    
    single<DialogProviderRegistry> {
        DefaultDialogProviderRegistry(getAll<DialogProvider>())
    }
    
    single<NavigationHandlerRegistry> {
        NavigationHandlerRegistry(getAll<NavigationHandler>())
    }

    // Configure automatic refresh for the configuration manager (every 30 minutes)
    single { 
        get<ConfigurationManager>().apply {
            setupAutomaticRefresh(intervalMs = 30 * 60 * 1000, immediate = false)
        }
    }

    // Provide the AI service factory
    single { AIServiceFactory(get(), get(), get(), get(), get()) }
}