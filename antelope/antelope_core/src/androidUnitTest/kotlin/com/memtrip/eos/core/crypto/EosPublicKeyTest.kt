/*
 * Copyright 2013-present memtrip LTD.
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

// ------------------------------------------------------------------
// MODIFICATION NOTICE:
// Modified by Mangala Wallet
// Description: Adapted for Kotlin Multiplatform compatibility.
// ------------------------------------------------------------------

package com.memtrip.eos.core.crypto

import org.junit.Assert.assertEquals
import org.junit.Test

class EosPublicKeyTest {

    @Test
    fun `Given a k1 public key, when calling constructor that takes in string, then return a valid key 1`() {
        val input = "PUB_K1_8gkgxwnq2dnQxTfEmAiHBAsLs8txGsbiAcgWpaDgKEdCSEeUXG"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
        assertEquals("EOS8gkgxwnq2dnQxTfEmAiHBAsLs8txGsbiAcgWpaDgKEdCS6UmQ8", result.toLegacyString())
    }

    @Test
    fun `Given a k1 public key, when calling constructor that takes in string, then return a valid key 2`() {
        val input = "PUB_K1_5LGeA7741BRFJhbZmhmjVHReqGeTX76vY71a62rkCZ5Uy2D8Ho"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
        assertEquals("EOS5LGeA7741BRFJhbZmhmjVHReqGeTX76vY71a62rkCZ5UuAPLtP", result.toLegacyString())
    }

    @Test
    fun `Given a k1 public key, when calling constructor that takes in string, then return a valid key 3`() {
        val input = "PUB_K1_582RELvoy2LS2tpZLhdo2AAkBDPKwb9PMj7VD9vXjoSVAfS42q"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
        assertEquals("EOS582RELvoy2LS2tpZLhdo2AAkBDPKwb9PMj7VD9vXjoSV4VTKR1", result.toLegacyString())
    }

    @Test
    fun `Given a k1 public key, when calling constructor that takes in string, then return a valid key 4`() {
        val input = "PUB_K1_5gU1cgbrqXY452cHLxLKkuEf43gsyMv1atXTXBq8hLAsSrbTqT"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
        assertEquals("EOS5gU1cgbrqXY452cHLxLKkuEf43gsyMv1atXTXBq8hLAsSBpava", result.toLegacyString())
    }

    @Test
    fun `Given a k1 public key, when calling constructor that takes in string, then return a valid key 5`() {
        val input = "PUB_K1_7wsdMbpF5vThMy6ifYuX1qykG6XdciLaLK5NpachVoQpiEbbwD"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
        assertEquals("EOS7wsdMbpF5vThMy6ifYuX1qykG6XdciLaLK5NpachVoQpnVTmun", result.toLegacyString())
    }

    @Test
    fun `Given a k1 public key, when calling constructor that takes in string, then return a valid key 6`() {
        val input = "PUB_K1_8iYsy6siFTjUEcraD5EWgCQHs8vdaFGtk1HncEsr2GaJfFUcJ3"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
        assertEquals("EOS8iYsy6siFTjUEcraD5EWgCQHs8vdaFGtk1HncEsr2GaJkHaWCT", result.toLegacyString())
    }

    @Test
    fun `Given a k1 public key, when calling constructor that takes in string, then return a valid key 7`() {
        val input = "PUB_K1_8LFsc7v7DA99MtDmS2sDXhvfa4hkKGrUa32dYbFmvZkszHb2kA"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
        assertEquals("EOS8LFsc7v7DA99MtDmS2sDXhvfa4hkKGrUa32dYbFmvZkswpzWCc", result.toLegacyString())
    }

    @Test
    fun `Given a k1 public key, when calling constructor that takes in string, then return a valid key 8`() {
        val input = "PUB_K1_7dBhdwkapNStK37kGvgVyEUP58FYH6srgnYd7QV5K7vwkK4JVy"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
        assertEquals("EOS7dBhdwkapNStK37kGvgVyEUP58FYH6srgnYd7QV5K7vwr5ZToe", result.toLegacyString())
    }

    @Test
    fun `Given a k1 public key, when calling constructor that takes in string, then return a valid key 9`() {
        val input = "PUB_K1_5HbmnXBjEqba7pneUAnkydagxndYXujTYs8CaZaYkwjmpFB6Aw"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
        assertEquals("EOS5HbmnXBjEqba7pneUAnkydagxndYXujTYs8CaZaYkwjmi2MRK5", result.toLegacyString())
    }

    @Test
    fun `Given a k1 public key, when calling constructor that takes in string, then return a valid key 10`() {
        val input = "PUB_K1_6RG4crTAamWEcoAheAq6u63h1tigndM7yaVNobewSJ4Y52AaaM"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
        assertEquals("EOS6RG4crTAamWEcoAheAq6u63h1tigndM7yaVNobewSJ4XzpKepC", result.toLegacyString())
    }

    @Test
    fun `Given a legacy public key, when calling constructor that takes in string, then return a valid key 1`() {
        val input = "EOS8gkgxwnq2dnQxTfEmAiHBAsLs8txGsbiAcgWpaDgKEdCS6UmQ8"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy public key, when calling constructor that takes in string, then return a valid key 2`() {
        val input = "EOS5LGeA7741BRFJhbZmhmjVHReqGeTX76vY71a62rkCZ5UuAPLtP"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy public key, when calling constructor that takes in string, then return a valid key 3`() {
        val input = "EOS582RELvoy2LS2tpZLhdo2AAkBDPKwb9PMj7VD9vXjoSV4VTKR1"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy public key, when calling constructor that takes in string, then return a valid key 4`() {
        val input = "EOS5gU1cgbrqXY452cHLxLKkuEf43gsyMv1atXTXBq8hLAsSBpava"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy public key, when calling constructor that takes in string, then return a valid key 5`() {
        val input = "EOS7wsdMbpF5vThMy6ifYuX1qykG6XdciLaLK5NpachVoQpnVTmun"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy public key, when calling constructor that takes in string, then return a valid key 6`() {
        val input = "EOS8iYsy6siFTjUEcraD5EWgCQHs8vdaFGtk1HncEsr2GaJkHaWCT"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy public key, when calling constructor that takes in string, then return a valid key 7`() {
        val input = "EOS8LFsc7v7DA99MtDmS2sDXhvfa4hkKGrUa32dYbFmvZkswpzWCc"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy public key, when calling constructor that takes in string, then return a valid key 8`() {
        val input = "EOS7dBhdwkapNStK37kGvgVyEUP58FYH6srgnYd7QV5K7vwr5ZToe"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy public key, when calling constructor that takes in string, then return a valid key 9`() {
        val input = "EOS5HbmnXBjEqba7pneUAnkydagxndYXujTYs8CaZaYkwjmi2MRK5"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy public key, when calling constructor that takes in string, then return a valid key 10`() {
        val input = "EOS6RG4crTAamWEcoAheAq6u63h1tigndM7yaVNobewSJ4XzpKepC"

        val result = EosPublicKey(input)

        assertEquals(input, result.toString())
    }
}