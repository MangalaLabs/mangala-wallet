/*
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mangala.antelope.base

//import com.mangala.antelope.base.serializer.ABISerializableObject
import kotlin.random.Random
import kotlin.reflect.KClass


fun arrayEquals(a: IntArray, b: IntArray): Boolean {
    val len = a.size
    if (len != b.size) {
        return false
    }
    for (i in 0 until len) {
        if (a[i] != b[i]) {
            return false
        }
    }
    return true
}

//fun arrayEquatableEquals(a: Array<ABISerializableObject>, b: Array<ABISerializableObject>): Boolean {
//    val len = a.size
//    if (len != b.size) {
//        return false
//    }
//    for (i in 0 until len) {
//        if (a[i] != b[i]) {
//            return false
//        }
//    }
//    return true
//}

val hexLookup: MutableMap<String, Int> = HashMap()

fun buildHexLookup() {
    hexLookup.clear()
    for (i in 0..0xff) {
        val b = i.toString(16).padStart(2, '0')
        hexLookup[b] = i
    }
}

fun arrayToHex(array: ByteArray): String {
    if (hexLookup.isEmpty()) {
        buildHexLookup()
    }
    return array.joinToString("") { byte ->
        val unsignedByte = byte.toInt() and 0xFF // Convert to unsigned byte
        hexLookup.keys.first { hexLookup[it] == unsignedByte }.padStart(2, '0')
    }
}


fun hexToArray(hex: String): IntArray {
    if (hexLookup.isEmpty()) {
        buildHexLookup()
    }
    if (hex.length % 2 != 0) {
        throw Error("Odd number of hex digits")
    }
    val len = hex.length / 2
    val result = IntArray(len)
    for (i in 0 until len) {
        val b = hexLookup[hex[i * 2].toString() + hex[i * 2 + 1].toString()]
            ?: throw Error("Expected hex string")
        result[i] = b
    }
    return result
}

fun secureRandom(length: Int): IntArray {
    val result = IntArray(length)
    for (i in 0 until length) {
        result[i] = Random.nextInt(256)
    }
    return result
}

var didWarn = false

fun <T : Any> isInstanceOf(obj: Any?, someClass: KClass<T>): Boolean {
    if (obj == null) {
        return false
    }
    return someClass.isInstance(obj)
}

