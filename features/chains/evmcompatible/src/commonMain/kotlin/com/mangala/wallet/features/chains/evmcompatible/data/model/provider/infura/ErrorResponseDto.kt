package com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura

@kotlinx.serialization.Serializable
data class ErrorResponseDto(
    val id: Int?,
    val jsonrpc: String?,
    val error: ErrorResponseDto.Error?
){
    @kotlinx.serialization.Serializable
    data class Error(
        val code: Int?,
        val message: String?
    )
}