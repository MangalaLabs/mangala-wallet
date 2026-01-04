package com.mangala.wallet.remote.network

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.Serializable

// Define your own exception for 2xx responses with error details
class CustomResponseException(response: HttpResponse, message: String): ResponseException(response, message)
