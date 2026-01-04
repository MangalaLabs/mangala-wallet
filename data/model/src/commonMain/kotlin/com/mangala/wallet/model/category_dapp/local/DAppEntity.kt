package com.mangala.wallet.model.category_dapp.local

import com.mangala.wallet.model.Dto
import com.mangala.wallet.model.Model
import com.mangala.wallet.model.category_dapp.domain.DAppModel
import kotlinx.serialization.Serializable
@Serializable
data class DAppEntity(
    val uuid: String,
    val title: String?,
    val description: String?,
    val iconUrl: String?,
    val bannerUrl: String?,
    val redirectLink: String?, // New field
    val chainId: String? // New field
) : Dto {
    override fun mapToDomainModel(): Model {
        return DAppModel(
            uuid = this.uuid,
            title = this.title ?: "",
            description = this.description ?: "",
            iconUrl = this.iconUrl ?: "",
            bannerUrl = this.bannerUrl ?: "",
            redirectLink = this.redirectLink ?: "", // Map new field
            chainId = this.chainId ?: "" // Map new field
        )
    }
}
