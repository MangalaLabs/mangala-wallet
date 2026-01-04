package com.mangala.wallet.model.category_dapp.domain

import com.mangala.wallet.model.Dto
import com.mangala.wallet.model.Model
import com.mangala.wallet.model.category_dapp.local.DAppEntity
import com.mangala.wallet.model.category_dapp.remote.DAppRemote
import kotlinx.serialization.Serializable
@Serializable
data class DAppModel(
    val uuid: String,
    val title: String,
    val description: String,
    val iconUrl: String,
    val bannerUrl: String,
    val redirectLink: String, // New field
    val chainId: String // New field
) : Model {
    override fun toLocalDto(): Dto {
        return DAppEntity(
            uuid = this.uuid,
            title = this.title,
            description = this.description,
            iconUrl = this.iconUrl,
            bannerUrl = this.bannerUrl,
            redirectLink = this.redirectLink,
            chainId = this.chainId
        )
    }

    override fun toRemoteDto(): Dto {
        return DAppRemote(
            uuid = this.uuid,
            title = this.title,
            description = this.description,
            iconUrl = this.iconUrl,
            bannerUrl = this.bannerUrl,
            redirectLink = this.redirectLink,
            chainId = this.chainId
        )
    }
}
