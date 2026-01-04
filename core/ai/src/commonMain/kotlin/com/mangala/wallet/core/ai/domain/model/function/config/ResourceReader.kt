package com.mangala.wallet.core.ai.domain.model.function.config

import dev.icerock.moko.resources.FileResource

/**
 * Interface for reading file resources in a platform-independent way
 */
expect class ResourceReader {
    /**
     * Read the content of a file resource as a string
     *
     * @param resource The file resource to read
     * @return The content of the file as a string
     */
    suspend fun readResourceAsString(resource: FileResource): String
}