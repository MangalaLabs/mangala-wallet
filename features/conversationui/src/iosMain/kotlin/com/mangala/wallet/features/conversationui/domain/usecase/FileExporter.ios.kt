package com.mangala.wallet.features.conversationui.domain.usecase

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.*
import platform.UIKit.UIDocumentPickerViewController

@OptIn(ExperimentalForeignApi::class)
actual class FileExporter {
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun exportToFile(filename: String, content: String): String? {
        return withContext(Dispatchers.Main) {
            try {
                // Get Documents directory
                val documentsDirectory = NSFileManager.defaultManager.URLsForDirectory(
                    NSDocumentDirectory,
                    NSUserDomainMask
                ).firstOrNull() as? NSURL ?: return@withContext null

                // Create MangalaWallet/ChatExports subdirectory
                val exportDirectory = documentsDirectory.URLByAppendingPathComponent("MangalaWallet/ChatExports")
                if (exportDirectory != null) {
                    NSFileManager.defaultManager.createDirectoryAtURL(
                        exportDirectory,
                        withIntermediateDirectories = true,
                        attributes = null,
                        error = null
                    )

                    // Create file URL
                    val fileURL = exportDirectory.URLByAppendingPathComponent(filename)

                    if (fileURL != null) {
                        // Write content to file
                        val nsString = (content as NSString)
                        val success = nsString.writeToURL(
                            fileURL,
                            atomically = true,
                            encoding = NSUTF8StringEncoding,
                            error = null
                        )

                        if (success) {
                            println("Chat export saved to: ${fileURL.path}")
                            fileURL.path
                        } else {
                            println("Failed to write chat export file")
                            null
                        }
                    } else {
                        println("Failed to create file URL")
                        null
                    }
                } else {
                    println("Failed to create export directory")
                    null
                }
            } catch (e: Exception) {
                println("Failed to export chat to file: ${e.message}")
                null
            }
        }
    }
}