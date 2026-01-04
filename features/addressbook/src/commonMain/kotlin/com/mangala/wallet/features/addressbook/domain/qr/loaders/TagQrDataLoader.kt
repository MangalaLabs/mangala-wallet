package com.mangala.wallet.features.addressbook.domain.qr.loaders

import com.mangala.wallet.features.addressbook.domain.qr.QrDataLoader
import com.mangala.wallet.features.addressbook.domain.qr.QrLoadResult
import com.mangala.wallet.features.addressbook.domain.qr.QrDisplayData
import com.mangala.wallet.features.addressbook.domain.qr.QrType
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository

/**
 * Tag-specific QR data loader
 */
class TagQrDataLoader(
    private val tagRepository: TagRepository
) : QrDataLoader<String> {
    
    override suspend fun loadData(id: String): QrLoadResult {
        return try {
            val tagEntity = tagRepository.getTagById(id)
            
            if (tagEntity != null) {
                // Get contact count for this tag
                val contactIds = tagRepository.getContactIdsWithTag(id)
                val contactCount = contactIds.size
                
                val displayData = QrDisplayData(
                    type = QrType.TAG,
                    id = tagEntity.id,
                    title = tagEntity.name,
                    subtitle = "Tag ($contactCount contacts)",
                    primaryInfo = contactCount.toString(),
                    secondaryInfo = null,
                    icon = tagEntity.icon,
                    color = tagEntity.color,
                    metadata = mapOf<String, Any>(
                        "textColor" to (tagEntity.textColor ?: ""),
                        "contactCount" to contactCount,
                        "createdAt" to tagEntity.createdAt.toEpochMilliseconds(),
                        "updatedAt" to tagEntity.updatedAt.toEpochMilliseconds()
                    )
                )
                QrLoadResult.Success(displayData)
            } else {
                QrLoadResult.Error("Tag not found")
            }
        } catch (e: Exception) {
            QrLoadResult.Error("Failed to load tag data", e)
        }
    }
    
    override fun getQrType(): QrType = QrType.TAG
}