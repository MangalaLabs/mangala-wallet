package com.mangala.wallet.model.person.domain

import com.mangala.wallet.model.Dto
import com.mangala.wallet.model.Model
import com.mangala.wallet.model.person.local.PersonEntity
import com.mangala.wallet.model.person.remote.PersonDto
import kotlinx.serialization.Serializable

@Serializable
data class PersonModel(
    val name: String,
    val height: String,
    val mass: String,
    val url: String
): Model {
    override fun toLocalDto(): PersonEntity {
        return PersonEntity(name, height, mass, url)
    }

    override fun toRemoteDto(): PersonDto {
        return PersonDto(name, height, mass, url)
    }

}