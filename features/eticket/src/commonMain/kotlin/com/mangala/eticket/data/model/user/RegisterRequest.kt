package com.mangala.eticket.data.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RegisterRequest(
    val id: String,
    @SerialName("full_name")
    val fullName: String? = null,
    @SerialName("phone_number")
    val phoneNumber: String? = null,
    val email: String? = null,
    @SerialName("citizen_id")
    val citizenId: String? = null,
    val address: String? = null,
    @SerialName("country_code")
    val countryCode: String? = null
)