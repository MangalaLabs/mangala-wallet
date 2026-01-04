package com.mangala.wallet.features.conversationui.domain.usecase

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

actual class FileExporter(
    private val context: Context
) {
    actual suspend fun exportToFile(filename: String, content: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Use external storage Documents directory for user-accessible files
                val documentsDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    "MangalaWallet/ChatExports"
                )
                
                // Create directory if it doesn't exist
                if (!documentsDir.exists()) {
                    documentsDir.mkdirs()
                }
                
                val file = File(documentsDir, filename)
                
                // Write content to file
                FileOutputStream(file).use { outputStream ->
                    outputStream.write(content.toByteArray())
                }
                
                println("Chat export saved to: ${file.absolutePath}")
                file.absolutePath
            } catch (e: IOException) {
                println("Failed to export chat to file: ${e.message}")
                e.printStackTrace()
                null
            } catch (e: SecurityException) {
                println("Permission denied to write file: ${e.message}")
                e.printStackTrace()
                null
            }
        }
    }
}