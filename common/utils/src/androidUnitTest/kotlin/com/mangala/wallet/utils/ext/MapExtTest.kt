package com.mangala.wallet.utils.ext

import junit.framework.TestCase.assertEquals
import kotlin.test.Test

class MapExtTest {

    @Test
    fun `Given an empty map, when putOrAppend is called, then the map should contain the key and value`() {
        val map = mutableMapOf<String, List<String>>()
        val key = "key"
        val value = "value"

        map.putOrAppend(key, value)

        assertEquals(map[key], listOf(value))
    }

    @Test
    fun `Given map with existing key, when putOrAppend is called, then the map should contain the key and new value`() {
        val key = "key"
        val value = "value"
        val map = mutableMapOf(key to listOf("existingValue"))

        map.putOrAppend(key, value)

        assertEquals(map[key], listOf("existingValue", value))
    }
}