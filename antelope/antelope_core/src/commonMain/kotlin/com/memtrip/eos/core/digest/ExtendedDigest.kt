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

interface ExtendedDigest : Digest {
    /**
     * Return the size in bytes of the internal buffer the digest applies it's compression
     * function to.
     *
     * @return byte length of the digests internal buffer.
     */
    fun getByteLength(): Int
}
