package com.mangala.wallet.remote.provider.ipfs

import com.mangala.wallet.model.provider.ipfs.TokenMetadataResponse
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import com.mangala.wallet.remote.network.CustomError

class IpfsRemoteDataSource(private val ipfsApi: IpfsApi) {

    suspend fun getNftMetadata(uri: String): ApiResponse<TokenMetadataResponse, CustomError> {
        val url = resolveIpfsUriToHttpUrl(uri)

        return safeApiCall { ipfsApi.getMetadata(url) }
    }

    fun isIpfsUri(uri: String): Boolean {
        return uri.startsWith(IPFS_PREFIX)
    }

    fun resolveIpfsUriToHttpUrl(uri: String): String {
        val newPrefix = "https://"
        val newSuffix = ".ipfs.dweb.link"

        return if (isIpfsUri(uri)) {
            val withoutPrefix = uri.substring(IPFS_PREFIX.length)
            val cidAndPath = withoutPrefix.split("/")
            val cid = cidAndPath[0]
            val path = cidAndPath.getOrNull(1) ?: ""

            "$newPrefix$cid$newSuffix/$path"
        } else {
            throw IllegalArgumentException("Invalid URI format")
        }
    }

    companion object {
        private const val IPFS_PREFIX = "ipfs://"
    }
}