package com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models

import com.mangala.wallet.common.test.utils.SharedFileReader
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertTrue

class RpcLatestBlockResponseTest {

    @Test
    fun testParseResponseEthereumWithTransactionDetail() {
        val data: RpcLatestBlockResponse =
            Json(builderAction = {
                ignoreUnknownKeys = false
            }).decodeFromString(SharedFileReader().loadJsonFile("RpcLatestBlockEthereum.json")!!)

        assertTrue(true) // assert parse successfully without exception
    }

    @Test
    fun testParseResponseEthereumWithoutTransactionDetail() {
        val data: RpcLatestBlockResponseWithoutTransactionDetail =
            Json(builderAction = {
                ignoreUnknownKeys = false
            }).decodeFromString(SharedFileReader().loadJsonFile("RpcLatestBlockEthereumWithoutTransactionDetail.json")!!)

        assertTrue(true) // assert parse successfully without exception
    }

    @Test
    fun testParseResponseBinanceSmartChainWithTransactionDetail() {
        val data: RpcLatestBlockResponse =
            Json(builderAction = {
                ignoreUnknownKeys = false
            }).decodeFromString(SharedFileReader().loadJsonFile("RpcLatestBlockBinanceSmartChain.json")!!)

        assertTrue(true) // assert parse successfully without exception
    }
}