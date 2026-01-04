package com.mangala.wallet.features.conversationui.di

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.mangala.wallet.features.conversationui.data.local.AndroidDatabaseDriverFactory
import com.mangala.wallet.features.conversationui.data.local.ConversationUiDatabaseWrapper
import com.mangala.wallet.features.conversationui.database.ConversationUiDatabase
import com.mangala.wallet.features.conversationui.domain.usecase.FileExporter
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

internal actual fun conversationUiPlatformSpecificModule() = module {
    single {
        val driver = AndroidSqliteDriver(
            schema = ConversationUiDatabase.Schema,
            context = get(),
            name = CONVERSATION_UI_DATABASE_NAME,
            callback = object : AndroidSqliteDriver.Callback(ConversationUiDatabase.Schema) {
                override fun onConfigure(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
        ConversationUiDatabase(driver = driver)
    }
    
    single { 
        ConversationUiDatabaseWrapper(
            AndroidDatabaseDriverFactory(androidContext()).createDriver()
        )
    }
    
    single { FileExporter(androidContext()) }
    
    // Clean HTTP client specifically for WebSocket connections - no interceptors
    single<HttpClient>(named("stomp_websocket")) {
        HttpClient(OkHttp) {
            install(WebSockets) {
                pingInterval = 20.seconds
                // Removed maxFrameSize - not supported by OkHttp engine
            }
            
            // Clean configuration - let Ktor handle WebSocket upgrade properly
            engine {
                config {
                    retryOnConnectionFailure(true)
                    // WebSocket specific configuration
                    pingInterval(20, java.util.concurrent.TimeUnit.SECONDS)
                }
            }
        }
    }
    
    // Separate HTTP client for regular API calls with necessary headers
    single<HttpClient>(named("api_client")) {
        HttpClient(OkHttp) {
            engine {
                config {
                    retryOnConnectionFailure(true)
                    addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .removeHeader("Origin")
                            .addHeader("Origin", "https://gateway.taman2h.fun")
                            .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android) AppleWebKit/537.36")
                            .addHeader("Accept", "*/*")
                            .addHeader("Accept-Language", "en-US,en;q=0.9")
                            .build()
                        chain.proceed(request)
                    }
                }
            }
        }
    }
}