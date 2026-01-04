package com.mangala.wallet.model.person.local

import com.mangala.wallet.model.Dto
import com.mangala.wallet.model.Model
import com.mangala.wallet.model.person.domain.PersonModel
import kotlinx.serialization.Serializable

@Serializable
data class PersonEntity(
    val name: String?,
    val height: String?,
    val mass: String?,
    val url: String?
): Dto {
    override fun mapToDomainModel(): PersonModel {
        return PersonModel(name ?: "", height ?: "", mass ?: "", url ?: "")
    }
}