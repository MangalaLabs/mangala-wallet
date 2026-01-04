package com.mangala.eticket.di

import com.mangala.eticket.data.local.securestorage.SecureStorageWrapperConstants.ACCESS_TOKEN
import com.mangala.eticket.data.local.securestorage.SecureStorageWrapperConstants.TOKEN_TYPE
import com.mangala.eticket.data.remote.AuthApi
import com.mangala.eticket.data.remote.CategoriesApi
import com.mangala.eticket.data.remote.EventsApi
import com.mangala.eticket.data.remote.UserApi
import com.mangala.eticket.data.remote.favourite.UserEventFavouriteApi
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.remote.network.CustomError
import com.mangala.wallet.remote.network.CustomResponseException
import com.mangala.wallet.remote.network.MissingPageException
import de.jensklingenberg.ktorfit.Ktorfit
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.append
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.koin.dsl.module

const val BASE_URL = "https://moccasin-tight-jaguar.ngrok-free.app/e-ticket/"

fun eTicketRemoteModule() = module {
    single<CategoriesApi> {
        provideKtorfit(
            BASE_URL,
            true,
            get(),
            get()
        ).create()
    }
    single<EventsApi> {
        provideKtorfit(
            BASE_URL,
            true,
            get(),
            get()
        ).create()
    }
    single<AuthApi> {
        provideKtorfit(
            BASE_URL,
            true,
            get(),
            get()
        ).create()
    }
    single<UserApi> {
        provideKtorfit(
            BASE_URL,
            true,
            get(),
            get()
        ).create()
    }
    single<UserEventFavouriteApi> {
        provideKtorfit(
            BASE_URL,
            true,
            get(),
            get()
        ).create()
    }
}

fun provideHttpClient(
    httpClientEngine: HttpClientEngine,
    enableNetworkLogs: Boolean,
    secureStorage: SecureStorageWrapper
): HttpClient {
    return HttpClient(httpClientEngine) {
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true; prettyPrint = true })
        }
        if (enableNetworkLogs) {
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
        // Install HttpRequestRetry for retry mechanism
        install(HttpRequestRetry) {
            retryIf { httpRequest, httpResponse ->
                // Only retry if the request doesn't have a status code
                httpResponse.status.value.let { it !in 200..599 }
            }
            retryOnException(maxRetries = 3, retryOnTimeout = true)
            delayMillis { retry -> retry * 3000L } // retries in 3, 6, 9, etc. seconds
            modifyRequest { request -> request.headers.append("x-retry-count", retryCount.toString()) }
        }

        // Enable default validation and install HttpCallValidator for custom validation --> TODO: 401
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
                val clientException = exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
                val exceptionResponse = clientException.response
                if (exceptionResponse.status == HttpStatusCode.NotFound) {
                    val exceptionResponseText = exceptionResponse.bodyAsText()
                    throw MissingPageException(exceptionResponse, exceptionResponseText)
                }
            }
        }

        install(DefaultRequest) {
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json)
            val tokenType = secureStorage.getValue(TOKEN_TYPE)
            val accessToken = secureStorage.getValue(ACCESS_TOKEN)

            tokenType?.let {type ->
                accessToken?.let {
                    headers.append(HttpHeaders.Authorization, "$type $it")
                }
            }
        }
    }
}

inline fun provideKtorfit(
    baseUrl: String,
    enableNetworkLogs: Boolean,
    httpClientEngine: HttpClientEngine,
    secureStorage: SecureStorageWrapper
): Ktorfit {
    val builder = Ktorfit.Builder()
    if (baseUrl.isNotBlank()) {
        builder.baseUrl(baseUrl)
    }
    return builder
        .httpClient(provideHttpClient(httpClientEngine, enableNetworkLogs, secureStorage))
        .build()
}

suspend inline fun <reified T, reified E> safeApiCall(
    apiCall: suspend () -> T,
): ApiResponse<T, E> {
    return try {
        val response = apiCall.invoke()
        return ApiResponse.Success(response)
    } catch (e: CustomResponseException) {
        ApiResponse.Error.CustomError(e.response.status.value, e.errorBody())
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

suspend inline fun <reified E> ResponseException.errorBody(): E? =
    try {
        response.body()
    } catch (e: SerializationException) {
        null
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
        object SerializationError : Error<Nothing>()

        data class UnknownError(val message: String) : Error<Nothing>()
    }

    fun <V> map(transform: (T) -> V): ApiResponse<V, E> {
        return when (this) {
            is Success -> Success(transform(body))
            is Error -> this
        }
    }
}