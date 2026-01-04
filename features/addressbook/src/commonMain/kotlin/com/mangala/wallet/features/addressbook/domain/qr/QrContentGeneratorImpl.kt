package com.mangala.wallet.features.addressbook.domain.qr

import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.datetime.Clock

/**
 * Implementation of QR content generator with structured JSON format
 */
class QrContentGeneratorImpl : QrContentGenerator {
    
    override fun generateContent(displayData: QrDisplayData): QrContentResult {
        return try {
            val jsonContent = when (displayData.type) {
                QrType.CONTACT -> generateContactQr(displayData)
                QrType.GROUP -> generateGroupQr(displayData)
                QrType.TAG -> generateTagQr(displayData)
                QrType.ADDRESS -> generateAddressQr(displayData)
            }
            
            QrContentResult.Success(
                content = jsonContent,
                displayData = displayData
            )
        } catch (e: Exception) {
            QrContentResult.Error("Failed to generate QR content: ${e.message}")
        }
    }
    
    private fun generateContactQr(data: QrDisplayData): String {
        return buildJsonObject {
            put("type", "contact")
            put("version", "1.0")
            put("id", data.id)
            put("name", data.title)
            put("address", data.primaryInfo ?: "")
            put("blockchain", data.secondaryInfo ?: "")
            put("timestamp", Clock.System.now().toEpochMilliseconds())
            
            // Optional metadata
            data.metadata.forEach { (key, value) ->
                when (value) {
                    is String -> put(key, value)
                    is Number -> put(key, value)
                    is Boolean -> put(key, value)
                }
            }
        }.toString()
    }
    
    private fun generateGroupQr(data: QrDisplayData): String {
        return buildJsonObject {
            put("type", "group")
            put("version", "1.0")
            put("id", data.id)
            put("name", data.title)
            put("memberCount", data.primaryInfo ?: "0")
            put("blockchain", data.secondaryInfo ?: "")
            put("timestamp", Clock.System.now().toEpochMilliseconds())
            
            // Group specific metadata
            data.metadata.forEach { (key, value) ->
                when (value) {
                    is String -> put(key, value)
                    is Number -> put(key, value)
                    is Boolean -> put(key, value)
                }
            }
        }.toString()
    }
    
    private fun generateTagQr(data: QrDisplayData): String {
        return buildJsonObject {
            put("type", "tag")
            put("version", "1.0")
            put("id", data.id)
            put("name", data.title)
            put("contactCount", data.primaryInfo ?: "0")
            put("color", data.color ?: "")
            put("timestamp", Clock.System.now().toEpochMilliseconds())
            
            // Tag specific metadata
            data.metadata.forEach { (key, value) ->
                when (value) {
                    is String -> put(key, value)
                    is Number -> put(key, value)
                    is Boolean -> put(key, value)
                }
            }
        }.toString()
    }
    
    private fun generateAddressQr(data: QrDisplayData): String {
        return buildJsonObject {
            put("type", "address")
            put("version", "1.0")
            put("id", data.id)
            put("address", data.primaryInfo ?: "")
            put("alias", data.title)
            put("walletType", data.secondaryInfo ?: "")
            put("timestamp", Clock.System.now().toEpochMilliseconds())
            
            // Address specific metadata
            data.metadata.forEach { (key, value) ->
                when (value) {
                    is String -> put(key, value)
                    is Number -> put(key, value)
                    is Boolean -> put(key, value)
                }
            }
        }.toString()
    }
}