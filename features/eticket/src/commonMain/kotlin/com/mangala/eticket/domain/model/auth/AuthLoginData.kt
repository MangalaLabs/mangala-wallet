package com.mangala.eticket.domain.model.auth

class AuthLoginData(
    val publicKey: String,
    val accessToken: String,
    val tokenExpiration: Long,
    val refreshToken: String,
    val refreshTokenExpiration: Long,
    val tokenType: String
)