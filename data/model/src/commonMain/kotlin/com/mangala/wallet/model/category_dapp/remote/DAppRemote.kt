package com.mangala.wallet.model.category_dapp.remote

import com.mangala.wallet.model.Dto
import com.mangala.wallet.model.Model
import com.mangala.wallet.model.category_dapp.domain.DAppModel
import kotlinx.serialization.Serializable

@Serializable
data class DAppRemote(
    val uuid: String,
    val title: String?,
    val description: String?,
    val iconUrl: String?,
    val bannerUrl: String?,
    val redirectLink: String?,
    val chainId: String?
) : Dto {
    override fun mapToDomainModel(): Model {
        return DAppModel(
            uuid = this.uuid,
            title = this.title ?: "",
            description = this.description ?: "",
            iconUrl = this.iconUrl ?: "",
            bannerUrl = this.bannerUrl ?: "",
            redirectLink = this.redirectLink ?: "",
            chainId = this.chainId ?: ""
        )
    }
}
