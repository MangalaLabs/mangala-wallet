package com.mangala.wallet.features.chains.evmcompatible.utils

import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.math.min

object Arrays {

    fun concatenate(a: ByteArray?, b: ByteArray?): ByteArray {
        if (a == null) {
            // b might also be null
            return b?.copyOf() ?: ByteArray(0)
        }
        if (b == null) {
            // a might also be null
            return a?.copyOf() ?: ByteArray(0)
        }

        val r = ByteArray(a.size + b.size)
        a.copyInto(r)
        b.copyInto(r, a.size)
        return r
    }

    fun copyOf(original: ByteArray, newLength: Int): ByteArray {
        val copy = ByteArray(newLength)
        original.copyInto(copy, 0, 0, min(original.size, newLength))
        return copy
    }

    fun asUnsignedByteArray(value: BigInteger): ByteArray {
        var bytes = value.toByteArray()

        if (bytes[0] == 0.toByte() && bytes.size != 1) {
            val tmp = ByteArray(bytes.size - 1)
            bytes.copyInto(tmp, 0, 1, bytes.size)
            bytes = tmp
        }

        return bytes
    }

    fun numberOfLeadingZeros(i: Int): Int {
        // HD, Figure 5-6
        return if (i == 0) {
            32
        } else {
            var n = 1
            if (i ushr 16 == 0) {
                n += 16
                i shl 16
            }
            if (i ushr 24 == 0) {
                n += 8
                i shl 8
            }
            if (i ushr 28 == 0) {
                n += 4
                i shl 4
            }
            if (i ushr 30 == 0) {
                n += 2
                i shl 2
            }
            n - (i ushr 31)
        }
    }

    fun clone(data: ByteArray?): ByteArray? {
        return data?.copyOf()
    }
}