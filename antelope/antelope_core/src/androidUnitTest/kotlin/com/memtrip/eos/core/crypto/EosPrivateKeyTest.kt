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

import junit.framework.TestCase.assertEquals
import org.junit.Test

class EosPrivateKeyTest {

    @Test
    fun `Given a k1 private key, when calling fromString, then return a valid key 1`() {
        val input = "PVT_K1_22QMvpQFv4ne72Wu4Hrvqebt7jzGWtqVKb6vgxXt1bdjJnFRsg"

        val result = EosPrivateKey.fromString(input)

        assertEquals("5JqgwcGVLNdmu7QnwFjm1tPj6iZuUekvD8et9s1H7tTGtxxxWx6", result.toLegacyString())
        assertEquals(input, result.toString())
        assertEquals("PUB_K1_8gkgxwnq2dnQxTfEmAiHBAsLs8txGsbiAcgWpaDgKEdCSEeUXG", result.publicKey.toString())
        assertEquals("EOS8gkgxwnq2dnQxTfEmAiHBAsLs8txGsbiAcgWpaDgKEdCS6UmQ8", result.publicKey.toLegacyString())
    }

    @Test
    fun `Given a k1 private key, when calling fromString, then return a valid key 2`() {
        val input = "PVT_K1_aq8cvhTmywr1vEMTEycK5qt8k1yqifbjgPwrcfSLQLDLryCU7"

        val result = EosPrivateKey.fromString(input)

        assertEquals("5JQ7iJNnYEYw72JWPegshMpy6jZwByakKYkBB2fQgDFyNzjTRYK", result.toLegacyString())
        assertEquals(input, result.toString())
        assertEquals("PUB_K1_5LGeA7741BRFJhbZmhmjVHReqGeTX76vY71a62rkCZ5Uy2D8Ho", result.publicKey.toString())
        assertEquals("EOS5LGeA7741BRFJhbZmhmjVHReqGeTX76vY71a62rkCZ5UuAPLtP", result.publicKey.toLegacyString())
    }

    @Test
    fun `Given a k1 private key, when calling fromString, then return a valid key 3`() {
        val input = "PVT_K1_cwiA6pyuk5NuhUdZPXS8dgfwFEoapBoaL7c9WAN9Bs3f3rhr1"

        val result = EosPrivateKey.fromString(input)

        assertEquals("5JSEHqYv4NK4dv5kfkqRXBNotY5A1igGXPPtqKYuc23WDKZbXbu", result.toLegacyString())
        assertEquals(input, result.toString())
        assertEquals("PUB_K1_582RELvoy2LS2tpZLhdo2AAkBDPKwb9PMj7VD9vXjoSVAfS42q", result.publicKey.toString())
        assertEquals("EOS582RELvoy2LS2tpZLhdo2AAkBDPKwb9PMj7VD9vXjoSV4VTKR1", result.publicKey.toLegacyString())
    }

    @Test
    fun `Given a k1 private key, when calling fromString, then return a valid key 4`() {
        val input = "PVT_K1_2841ycoTmLTKEKx1U7uG7G7Ydyoc3JJ7GnwUw2spZNESVaYTi4"

        val result = EosPrivateKey.fromString(input)

        assertEquals("5JwLbf4tYDuSaEiE3fZoMA1EmEoipBANq5rii75d4SDsc4Sg3b5", result.toLegacyString())
        assertEquals(input, result.toString())
        assertEquals("PUB_K1_5gU1cgbrqXY452cHLxLKkuEf43gsyMv1atXTXBq8hLAsSrbTqT", result.publicKey.toString())
        assertEquals("EOS5gU1cgbrqXY452cHLxLKkuEf43gsyMv1atXTXBq8hLAsSBpava", result.publicKey.toLegacyString())
    }

    @Test
    fun `Given a k1 private key, when calling fromString, then return a valid key 5`() {
        val input = "PVT_K1_VKxBi3v7tgs6G9KDRdD3Nm2mYhV9oSVsXBHNnCXquwLL15z9P"

        val result = EosPrivateKey.fromString(input)

        assertEquals("5JJcXsA8zaTg86eRMQsXJ67tFNNchHfXDgaxWYpwmimaVzUs7nT", result.toLegacyString())
        assertEquals(input, result.toString())
        assertEquals("PUB_K1_7wsdMbpF5vThMy6ifYuX1qykG6XdciLaLK5NpachVoQpiEbbwD", result.publicKey.toString())
        assertEquals("EOS7wsdMbpF5vThMy6ifYuX1qykG6XdciLaLK5NpachVoQpnVTmun", result.publicKey.toLegacyString())
    }

    @Test
    fun `Given a k1 private key, when calling fromString, then return a valid key 6`() {
        val input = "PVT_K1_2qvUFqzT9KTM5Tpgxy19JJKy21VmkhGMyRT3dRFPR3nTCdJK5i"

        val result = EosPrivateKey.fromString(input)

        assertEquals("5KfD3wJ5XbtSc5r6jAQuEM3TBcqQytZM5nVEGoTzdHuRcpe2SRQ", result.toLegacyString())
        assertEquals(input, result.toString())
        assertEquals("PUB_K1_8iYsy6siFTjUEcraD5EWgCQHs8vdaFGtk1HncEsr2GaJfFUcJ3", result.publicKey.toString())
        assertEquals("EOS8iYsy6siFTjUEcraD5EWgCQHs8vdaFGtk1HncEsr2GaJkHaWCT", result.publicKey.toLegacyString())
    }

    @Test
    fun `Given a k1 private key, when calling fromString, then return a valid key 7`() {
        val input = "PVT_K1_oU2v9eTRwjFPZr7vbopEzoV7oKP3jKoXxvwUUy8ZG8xJdnHx9"

        val result = EosPrivateKey.fromString(input)

        assertEquals("5JckcbbjXtWiWPx8A83huHjvhidEbBbQXM2iAeXiNS7n7tgompQ", result.toLegacyString())
        assertEquals(input, result.toString())
        assertEquals("PUB_K1_8LFsc7v7DA99MtDmS2sDXhvfa4hkKGrUa32dYbFmvZkszHb2kA", result.publicKey.toString())
        assertEquals("EOS8LFsc7v7DA99MtDmS2sDXhvfa4hkKGrUa32dYbFmvZkswpzWCc", result.publicKey.toLegacyString())
    }

    @Test
    fun `Given a k1 private key, when calling fromString, then return a valid key 8`() {
        val input = "PVT_K1_e16WvFCURPxinjtKnpNsNs9hYudC6AqR51P22eKm9Mct4H7D5"

        val result = EosPrivateKey.fromString(input)

        assertEquals("5JTHgCNLGvzPDjB1vXEiTv7zNJNpqKxFZE8ncC5PZdzznPEj2un", result.toLegacyString())
        assertEquals(input, result.toString())
        assertEquals("PUB_K1_7dBhdwkapNStK37kGvgVyEUP58FYH6srgnYd7QV5K7vwkK4JVy", result.publicKey.toString())
        assertEquals("EOS7dBhdwkapNStK37kGvgVyEUP58FYH6srgnYd7QV5K7vwr5ZToe", result.publicKey.toLegacyString())
    }

    @Test
    fun `Given a k1 private key, when calling fromString, then return a valid key 9`() {
        val input = "PVT_K1_6XHWTx1hGCifFRfJxS9aGDtqctnPc8sG6T2EwhRrgVyLnHwha"

        val result = EosPrivateKey.fromString(input)

        assertEquals("5HuosBv369qByfdhhWQLEd1M7SSozXUDb5AEFQzSfjY98y6gGZ4", result.toLegacyString())
        assertEquals(input, result.toString())
        assertEquals("PUB_K1_5HbmnXBjEqba7pneUAnkydagxndYXujTYs8CaZaYkwjmpFB6Aw", result.publicKey.toString())
        assertEquals("EOS5HbmnXBjEqba7pneUAnkydagxndYXujTYs8CaZaYkwjmi2MRK5", result.publicKey.toLegacyString())
    }

    @Test
    fun `Given a k1 private key, when calling fromString, then return a valid key 10`() {
        val input = "PVT_K1_FAPHpKLg1KryC3KaTgnjNPupUsWxygPG3sogdNSxU33aJEDRY"

        val result = EosPrivateKey.fromString(input)

        assertEquals("5J4SxyGQR8aK7yaKMmuasn7X8RJnj6qm757f2rg7gqKgD9T6Dgv", result.toLegacyString())
        assertEquals(input, result.toString())
        assertEquals("PUB_K1_6RG4crTAamWEcoAheAq6u63h1tigndM7yaVNobewSJ4Y52AaaM", result.publicKey.toString())
        assertEquals("EOS6RG4crTAamWEcoAheAq6u63h1tigndM7yaVNobewSJ4XzpKepC", result.publicKey.toLegacyString())
    }

    @Test
    fun `Given a legacy private key, when calling fromString, then return a valid key 1`() {
        val input = "5JqgwcGVLNdmu7QnwFjm1tPj6iZuUekvD8et9s1H7tTGtxxxWx6"

        val result = EosPrivateKey.fromString(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy private key, when calling fromString, then return a valid key 2`() {
        val input = "5JQ7iJNnYEYw72JWPegshMpy6jZwByakKYkBB2fQgDFyNzjTRYK"

        val result = EosPrivateKey.fromString(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy private key, when calling fromString, then return a valid key 3`() {
        val input = "5JSEHqYv4NK4dv5kfkqRXBNotY5A1igGXPPtqKYuc23WDKZbXbu"

        val result = EosPrivateKey.fromString(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy private key, when calling fromString, then return a valid key 4`() {
        val input = "5JwLbf4tYDuSaEiE3fZoMA1EmEoipBANq5rii75d4SDsc4Sg3b5"

        val result = EosPrivateKey.fromString(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy private key, when calling fromString, then return a valid key 5`() {
        val input = "5JJcXsA8zaTg86eRMQsXJ67tFNNchHfXDgaxWYpwmimaVzUs7nT"

        val result = EosPrivateKey.fromString(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy private key, when calling fromString, then return a valid key 6`() {
        val input = "5KfD3wJ5XbtSc5r6jAQuEM3TBcqQytZM5nVEGoTzdHuRcpe2SRQ"

        val result = EosPrivateKey.fromString(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy private key, when calling fromString, then return a valid key 7`() {
        val input = "5JckcbbjXtWiWPx8A83huHjvhidEbBbQXM2iAeXiNS7n7tgompQ"

        val result = EosPrivateKey.fromString(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy private key, when calling fromString, then return a valid key 8`() {
        val input = "5JTHgCNLGvzPDjB1vXEiTv7zNJNpqKxFZE8ncC5PZdzznPEj2un"

        val result = EosPrivateKey.fromString(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy private key, when calling fromString, then return a valid key 9`() {
        val input = "5HuosBv369qByfdhhWQLEd1M7SSozXUDb5AEFQzSfjY98y6gGZ4"

        val result = EosPrivateKey.fromString(input)

        assertEquals(input, result.toString())
    }

    @Test
    fun `Given a legacy private key, when calling fromString, then return a valid key 10`() {
        val input = "5J4SxyGQR8aK7yaKMmuasn7X8RJnj6qm757f2rg7gqKgD9T6Dgv"

        val result = EosPrivateKey.fromString(input)

        assertEquals(input, result.toString())
    }
}