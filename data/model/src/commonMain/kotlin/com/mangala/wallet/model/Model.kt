package com.mangala.wallet.model

interface Model {
    fun toLocalDto(): Dto
    fun toRemoteDto(): Dto
}