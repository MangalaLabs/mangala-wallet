package com.mangala.wallet.passkey.di

import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

actual fun createHttpClient(): HttpClient {
    return HttpClient(Java) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = 60000  // Increased to 60s for slow server responses
            connectTimeoutMillis = 60000  // 30s for connection
            socketTimeoutMillis = 60000   // Increased to 60s for socket operations
        }
    }
}