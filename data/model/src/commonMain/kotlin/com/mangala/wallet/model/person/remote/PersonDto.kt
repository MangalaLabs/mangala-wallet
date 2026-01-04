package com.mangala.wallet.model.person.remote

import com.mangala.wallet.model.Dto
import com.mangala.wallet.model.Model
import com.mangala.wallet.model.person.domain.PersonModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonDto(
    @SerialName("")
    val name: String?,
    val height: String?,
    val mass: String?,
    val url: String?
): Dto {
    override fun mapToDomainModel(): PersonModel {
        return PersonModel(name ?: "", height ?: "", mass ?: "", url ?: "")
    }

}