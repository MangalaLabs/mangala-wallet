package com.mangala.eticket.data.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class UserResponse (
    val id: String,
    @SerialName("full_name")
    val fullName: String?,
    @SerialName("phone_number")
    val phoneNumber: String?,
    val email: String?,
    @SerialName("citizen_id")
    val citizenId: String?,
    val address: String?,
    @SerialName("country_code")
    val countryCode: String?,
    val nonce: Int?,
    @SerialName("kyc_level")
    val kycLevel: Int?
)