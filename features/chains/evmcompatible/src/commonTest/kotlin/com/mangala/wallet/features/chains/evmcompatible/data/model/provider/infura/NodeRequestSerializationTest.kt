package com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NodeRequestSerializationTest {

    @Test
    fun testSerializeBoolean() {
        val request = NodeRequest(
            "2.0",
            "eth_getBlockByNumber",
            listOf(Param.StringParam("latest"), Param.BooleanParam(true)),
            1
        )
        val data = Json.encodeToString(request)

        val expected = """{"jsonrpc":"2.0","method":"eth_getBlockByNumber","params":["latest",true],"id":1}"""
        assertEquals(expected, data) // assert parse successfully without exception
    }

}