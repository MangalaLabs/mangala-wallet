/*
 * Copyright (c) 2000-2021 The Legion of the Bouncy Castle Inc.
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the MIT License.
 * See https://www.bouncycastle.org/licence.html for license details.
 */

// ------------------------------------------------------------------
// MODIFICATION NOTICE:
// Modified by Mangala Wallet
// Description: Adapted from BouncyCastle for Kotlin Multiplatform compatibility.
// ------------------------------------------------------------------
package com.memtrip.eos.core.digest

interface Digest {
    /**
     * return the algorithm name
     *
     * @return the algorithm name
     */
    val algorithmName: String

    /**
     * return the size, in bytes, of the digest produced by this message digest.
     *
     * @return the size, in bytes, of the digest produced by this message digest.
     */
    val digestSize: Int

    /**
     * update the message digest with a single byte.
     *
     * @param in the input byte to be entered.
     */
    fun update(input: Byte)

    /**
     * update the message digest with a block of bytes.
     *
     * @param in the byte array containing the data.
     * @param inOff the offset into the byte array where the data starts.
     * @param len the length of the data.
     */
    fun update(input: ByteArray, inOff: Int, len: Int)

    /**
     * close the digest, producing the final digest value. The doFinal
     * call leaves the digest reset.
     *
     * @param out the array the digest is to be copied into.
     * @param outOff the offset into the out array the digest is to start at.
     */
    fun doFinal(out: ByteArray, outOff: Int): Int

    /**
     * reset the digest back to it's initial state.
     */
    fun reset()
}
