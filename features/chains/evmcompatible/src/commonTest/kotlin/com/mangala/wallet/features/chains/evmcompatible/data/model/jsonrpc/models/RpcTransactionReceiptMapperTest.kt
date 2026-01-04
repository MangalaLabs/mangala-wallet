package com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.models

import com.mangala.wallet.common.test.utils.SharedFileReader
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertTrue

class RpcTransactionReceiptMapperTest {
    @Test
    fun testParse() {
        val data: RpcTransactionReceiptResponse =
            Json(builderAction = {
                ignoreUnknownKeys = true
            }).decodeFromString(SharedFileReader().loadJsonFile("RpcTransactionReceipt.json")!!)

        assertTrue(true) // assert parse successfully without exception
    }
}