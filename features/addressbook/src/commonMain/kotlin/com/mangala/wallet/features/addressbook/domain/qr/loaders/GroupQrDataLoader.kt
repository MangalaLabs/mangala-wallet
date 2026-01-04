package com.mangala.wallet.features.addressbook.domain.qr.loaders

import com.mangala.wallet.features.addressbook.domain.qr.QrDataLoader
import com.mangala.wallet.features.addressbook.domain.qr.QrLoadResult
import com.mangala.wallet.features.addressbook.domain.qr.QrDisplayData
import com.mangala.wallet.features.addressbook.domain.qr.QrType
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetGroupByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetGroupDetailByIdUseCase

/**
 * Group-specific QR data loader
 */
class GroupQrDataLoader(
    private val getGroupByIdUseCase: GetGroupByIdUseCase,
    private val getGroupDetailByIdUseCase: GetGroupDetailByIdUseCase
) : QrDataLoader<String> {
    
    override suspend fun loadData(id: String): QrLoadResult {
        return try {
            val groupEntity = getGroupByIdUseCase(id)
            
            if (groupEntity != null) {
                // Try to get detailed group information
                val groupDetail = getGroupDetailByIdUseCase(id)
                
                if (groupDetail != null) {
                    val displayData = QrDisplayData(
                        type = QrType.GROUP,
                        id = groupDetail.group.id,
                        title = groupDetail.group.name,
                        subtitle = "Group (${groupDetail.getMemberCount()} members)",
                        primaryInfo = groupDetail.getMemberCount().toString(),
                        secondaryInfo = groupDetail.mainBlockchainType?.symbol,
                        icon = groupDetail.group.icon,
                        color = groupDetail.group.color,
                        metadata = mapOf<String, Any>(
                            "description" to (groupDetail.group.description ?: ""),
                            "privacyLevel" to groupDetail.group.privacyLevel.name,
                            "securityLevel" to groupDetail.group.securityLevel.name,
                            "mainBlockchainName" to (groupDetail.mainBlockchainType?.name ?: ""),
                            "mainBlockchainId" to (groupDetail.mainBlockchainType?.id ?: ""),
                            "createdAt" to groupDetail.group.createdAt.toEpochMilliseconds(),
                            "updatedAt" to groupDetail.group.updatedAt.toEpochMilliseconds()
                        )
                    )
                    QrLoadResult.Success(displayData)
                } else {
                    // Fallback to basic group entity info
                    val displayData = QrDisplayData(
                        type = QrType.GROUP,
                        id = groupEntity.id,
                        title = groupEntity.name,
                        subtitle = "Group",
                        primaryInfo = "0", // Will be updated when detail is available
                        secondaryInfo = null,
                        icon = groupEntity.icon,
                        color = groupEntity.color,
                        metadata = mapOf<String, Any>(
                            "description" to (groupEntity.description ?: ""),
                            "privacyLevel" to groupEntity.privacyLevel.name,
                            "securityLevel" to groupEntity.securityLevel.name,
                            "createdAt" to groupEntity.createdAt.toEpochMilliseconds(),
                            "updatedAt" to groupEntity.updatedAt.toEpochMilliseconds()
                        )
                    )
                    QrLoadResult.Success(displayData)
                }
            } else {
                QrLoadResult.Error("Group not found")
            }
        } catch (e: Exception) {
            QrLoadResult.Error("Failed to load group data", e)
        }
    }
    
    override fun getQrType(): QrType = QrType.GROUP
}