package com.mangala.wallet.passkey.di

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

actual fun createHttpClient(): HttpClient {
    return HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = true
            })
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = 60000  // Increased to 60s for slow server responses
            connectTimeoutMillis = 60000  // Increased to 30s for connection
            socketTimeoutMillis = 60000   // Increased to 60s for socket operations
        }
        
        engine {
            // For development only - trust all certificates
            // This should be removed in production
            preconfigured = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)   // Increased to 30s
                .readTimeout(60, TimeUnit.SECONDS)      // Increased to 60s
                .writeTimeout(60, TimeUnit.SECONDS)     // Increased to 60s
                .addInterceptor { chain ->
                    val request = chain.request()
                    println("=== OkHttp Request ===")
                    println("Method: ${request.method}")
                    println("URL: ${request.url}")
                    println("Headers: ${request.headers}")
                    
                    // Log request body if present
                    request.body?.let { body ->
                        val buffer = okio.Buffer()
                        body.writeTo(buffer)
                        println("Body: ${buffer.readUtf8()}")
                    }
                    
                    val startTime = System.currentTimeMillis()
                    val response = chain.proceed(request)
                    val endTime = System.currentTimeMillis()
                    
                    println("=== OkHttp Response ===")
                    println("Status: ${response.code} ${response.message}")
                    println("Time: ${endTime - startTime}ms")
                    println("Headers: ${response.headers}")
                    
                    response
                }
                .apply {
                    // Trust all certificates for development
                    val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                    })
                    
                    val sslContext = SSLContext.getInstance("TLS")
                    sslContext.init(null, trustAllCerts, java.security.SecureRandom())
                    
                    sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
                    hostnameVerifier { _, _ -> true }
                }
                .build()
        }
    }
}