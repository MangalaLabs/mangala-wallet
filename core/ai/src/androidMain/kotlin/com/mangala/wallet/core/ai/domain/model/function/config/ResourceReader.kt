package com.mangala.wallet.core.ai.domain.model.function.config

import android.content.Context
import dev.icerock.moko.resources.FileResource

/**
 * Android implementation of ResourceReader
 */
actual class ResourceReader(private val context: Context) {
    /**
     * Read the content of a file resource as a string on Android
     *
     * @param resource The file resource to read
     * @return The content of the file as a string
     */
    actual suspend fun readResourceAsString(resource: FileResource): String {
        return resource.readText(context)
    }
}