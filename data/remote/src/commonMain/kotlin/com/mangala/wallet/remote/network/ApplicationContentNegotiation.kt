package com.mangala.wallet.remote.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentConverterException
import io.ktor.client.plugins.contentnegotiation.JsonContentTypeMatcher
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.accept
import io.ktor.client.statement.HttpResponseContainer
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.client.utils.EmptyContent
import io.ktor.http.ContentType
import io.ktor.http.ContentTypeMatcher
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.charset
import io.ktor.http.content.NullBody
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentType
import io.ktor.serialization.Configuration
import io.ktor.serialization.ContentConverter
import io.ktor.serialization.deserialize
import io.ktor.serialization.suitableCharset
import io.ktor.util.AttributeKey
import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.util.reflect.TypeInfo
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.KtorDsl
import io.ktor.utils.io.charsets.Charset
import io.ktor.utils.io.charsets.Charsets
import kotlin.reflect.KClass

private val LOGGER = KtorSimpleLogger("com.mangala.wallet.remote.network.ApplicationContentNegotiation")

// A custom ContentNegotiation plugins that maps unspecified ContentType to ContentType.Application.Json
class ApplicationContentNegotiation internal constructor(
    internal val registrations: List<Config.ConverterRegistration>,
    internal val ignoredTypes: Set<KClass<*>>
) {

    /**
     * A [ContentNegotiation] configuration that is used during installation.
     */
    public class Config : Configuration {

        internal class ConverterRegistration(
            val converter: ContentConverter,
            val contentTypeToSend: ContentType,
            val contentTypeMatcher: ContentTypeMatcher
        )

        internal val ignoredTypes: MutableSet<KClass<*>> = mutableSetOf()

        internal val registrations = mutableListOf<ConverterRegistration>()

        /**
         * Registers a [contentType] to a specified [converter] with an optional [configuration] script for a converter.
         */
        public override fun <T : ContentConverter> register(
            contentType: ContentType,
            converter: T,
            configuration: T.() -> Unit
        ) {
            val matcher = when (contentType) {
                ContentType.Application.Json -> JsonContentTypeMatcher
                else -> defaultMatcher(contentType)
            }
            register(contentType, converter, matcher, configuration)
        }

        /**
         * Registers a [contentTypeToSend] and [contentTypeMatcher] to a specified [converter] with
         * an optional [configuration] script for a converter.
         */
        public fun <T : ContentConverter> register(
            contentTypeToSend: ContentType,
            converter: T,
            contentTypeMatcher: ContentTypeMatcher,
            configuration: T.() -> Unit
        ) {
            val registration = ConverterRegistration(
                converter.apply(configuration),
                contentTypeToSend,
                contentTypeMatcher
            )
            registrations.add(registration)
        }

        /**
         * Adds a type to the list of types that should be ignored by [ContentNegotiation].
         *
         * The list contains the [HttpStatusCode], [ByteArray], [String] and streaming types by default.
         */
        public inline fun <reified T> ignoreType() {
            ignoreType(T::class)
        }

        /**
         * Remove [T] from the list of types that should be ignored by [ContentNegotiation].
         */
        public inline fun <reified T> removeIgnoredType() {
            removeIgnoredType(T::class)
        }

        /**
         * Remove [type] from the list of types that should be ignored by [ContentNegotiation].
         */
        public fun removeIgnoredType(type: KClass<*>) {
            ignoredTypes.remove(type)
        }

        /**
         * Adds a [type] to the list of types that should be ignored by [ContentNegotiation].
         *
         * The list contains the [HttpStatusCode], [ByteArray], [String] and streaming types by default.
         */
        public fun ignoreType(type: KClass<*>) {
            ignoredTypes.add(type)
        }

        /**
         * Clear all configured ignored types including defaults.
         */
        public fun clearIgnoredTypes() {
            ignoredTypes.clear()
        }

        private fun defaultMatcher(pattern: ContentType): ContentTypeMatcher = object :
            ContentTypeMatcher {
            override fun contains(contentType: ContentType): Boolean = contentType.match(pattern)
        }
    }

    internal suspend fun convertRequest(request: HttpRequestBuilder, body: Any): Any? {
        registrations.forEach {
            LOGGER.trace("Adding Accept=${it.contentTypeToSend.contentType} header for ${request.url}")
            request.accept(it.contentTypeToSend)
        }

        if (body is OutgoingContent || ignoredTypes.any { it.isInstance(body) }) {
            LOGGER.trace(
                "Body type ${body::class} is in ignored types. " +
                        "Skipping ContentNegotiation for ${request.url}."
            )
            return null
        }
        val contentType = request.contentType() ?: run {
            LOGGER.trace("Request doesn't have Content-Type header. Skipping ContentNegotiation for ${request.url}.")
            return null
        }

        if (body is Unit) {
            LOGGER.trace("Sending empty body for ${request.url}")
            request.headers.remove(HttpHeaders.ContentType)
            return EmptyContent
        }

        val matchingRegistrations =
            registrations.filter { it.contentTypeMatcher.contains(contentType) }
                .takeIf { it.isNotEmpty() } ?: run {
                LOGGER.trace(
                    "None of the registered converters match request Content-Type=$contentType. " +
                            "Skipping ContentNegotiation for ${request.url}."
                )
                return null
            }
        if (request.bodyType == null) {
            LOGGER.trace("Request has unknown body type. Skipping ContentNegotiation for ${request.url}.")
            return null
        }
        request.headers.remove(HttpHeaders.ContentType)

        // Pick the first one that can convert the subject successfully
        val serializedContent = matchingRegistrations.firstNotNullOfOrNull { registration ->
            val result = registration.converter.serialize(
                contentType,
                contentType.charset() ?: Charsets.UTF_8,
                request.bodyType!!,
                body.takeIf { it != NullBody }
            )
            if (result != null) {
                LOGGER.trace("Converted request body using ${registration.converter} for ${request.url}")
            }
            result
        } ?: throw ContentConverterException(
            "Can't convert $body with contentType $contentType using converters " +
                    matchingRegistrations.joinToString { it.converter.toString() }
        )

        return serializedContent
    }

    @OptIn(InternalAPI::class)
    internal suspend fun convertResponse(
        requestUrl: Url,
        info: TypeInfo,
        body: Any,
        responseContentType: ContentType,
        charset: Charset = Charsets.UTF_8
    ): Any? {
        if (body !is ByteReadChannel) {
            LOGGER.trace("Response body is already transformed. Skipping ContentNegotiation for $requestUrl.")
            return null
        }
        if (info.type in ignoredTypes) {
            LOGGER.trace(
                "Response body type ${info.type} is in ignored types. " +
                        "Skipping ContentNegotiation for $requestUrl."
            )
            return null
        }

        val suitableConverters = registrations
            .filter { it.contentTypeMatcher.contains(responseContentType) }
            .map { it.converter }
            .takeIf { it.isNotEmpty() }
            ?: run {
                LOGGER.trace(
                    "None of the registered converters match response with Content-Type=$responseContentType. " +
                            "Skipping ContentNegotiation for $requestUrl."
                )
                return null
            }

        val result = suitableConverters.deserialize(body, info, charset)
        if (result !is ByteReadChannel) {
            LOGGER.trace("Response body was converted to ${result::class} for $requestUrl.")
        }
        return result
    }

    /**
     * A companion object used to install a plugin.
     */
    @KtorDsl
    public companion object Plugin :
        HttpClientPlugin<Config, ApplicationContentNegotiation> {
        public override val key: AttributeKey<ApplicationContentNegotiation> =
            AttributeKey("ContentNegotiation")

        override fun prepare(block: Config.() -> Unit): ApplicationContentNegotiation {
            val config = Config().apply(block)
            return ApplicationContentNegotiation(config.registrations, config.ignoredTypes)
        }

        override fun install(plugin: ApplicationContentNegotiation, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Transform) {
                val result = plugin.convertRequest(context, subject) ?: return@intercept
                proceedWith(result)
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { (info, body) ->
                val contentType = context.response.contentType() ?: ContentType.Application.Json
                val charset = context.request.headers.suitableCharset()

                val deserializedBody =
                    plugin.convertResponse(context.request.url, info, body, contentType, charset)
                        ?: return@intercept
                val result = HttpResponseContainer(info, deserializedBody)
                proceedWith(result)
            }
        }
    }
}