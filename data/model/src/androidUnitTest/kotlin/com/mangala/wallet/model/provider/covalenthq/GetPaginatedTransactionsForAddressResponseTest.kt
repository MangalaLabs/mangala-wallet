package com.mangala.wallet.model.provider.covalenthq

import com.mangala.wallet.common.test.utils.SharedFileReader
import junit.framework.TestCase.assertTrue
import kotlinx.serialization.json.Json
import org.junit.Test

class GetPaginatedTransactionsForAddressResponseTest {

    @Test
    fun testParse() {
        val data: GetPaginatedCovalenthqTransactionsForAddressResponse =
            Json.decodeFromString(SharedFileReader().loadJsonFile("GetPaginatedTransactionsForAddress.json")!!)

        assertTrue(true) // assert parse successfully without exception
    }

    @Test
    fun testParse2() {
        val data: GetPaginatedCovalenthqTransactionsForAddressResponse =
            Json.decodeFromString(SharedFileReader().loadJsonFile("GetPaginatedTransactionsForAddress2.json")!!)

        assertTrue(true) // assert parse successfully without exception
    }
}