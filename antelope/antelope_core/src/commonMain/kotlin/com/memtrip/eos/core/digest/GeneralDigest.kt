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

import com.memtrip.eos.core.digest.Pack.bigEndianToInt
import com.memtrip.eos.core.digest.Pack.bigEndianToLong
import com.memtrip.eos.core.digest.Pack.intToBigEndian
import com.memtrip.eos.core.digest.Pack.longToBigEndian

abstract class GeneralDigest : ExtendedDigest, Memoable {
    private val xBuf = ByteArray(4)
    private var xBufOff = 0
    private var byteCount: Long = 0

    /**
     * Standard constructor
     */
    protected constructor() {
        xBufOff = 0
    }

    /**
     * Copy constructor.  We are using copy constructors in place
     * of the Object.clone() interface as this interface is not
     * supported by J2ME.
     */
    protected constructor(t: GeneralDigest) {
        copyIn(t)
    }

    protected constructor(encodedState: ByteArray) {
        encodedState.copyInto(
            destination = xBuf,
            destinationOffset = 0,
            startIndex = 0,
            endIndex = xBuf.size
        )
        xBufOff = bigEndianToInt(encodedState, 4)
        byteCount = bigEndianToLong(encodedState, 8)
    }

    protected fun copyIn(t: GeneralDigest) {
        t.xBuf.copyInto(
            destination = xBuf,
            destinationOffset = 0,
            startIndex = 0,
            endIndex = t.xBuf.size
        )
        xBufOff = t.xBufOff
        byteCount = t.byteCount
    }

    override fun update(
        input: Byte
    ) {
        xBuf[xBufOff++] = input
        if (xBufOff == xBuf.size) {
            processWord(xBuf, 0)
            xBufOff = 0
        }
        byteCount++
    }

    override fun update(
        input: ByteArray,
        inOff: Int,
        len: Int
    ) {
        //
        // fill the current word
        //
        var inOff = inOff
        var len = len
        while (xBufOff != 0 && len > 0) {
            update(input[inOff])
            inOff++
            len--
        }

        //
        // process whole words.
        //
        while (len > xBuf.size) {
            processWord(input, inOff)
            inOff += xBuf.size
            len -= xBuf.size
            byteCount += xBuf.size.toLong()
        }

        //
        // load in the remainder.
        //
        while (len > 0) {
            update(input[inOff])
            inOff++
            len--
        }
    }

    fun finish() {
        val bitLength = byteCount shl 3

        //
        // add the pad bytes.
        //
        update(128.toByte())
        while (xBufOff != 0) {
            update(0.toByte())
        }
        processLength(bitLength)
        processBlock()
    }

    override fun reset() {
        byteCount = 0
        xBufOff = 0
        for (i in xBuf.indices) {
            xBuf[i] = 0
        }
    }

    protected fun populateState(state: ByteArray) {
        xBuf.copyInto(
            destination = state,
            destinationOffset = 0,
            startIndex = 0,
            endIndex = xBufOff
        )
        intToBigEndian(xBufOff, state, 4)
        longToBigEndian(byteCount, state, 8)
    }

    override fun getByteLength(): Int {
        return BYTE_LENGTH
    }

    protected abstract fun processWord(`in`: ByteArray, inOff: Int)
    protected abstract fun processLength(bitLength: Long)
    protected abstract fun processBlock()

    companion object {
        private const val BYTE_LENGTH = 64
    }
}