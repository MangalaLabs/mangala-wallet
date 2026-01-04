package com.mangala.wallet.core.ai.domain.model.function.config

import dev.icerock.moko.resources.FileResource
import java.io.InputStreamReader

/**
 * JVM implementation of ResourceReader
 */
actual class ResourceReader {
    /**
     * Read the content of a file resource as a string on JVM
     *
     * @param resource The file resource to read
     * @return The content of the file as a string
     */
    actual suspend fun readResourceAsString(resource: FileResource): String {
        // On JVM, we need to use ClassLoader to access resources
        val classLoader = this::class.java.classLoader
        val resourceName = resource.filePath.removePrefix("files/")
        
        return classLoader.getResourceAsStream(resourceName)?.use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                reader.readText()
            }
        } ?: throw IllegalArgumentException("Resource not found: $resourceName")
    }
}