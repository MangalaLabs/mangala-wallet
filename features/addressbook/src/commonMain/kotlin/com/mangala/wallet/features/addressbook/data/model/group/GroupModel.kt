package com.mangala.wallet.features.addressbook.data.model.group

import kotlinx.serialization.Serializable

@Serializable
data class GroupModel(
    val id: String,
    val name: String,
    val description: String?,
    val icon: String?,
    val color: String?,
    val privacyLevel: String,
    val securityLevel: String,
    val createdAt: Long,
    val updatedAt: Long,
    val mainBlockchainId: String?,
    val mainBlockchainName: String?,
    val mainBlockchainSymbol: String?,
    val mainBlockchainIcon: String?,
    val walletAddressCount: Int
)