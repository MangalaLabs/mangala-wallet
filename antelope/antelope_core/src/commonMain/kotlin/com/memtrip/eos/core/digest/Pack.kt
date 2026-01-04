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

import kotlin.jvm.JvmStatic

object Pack {
    @JvmStatic
    fun bigEndianToInt(bs: ByteArray, off: Int): Int {
        var off = off
        var n = bs[off].toInt() shl 24
        n = n or (bs[++off].toInt() and 0xff shl 16)
        n = n or (bs[++off].toInt() and 0xff shl 8)
        n = n or (bs[++off].toInt() and 0xff)
        return n
    }

    fun bigEndianToInt(bs: ByteArray, off: Int, ns: IntArray) {
        var off = off
        for (i in ns.indices) {
            ns[i] = bigEndianToInt(bs, off)
            off += 4
        }
    }

    fun intToBigEndian(n: Int): ByteArray {
        val bs = ByteArray(4)
        intToBigEndian(n, bs, 0)
        return bs
    }

    @JvmStatic
    fun intToBigEndian(n: Int, bs: ByteArray, off: Int) {
        var off = off
        bs[off] = (n ushr 24).toByte()
        bs[++off] = (n ushr 16).toByte()
        bs[++off] = (n ushr 8).toByte()
        bs[++off] = n.toByte()
    }

    fun intToBigEndian(ns: IntArray): ByteArray {
        val bs = ByteArray(4 * ns.size)
        intToBigEndian(ns, bs, 0)
        return bs
    }

    fun intToBigEndian(ns: IntArray, bs: ByteArray, off: Int) {
        var off = off
        for (i in ns.indices) {
            intToBigEndian(ns[i], bs, off)
            off += 4
        }
    }

    @JvmStatic
    fun bigEndianToLong(bs: ByteArray, off: Int): Long {
        val hi = bigEndianToInt(bs, off)
        val lo = bigEndianToInt(bs, off + 4)
        return (hi.toLong() and 0xffffffffL) shl 32 or (lo.toLong() and 0xffffffffL)
    }

    fun bigEndianToLong(bs: ByteArray, off: Int, ns: LongArray) {
        var off = off
        for (i in ns.indices) {
            ns[i] = bigEndianToLong(bs, off)
            off += 8
        }
    }

    fun longToBigEndian(n: Long): ByteArray {
        val bs = ByteArray(8)
        longToBigEndian(n, bs, 0)
        return bs
    }

    @JvmStatic
    fun longToBigEndian(n: Long, bs: ByteArray, off: Int) {
        intToBigEndian((n ushr 32).toInt(), bs, off)
        intToBigEndian((n and 0xffffffffL).toInt(), bs, off + 4)
    }

    fun longToBigEndian(ns: LongArray): ByteArray {
        val bs = ByteArray(8 * ns.size)
        longToBigEndian(ns, bs, 0)
        return bs
    }

    fun longToBigEndian(ns: LongArray, bs: ByteArray, off: Int) {
        var off = off
        for (i in ns.indices) {
            longToBigEndian(ns[i], bs, off)
            off += 8
        }
    }

    fun littleEndianToInt(bs: ByteArray, off: Int): Int {
        var off = off
        var n = bs[off].toInt() and 0xff
        n = n or (bs[++off].toInt() and 0xff shl 8)
        n = n or (bs[++off].toInt() and 0xff shl 16)
        n = n or (bs[++off].toInt() shl 24)
        return n
    }

    fun littleEndianToInt(bs: ByteArray, off: Int, ns: IntArray) {
        var off = off
        for (i in ns.indices) {
            ns[i] = littleEndianToInt(bs, off)
            off += 4
        }
    }

    fun littleEndianToInt(bs: ByteArray, bOff: Int, ns: IntArray, nOff: Int, count: Int) {
        var bOff = bOff
        for (i in 0 until count) {
            ns[nOff + i] = littleEndianToInt(bs, bOff)
            bOff += 4
        }
    }

    fun intToLittleEndian(n: Int): ByteArray {
        val bs = ByteArray(4)
        intToLittleEndian(n, bs, 0)
        return bs
    }

    fun intToLittleEndian(n: Int, bs: ByteArray, off: Int) {
        var off = off
        bs[off] = n.toByte()
        bs[++off] = (n ushr 8).toByte()
        bs[++off] = (n ushr 16).toByte()
        bs[++off] = (n ushr 24).toByte()
    }

    fun intToLittleEndian(ns: IntArray): ByteArray {
        val bs = ByteArray(4 * ns.size)
        intToLittleEndian(ns, bs, 0)
        return bs
    }

    fun intToLittleEndian(ns: IntArray, bs: ByteArray, off: Int) {
        var off = off
        for (i in ns.indices) {
            intToLittleEndian(ns[i], bs, off)
            off += 4
        }
    }

    fun littleEndianToLong(bs: ByteArray, off: Int): Long {
        val lo = littleEndianToInt(bs, off)
        val hi = littleEndianToInt(bs, off + 4)
        return (hi.toLong() and 0xffffffffL) shl 32 or (lo.toLong() and 0xffffffffL)
    }

    fun littleEndianToLong(bs: ByteArray, off: Int, ns: LongArray) {
        var off = off
        for (i in ns.indices) {
            ns[i] = littleEndianToLong(bs, off)
            off += 8
        }
    }

    fun longToLittleEndian(n: Long): ByteArray {
        val bs = ByteArray(8)
        longToLittleEndian(n, bs, 0)
        return bs
    }

    fun longToLittleEndian(n: Long, bs: ByteArray, off: Int) {
        intToLittleEndian((n and 0xffffffffL).toInt(), bs, off)
        intToLittleEndian((n ushr 32).toInt(), bs, off + 4)
    }

    fun longToLittleEndian(ns: LongArray): ByteArray {
        val bs = ByteArray(8 * ns.size)
        longToLittleEndian(ns, bs, 0)
        return bs
    }

    fun longToLittleEndian(ns: LongArray, bs: ByteArray, off: Int) {
        var off = off
        for (i in ns.indices) {
            longToLittleEndian(ns[i], bs, off)
            off += 8
        }
    }
}
