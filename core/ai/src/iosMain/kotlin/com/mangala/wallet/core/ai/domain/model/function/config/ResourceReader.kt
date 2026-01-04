package com.mangala.wallet.core.ai.domain.model.function.config

import dev.icerock.moko.resources.FileResource

/**
 * iOS implementation of ResourceReader
 */
actual class ResourceReader {
    /**
     * Read the content of a file resource as a string on iOS
     *
     * @param resource The file resource to read
     * @return The content of the file as a string
     */
    actual suspend fun readResourceAsString(resource: FileResource): String {
        return resource.readText()
    }
}