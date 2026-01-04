package com.mangala.wallet.remote.provider.alchemy

import com.mangala.wallet.model.provider.alchemy.AlchemyNativeCoinBalanceResponse
import com.mangala.wallet.model.provider.alchemy.AlchemyRequest
import com.mangala.wallet.model.provider.alchemy.AlchemyTokenBalanceResponse
import com.mangala.wallet.model.provider.alchemy.AlchemyTokenMetadataByContractResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Url

interface AlchemyApi {
    @POST
    suspend fun getTokenBalancesByWallet(
        @Url url: String,
        @Body request: AlchemyRequest
    ): AlchemyTokenBalanceResponse

    @POST
    suspend fun getTokenMetadataByContract(
        @Url url: String,
        @Body request: AlchemyRequest
    ): AlchemyTokenMetadataByContractResponse

    @POST
    suspend fun getNativeCoinBalance( // This shouldn't belong here since it's a node call, but we shouldn't import evmcompatible module here either
        @Url url: String,
        @Body request: AlchemyRequest
    ): AlchemyNativeCoinBalanceResponse
}