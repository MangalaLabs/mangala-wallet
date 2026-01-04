package com.mangala.wallet.remote.di

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.BuildKonfig
import com.mangala.wallet.remote.category_dapps.CategoryDAppApi
import com.mangala.wallet.remote.category_dapps.CategoryDAppRemoteDataSource
import com.mangala.wallet.remote.category_dapps.createCategoryDAppApi
import com.mangala.wallet.remote.network.ApplicationContentNegotiation
import com.mangala.wallet.remote.network.CustomError
import com.mangala.wallet.remote.network.CustomResponseException
import com.mangala.wallet.remote.network.MissingPageException
import com.mangala.wallet.remote.provider.BaseBlockExplorerRemoteDataSource
import com.mangala.wallet.remote.provider.alchemy.AlchemyApi
import com.mangala.wallet.remote.provider.alchemy.AlchemyRemoteDataSource
import com.mangala.wallet.remote.provider.alchemy.createAlchemyApi
import com.mangala.wallet.remote.provider.coingecko.CoingeckoApi
import com.mangala.wallet.remote.provider.coingecko.CoingeckoRemoteDataSource
import com.mangala.wallet.remote.provider.covalenthq.CovalenthqApi
import com.mangala.wallet.remote.provider.covalenthq.CovalenthqRemoteDataSource
import com.mangala.wallet.remote.provider.covalenthq.createCovalenthqApi
import com.mangala.wallet.remote.provider.eosEVM.EosEvmApi
import com.mangala.wallet.remote.provider.eosEVM.EosEvmRemoteDataSource
import com.mangala.wallet.remote.provider.eosEVM.createEosEvmApi
import com.mangala.wallet.remote.provider.ipfs.IpfsApi
import com.mangala.wallet.remote.provider.ipfs.IpfsRemoteDataSource
import com.mangala.wallet.remote.provider.ipfs.createIpfsApi
import com.mangala.wallet.remote.provider.moralis.MoralisApi
import com.mangala.wallet.remote.provider.moralis.MoralisRemoteDataSource
import com.mangala.wallet.remote.provider.moralis.createMoralisApi
import com.mangala.wallet.remote.provider.quicknode.QuickNodeApi
import com.mangala.wallet.remote.provider.quicknode.createQuickNodeApi
import com.mangala.wallet.remote.portfolio.MangalaPortfolioApi
import com.mangala.wallet.remote.portfolio.MangalaPortfolioRemoteDataSource
import com.mangala.wallet.remote.portfolio.createMangalaPortfolioApi
import com.mangala.wallet.core.auth.SessionManager
import de.jensklingenberg.ktorfit.Ktorfit
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.append
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.encodeBase64
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.time.Duration.Companion.minutes

fun remoteModule(baseUrl: String, enableNetworkLogs: Boolean) = module {
    single<CoingeckoApi> {
        provideKtorfitCoinGecko(
            baseUrl = baseUrl,
            enableNetworkLogs = true,
            forceJsonBody = false,
            httpClientEngine = get(),
            header = COINGECKO_API_KEY_HEADER,
            apiKey = BuildKonfig.COINGECKO_API_KEY
        ).create()
    }
    single { CoingeckoRemoteDataSource(get()) }

    single<CovalenthqApi> {
        provideKtorfit(
            baseUrl = "https://api.covalenthq.com/v1/",
            enableNetworkLogs = true,
            forceJsonBody = false,
            username = BuildKonfig.COVALENTHQ_API_KEY,
            password = "",
            httpClientEngine = get()
        ).createCovalenthqApi()
    }
    single<QuickNodeApi> {
        provideKtorfit(
            baseUrl = "https://api.covalenthq.com/v1/",
            enableNetworkLogs = true,
            forceJsonBody = false,
            username = BuildKonfig.COVALENTHQ_API_KEY,
            password = "",
            httpClientEngine = get()
        ).createQuickNodeApi()
    }
    single<AlchemyApi> {
        provideKtorfit(
            baseUrl = "https://eth-mainnet.g.alchemy.com/v2/",
            enableNetworkLogs = true,
            forceJsonBody = false,
            username = "",
            password = "",
            httpClientEngine = get()
        ).createAlchemyApi()
    }

    single<EosEvmApi>(named(EosEvmRemoteDataSource.MAINNET)) {
        provideKtorfit(
            baseUrl = "https://explorer.evm.eosnetwork.com/",
            enableNetworkLogs = true,
            forceJsonBody = false,
            username = "",
            password = "",
            httpClientEngine = get()
        ).createEosEvmApi()
    }

    single<EosEvmApi>(named(EosEvmRemoteDataSource.TESTNET)) {
        provideKtorfit(
            baseUrl = "https://explorer.testnet.evm.eosnetwork.com/",
            enableNetworkLogs = true,
            forceJsonBody = false,
            username = "",
            password = "",
            httpClientEngine = get()
        ).createEosEvmApi()
    }

    factory<(BlockchainType) -> BaseBlockExplorerRemoteDataSource> {
        { blockchainType: BlockchainType ->
            when (blockchainType) {
                BlockchainType.EosEvm -> get(
                    named(
                        EosEvmRemoteDataSource.MAINNET
                    )
                )

//                TODO: Add logic to get RemoteDataSource for EOS EVM Testnet

                else -> get(
                    named(
                        MoralisRemoteDataSource.TAG
                    )
                )
            }
        }
    }
    single<BaseBlockExplorerRemoteDataSource>(named(CovalenthqRemoteDataSource.TAG)) {
        CovalenthqRemoteDataSource(get())
    }
    single<BaseBlockExplorerRemoteDataSource>(named(EosEvmRemoteDataSource.MAINNET)) {
        EosEvmRemoteDataSource(
            get(named(EosEvmRemoteDataSource.MAINNET))
        )
    }
    single<BaseBlockExplorerRemoteDataSource>(named(EosEvmRemoteDataSource.TESTNET)) {
        EosEvmRemoteDataSource(
            get(named(EosEvmRemoteDataSource.TESTNET))
        )
    }
    single<BaseBlockExplorerRemoteDataSource>(named(AlchemyRemoteDataSource.TAG)) {
        AlchemyRemoteDataSource(get())
    }
    single<BaseBlockExplorerRemoteDataSource>(named(MoralisRemoteDataSource.TAG)) {
        MoralisRemoteDataSource(get())
    }
    single<CategoryDAppApi> {
        provideKtorfit(
            baseUrl = "https://raw.githubusercontent.com/",
            enableNetworkLogs = true,
            forceJsonBody = false,
            username = "",
            password = "",
            httpClientEngine = get()
        ).createCategoryDAppApi()
    }
    single { CategoryDAppRemoteDataSource(get()) }

    single<IpfsApi> {
        provideKtorfit(
            baseUrl = "",
            enableNetworkLogs = true,
            forceJsonBody = false,
            username = "",
            password = "",
            httpClientEngine = get()
        ).createIpfsApi()
    }

    single<MoralisApi> {
        provideKtorfit(
            baseUrl = "https://deep-index.moralis.io/api/v2.2/",
            enableNetworkLogs = true,
            forceJsonBody = false,
            username = "",
            password = "",
            httpClientEngine = get()
        ).createMoralisApi()
    }

    single { IpfsRemoteDataSource(get()) }
    
    single<MangalaPortfolioApi> {
        provideKtorfitWithJwtAuth(
            baseUrl = "https://gateway.taman2h.fun/",
            enableNetworkLogs = enableNetworkLogs,
            forceJsonBody = false,
            httpClientEngine = get(),
            sessionManager = get()
        ).createMangalaPortfolioApi()
    }

    single { MangalaPortfolioRemoteDataSource(get()) }
}

fun provideApiKeyHeaderHttpClient(
    httpClientEngine: HttpClientEngine,
    enableNetworkLogs: Boolean,
    forceJsonBody: Boolean,
    header: String,
    apiKey: String
): HttpClient {
    return provideHttpClient(
        httpClientEngine = httpClientEngine,
        enableNetworkLogs = enableNetworkLogs,
        forceJsonBody = forceJsonBody,
    ) {
        installInterceptorApiKey(header, apiKey)
    }
}

fun provideHttpClient(
    httpClientEngine: HttpClientEngine,
    enableNetworkLogs: Boolean,
    username: String,
    password: String,
    forceJsonBody: Boolean
): HttpClient {
    return provideHttpClient(
        httpClientEngine = httpClientEngine,
        enableNetworkLogs = enableNetworkLogs,
        forceJsonBody = forceJsonBody
    ) {
        installBasicAuthDefaultRequest(username, password)
    }
}

fun provideJwtAuthHttpClient(
    httpClientEngine: HttpClientEngine,
    enableNetworkLogs: Boolean,
    forceJsonBody: Boolean,
    jwtTokenFactory: () -> String
): HttpClient {
    return provideHttpClient(
        httpClientEngine = httpClientEngine,
        enableNetworkLogs = enableNetworkLogs,
        forceJsonBody = forceJsonBody
    ) {
        installJwtAuthDefaultRequest(jwtTokenFactory)
    }
}

fun provideHttpClient(
    httpClientEngine: HttpClientEngine,
    enableNetworkLogs: Boolean,
    forceJsonBody: Boolean,
    config: HttpClientConfig<*>.() -> Unit
): HttpClient {
    return HttpClient(httpClientEngine) {
        installJsonContentNegotiation(forceJsonBody)

        if (enableNetworkLogs) {
            installLogging()
        }

        // Install HttpRequestRetry for retry mechanism
        installHttpRequestRetry()

        installHttpTimeout()

        // Enable default validation and install HttpCallValidator for custom validation
        installHttpResponseValidator()

        config()
    }
}

fun provideSocketHttpClient(
    httpClientEngine: HttpClientEngine
): HttpClient {
    return HttpClient(httpClientEngine) {
        install(WebSockets) {
            pingIntervalMillis = 30_000
        }
    }
}

fun HttpClientConfig<*>.installJsonContentNegotiation(forceJsonBody: Boolean) {
    val contentNegotiation =
        if (forceJsonBody) ApplicationContentNegotiation else ContentNegotiation

    install(contentNegotiation) {
        json(Json { isLenient = true; ignoreUnknownKeys = true })
    }
}

fun HttpClientConfig<*>.installInterceptorApiKey(header: String, apiKey: String) {
    install(DefaultRequest) {
        headers.append(header, apiKey)
    }
}

fun HttpClientConfig<*>.installLogging() {
    install(Logging) {
        level = LogLevel.BODY
        logger = object : Logger {
            override fun log(message: String) {
                Napier.i(tag = "Http Client", message = message)
            }
        }
    }.also {
        Napier.base(DebugAntilog())
    }
}

fun HttpClientConfig<*>.installHttpRequestRetry() {
    install(HttpRequestRetry) {
        retryIf { httpRequest, httpResponse ->
            // Only retry if the request doesn't have a status code
            httpResponse.status.value.let { it !in 200..599 }
        }
        retryOnException(maxRetries = 3, retryOnTimeout = true)
        delayMillis { retry -> retry * 3000L } // retries in 3, 6, 9, etc. seconds
        modifyRequest { request -> request.headers.append("x-retry-count", retryCount.toString()) }
    }
}

fun HttpClientConfig<*>.installHttpTimeout() {
    install(HttpTimeout) {
        requestTimeoutMillis = 1.minutes.inWholeMilliseconds
        socketTimeoutMillis = 1.minutes.inWholeMilliseconds
    }
}

fun HttpClientConfig<*>.installHttpResponseValidator() {
    expectSuccess = true
    HttpResponseValidator {
        validateResponse { response ->
            if (!response.status.isSuccess()) {
                val error: CustomError = response.body()
                if (error.message?.isNotEmpty() == true) {
                    throw CustomResponseException(response, "Message: ${error.message}")
                }
            }
        }

        handleResponseExceptionWithRequest { exception, request ->
            val clientException =
                exception as? ResponseException ?: return@handleResponseExceptionWithRequest
            val exceptionResponse = clientException.response
            val exceptionResponseText = exceptionResponse.bodyAsText()

            if (exceptionResponse.status == HttpStatusCode.NotFound) {
                throw MissingPageException(exceptionResponse, exceptionResponseText)
            } else {
                throw CustomResponseException(exceptionResponse, exceptionResponseText)
            }
        }
    }
}

fun HttpClientConfig<*>.installBasicAuthDefaultRequest(username: String, password: String) {
    install(DefaultRequest) {
        headers.append(HttpHeaders.ContentType, ContentType.Application.Json)
        if (username.isNotEmpty()) {
            val authorization =
                if (password.isNotEmpty()) "$username:$password".toByteArray().encodeBase64()
                else "$username:".toByteArray().encodeBase64()
            headers.append(HttpHeaders.Authorization, "Basic $authorization")
        }
    }
}

fun HttpClientConfig<*>.installJwtAuthDefaultRequest(tokenFactory: () -> String) {
    install(DefaultRequest) {
        headers.append(HttpHeaders.ContentType, ContentType.Application.Json)
        val token = tokenFactory()
        if (token.isNotEmpty()) {
            headers.append(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}

inline fun provideKtorfit(
    baseUrl: String,
    enableNetworkLogs: Boolean,
    username: String,
    password: String,
    forceJsonBody: Boolean,
    httpClientEngine: HttpClientEngine,
): Ktorfit {
    val builder = Ktorfit.Builder()
    if (baseUrl.isNotBlank()) {
        builder.baseUrl(baseUrl)
    }
    return builder
        .httpClient(
            provideHttpClient(
                httpClientEngine = httpClientEngine,
                enableNetworkLogs = enableNetworkLogs,
                username = username,
                password = password,
                forceJsonBody = forceJsonBody
            )
        )
        .build()
}

inline fun provideKtorfitCoinGecko(
    baseUrl: String,
    enableNetworkLogs: Boolean,
    forceJsonBody: Boolean,
    httpClientEngine: HttpClientEngine,
    apiKey: String = "",
    header: String = ""
): Ktorfit {
    val builder = Ktorfit.Builder()
    if (baseUrl.isNotBlank()) {
        builder.baseUrl(baseUrl)
    }
    return builder
        .httpClient(
            provideApiKeyHeaderHttpClient(
                httpClientEngine = httpClientEngine,
                enableNetworkLogs = enableNetworkLogs,
                forceJsonBody = forceJsonBody,
                header = header,
                apiKey = apiKey
            )
        )
        .build()
}

fun provideKtorfit(
    baseUrl: String,
    enableNetworkLogs: Boolean,
    forceJsonBody: Boolean,
    httpClientEngine: HttpClientEngine,
    provideJwtToken: () -> String
): Ktorfit {
    val builder = Ktorfit.Builder()
    if (baseUrl.isNotBlank()) {
        builder.baseUrl(baseUrl)
    }
    return builder
        .httpClient(
            provideJwtAuthHttpClient(
                httpClientEngine = httpClientEngine,
                enableNetworkLogs = enableNetworkLogs,
                forceJsonBody = forceJsonBody,
                jwtTokenFactory = provideJwtToken
            )
        )
        .build()
}

fun provideKtorfitWithJwtAuth(
    baseUrl: String,
    enableNetworkLogs: Boolean,
    forceJsonBody: Boolean,
    httpClientEngine: HttpClientEngine,
    sessionManager: SessionManager
): Ktorfit {
    val builder = Ktorfit.Builder()
    if (baseUrl.isNotBlank()) {
        builder.baseUrl(baseUrl)
    }
    return builder
        .httpClient(
            provideJwtAuthHttpClient(
                httpClientEngine = httpClientEngine,
                enableNetworkLogs = enableNetworkLogs,
                forceJsonBody = forceJsonBody,
                jwtTokenFactory = { sessionManager.sessionState.value?.token?.accessToken ?: "" }
            )
        )
        .build()
}

suspend inline fun <reified T, reified E> safeApiCall(
    apiCall: suspend () -> T,
): ApiResponse<T, E> {
    return try {
        println("before api call")
        val response = apiCall.invoke()
        println("after api call, response: $response")
        return ApiResponse.Success(response)
    } catch (e: CustomResponseException) {
        val value = e.response.status.value
        try {
            println("CustomResponseException, errorBody: $e")
            ApiResponse.Error.CustomError(value, e.response.body())
        } catch (e: Exception) {
            ApiResponse.Error.CustomError(value, null)
        }
    } catch (e: ClientRequestException) {
        println("ClientRequestException, errorBody: $e")
        ApiResponse.Error.HttpError(e.response.status.value, e.message)
    } catch (e: MissingPageException) {
        println("MissingPageException, errorBody: $e")
        ApiResponse.Error.HttpError(e.response.status.value, e.message ?: "")
    } catch (e: UnresolvedAddressException) {
        println("UnresolvedAddressException, errorBody: $e")
        ApiResponse.Error.NetworkError(e)
    } catch (e: IOException) {
        println("IOException, errorBody: $e")
        ApiResponse.Error.NetworkError(e)
    } catch (e: SerializationException) {
        println("SerializationException, errorBody: $e")
        ApiResponse.Error.SerializationError
    } catch(e: CancellationException) {
        println("CancellationException, errorBody: $e")
        ApiResponse.Error.CancellationError
    }catch (e: Exception) {
        println("Exception, errorBody: $e")
        ApiResponse.Error.UnknownError(e.message ?: "")
    }
}

suspend inline fun <reified T, reified E> safeApiCallWithErrorBodyParsing(
    json: Json,
    apiCall: suspend () -> T
): ApiResponse<T, E> {
    return try {
        val response = apiCall.invoke()
        return ApiResponse.Success(response)
    } catch (e: CustomResponseException) {
        try {
            ApiResponse.Error.CustomError(
                e.response.status.value,
                json.decodeFromString<E>(e.response.bodyAsText())
            )
        } catch (parsingException: Exception) {
            ApiResponse.Error.CustomError(e.response.status.value, null)
        }
    } catch (e: ClientRequestException) {
        ApiResponse.Error.HttpError(e.response.status.value, e.message)
    } catch (e: MissingPageException) {
        ApiResponse.Error.HttpError(e.response.status.value, e.message ?: "")
    } catch (e: UnresolvedAddressException) {
        ApiResponse.Error.NetworkError(e)
    } catch (e: IOException) {
        ApiResponse.Error.NetworkError(e)
    } catch (e: SerializationException) {
        ApiResponse.Error.SerializationError
    } catch (e: Exception) {
        ApiResponse.Error.UnknownError(e.message ?: "")
    }
}

sealed class ApiResponse<out T, out E> {
    /**
     * Represents successful network responses (2xx).
     */
    data class Success<T>(val body: T) : ApiResponse<T, Nothing>()

    sealed class Error<E> : ApiResponse<Nothing, E>() {
        /**
         * Represents server (50x) and client (40x) errors.
         */
        data class CustomError<E>(val code: Int, val errorBody: E?) : Error<E>()
        data class HttpError(val code: Int, val errorBody: String) : Error<Nothing>()

        /**
         * Represent IOExceptions and connectivity issues.
         */
        data class NetworkError(val exception: Exception) : Error<Nothing>()

        /**
         * Represent SerializationExceptions.
         */
        data object SerializationError : Error<Nothing>()

        data class UnknownError(val message: String) : Error<Nothing>()
        data object CancellationError : Error<Nothing>()
    }

    fun <V> map(transform: (T) -> V): ApiResponse<V, E> {
        return when (this) {
            is Success -> Success(transform(body))
            is Error -> this
        }
    }

    fun <V, EE> mapWithErrorType(transformData: (T) -> V, transformErrorType: (E?) -> EE): ApiResponse<V, EE> {
        return when (this) {
            is Success -> Success(transformData(body))
            is Error -> when (this) {
                is Error.CustomError -> Error.CustomError(code, transformErrorType(errorBody))
                is Error.HttpError -> Error.HttpError(code, errorBody)
                is Error.NetworkError -> Error.NetworkError(exception)
                is Error.SerializationError -> Error.SerializationError
                is Error.UnknownError -> Error.UnknownError(message)
                is Error.CancellationError -> Error.CancellationError
            }
        }
    }
}

const val COINGECKO_API_KEY_HEADER = "x-cg-demo-api-key"
//data class ApiErrorException(val code: Int, val errorBody: String?) : Exception()