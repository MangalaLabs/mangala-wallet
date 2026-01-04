package com.mangala.wallet.remote.provider.ipfs

import com.mangala.wallet.model.provider.ipfs.TokenMetadataResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Url

interface IpfsApi {

    @GET("")
    suspend fun getMetadata(@Url url: String): TokenMetadataResponse
}