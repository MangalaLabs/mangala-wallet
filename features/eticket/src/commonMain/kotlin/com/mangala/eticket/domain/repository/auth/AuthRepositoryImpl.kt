package com.mangala.eticket.domain.repository.auth

import com.mangala.eticket.data.local.user.login.AuthenticationLocalDataSource
import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.auth.AuthenticationResponse
import com.mangala.eticket.data.model.auth.LoginRequest
import com.mangala.eticket.data.remote.AuthDataSource
import com.mangala.eticket.domain.model.auth.AuthLoginData
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError
import commangalaeticketdatabase.AuthenticationEntity

class AuthRepositoryImpl(
    private val authDataSource: AuthDataSource,
    private val authLocalDataSource: AuthenticationLocalDataSource
): AuthRepository {
    override suspend fun login(body: LoginRequest): ApiResponse<ETicketResponse<AuthenticationResponse>, CustomError> {
        return authDataSource.login(body)
    }

    override fun saveAuthEntity(authLoginData: AuthLoginData) {
        val authEntity = convertToAuthEntity(authLoginData)
        authLocalDataSource.insertOrReplace(authEntity)
    }

    override fun getAuthEntity(id: String): AuthLoginData? {
        val authEntity = authLocalDataSource.get(id)
        return authEntity?.let {
            convertToAuthLoginData(authEntity)
        }
    }

    private fun convertToAuthEntity(authLogin: AuthLoginData): AuthenticationEntity {
        return AuthenticationEntity(
            publicKey = authLogin.publicKey,
            accessToken = authLogin.accessToken,
            tokenExpiration = authLogin.tokenExpiration,
            refreshToken = authLogin.refreshToken,
            refreshTokenExpiration = authLogin.refreshTokenExpiration,
            tokenType = authLogin.tokenType
        )
    }

    private fun convertToAuthLoginData(authEntity: AuthenticationEntity): AuthLoginData {
        return AuthLoginData(
            publicKey = authEntity.publicKey,
            accessToken = authEntity.accessToken,
            tokenExpiration = authEntity.tokenExpiration,
            refreshToken = authEntity.refreshToken,
            refreshTokenExpiration = authEntity.refreshTokenExpiration,
            tokenType = authEntity.tokenType
        )
    }
}