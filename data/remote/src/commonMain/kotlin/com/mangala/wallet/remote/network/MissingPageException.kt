package com.mangala.wallet.remote.network

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse

// Define your own exception for 404 responses
class MissingPageException(response: HttpResponse, message: String): ResponseException(response, message)