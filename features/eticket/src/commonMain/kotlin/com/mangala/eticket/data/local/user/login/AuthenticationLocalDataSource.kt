package com.mangala.eticket.data.local.user.login

import commangalaeticketdatabase.AuthenticationEntity

interface AuthenticationLocalDataSource {
    fun insertOrReplace(authenticationEntity: AuthenticationEntity)
    fun get(publicKey: String): AuthenticationEntity?
    fun delete(publicKey: String)
    fun clearAll()
}