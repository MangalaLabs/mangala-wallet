/*
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mangala.antelope.base.api.model.resourceprovider

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

sealed class ErrorItem {
    @Serializable
    data class NodeError(
        @SerialName("path")
        val path: String? = "",
        @SerialName("response")
        val response: Response? = Response()
    ) : ErrorItem() {
        @Serializable
        data class Response(
            @SerialName("headers")
            val headers: Headers? = Headers(),
            @SerialName("json")
            val json: Json? = Json(),
            @SerialName("status")
            val status: Int? = 0,
            @SerialName("text")
            val text: String? = ""
        ) {
            @Serializable
            data class Headers(
                @SerialName("access-control-allow-headers")
                val accessControlAllowHeaders: String? = "",
                @SerialName("access-control-allow-methods")
                val accessControlAllowMethods: String? = "",
                @SerialName("access-control-allow-origin")
                val accessControlAllowOrigin: String? = "",
                @SerialName("connection")
                val connection: String? = "",
                @SerialName("content-length")
                val contentLength: String? = "",
                @SerialName("content-type")
                val contentType: String? = "",
                @SerialName("date")
                val date: String? = "",
                @SerialName("server")
                val server: String? = ""
            )

            @Serializable
            data class Json(
                @SerialName("code")
                val code: Int? = 0,
                @SerialName("error")
                val error: Error? = Error(),
                @SerialName("message")
                val message: String? = ""
            ) {
                @Serializable
                data class Error(
                    @SerialName("code")
                    val code: Int? = 0,
                    @SerialName("details")
                    val details: List<Detail?>? = listOf(),
                    @SerialName("name")
                    val name: String? = "",
                    @SerialName("what")
                    val what: String? = ""
                ) {
                    @Serializable
                    data class Detail(
                        @SerialName("file")
                        val `file`: String? = "",
                        @SerialName("line_number")
                        val lineNumber: Int? = 0,
                        @SerialName("message")
                        val message: String? = "",
                        @SerialName("method")
                        val method: String? = ""
                    )
                }
            }
        }
    }

    data class ProviderError(val error: String) : ErrorItem()
}

fun JsonElement?.toErrorItemOrNull(json: Json): ErrorItem? {
    return when {
        this is JsonPrimitive && this.isString -> return ErrorItem.ProviderError(this.jsonPrimitive.content)
        this is JsonObject -> return json.decodeFromJsonElement(
            ErrorItem.NodeError.serializer(),
            this
        )

        else -> null
    }
}