package com.mangala.eticket.data.local.user.login

import com.mangala.eticket.data.local.ETicketDatabaseWrapper
import commangalaeticketdatabase.AuthenticationEntity

class AuthenticationLocalDataSourceImpl(databaseWrapper: ETicketDatabaseWrapper): AuthenticationLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.eTicketDatabaseQueries

    override fun insertOrReplace(authenticationEntity: AuthenticationEntity) {
        dbQuery.insertOrReplaceUserLogin(
            authenticationEntity.publicKey,
            authenticationEntity.accessToken,
            authenticationEntity.tokenExpiration,
            authenticationEntity.refreshToken,
            authenticationEntity.refreshTokenExpiration,
            authenticationEntity.tokenType
        )
    }

    override fun get(publicKey: String): AuthenticationEntity? {
        return dbQuery.findUserLogin(publicKey, ::mapUserLogin).executeAsOneOrNull()
    }

    override fun delete(publicKey: String) {
        dbQuery.transaction {
            dbQuery.deleteUserLogin(publicKey)
        }
    }
    
    override fun clearAll() {
        dbQuery.transaction {
            dbQuery.clearAllAuthenticationEntities()
        }
    }

    private fun mapUserLogin(
        publicKey: String,
        accessToken: String,
        tokenExpiration: Long,
        refreshToken: String,
        refreshTokenExpiration: Long,
        tokenType: String
    ): AuthenticationEntity {
        return AuthenticationEntity(
            publicKey, accessToken, tokenExpiration, refreshToken, refreshTokenExpiration, tokenType
        )
    }
}