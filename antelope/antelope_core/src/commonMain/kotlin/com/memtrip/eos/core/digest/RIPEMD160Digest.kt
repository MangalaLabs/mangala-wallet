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

class RIPEMD160Digest : GeneralDigest {
    private var H0 = 0
    private var H1 = 0
    private var H2 = 0
    private var H3 = 0
    private var H4 = 0 // IV's
    private val X = IntArray(16)
    private var xOff = 0

    /**
     * Standard constructor
     */
    constructor() {
        reset()
    }

    /**
     * Copy constructor.  This will copy the state of the provided
     * message digest.
     */
    constructor(t: RIPEMD160Digest) : super(t) {
        copyIn(t)
    }

    private fun copyIn(t: RIPEMD160Digest) {
        super.copyIn(t)
        H0 = t.H0
        H1 = t.H1
        H2 = t.H2
        H3 = t.H3
        H4 = t.H4
        t.X.copyInto(
            destination = X,
            destinationOffset = 0,
            startIndex = 0,
            endIndex = t.X.size
        )
        xOff = t.xOff
    }



    override fun processWord(
        input: ByteArray,
        inOff: Int
    ) {
        X[xOff++] = (input[inOff].toInt() and 0xff or (input[inOff + 1].toInt() and 0xff shl 8)
                or (input[inOff + 2].toInt() and 0xff shl 16) or (input[inOff + 3].toInt() and 0xff shl 24))
        if (xOff == 16) {
            processBlock()
        }
    }

    override fun processLength(
        bitLength: Long
    ) {
        if (xOff > 14) {
            processBlock()
        }
        X[14] = (bitLength and 0xffffffffL).toInt()
        X[15] = (bitLength ushr 32).toInt()
    }

    private fun unpackWord(
        word: Int,
        out: ByteArray,
        outOff: Int
    ) {
        out[outOff] = word.toByte()
        out[outOff + 1] = (word ushr 8).toByte()
        out[outOff + 2] = (word ushr 16).toByte()
        out[outOff + 3] = (word ushr 24).toByte()
    }

    override fun doFinal(
        out: ByteArray,
        outOff: Int
    ): Int {
        finish()
        unpackWord(H0, out, outOff)
        unpackWord(H1, out, outOff + 4)
        unpackWord(H2, out, outOff + 8)
        unpackWord(H3, out, outOff + 12)
        unpackWord(H4, out, outOff + 16)
        reset()
        return DIGEST_LENGTH
    }

    /**
     * reset the chaining variables to the IV values.
     */
    override fun reset() {
        super.reset()
        H0 = 0x67452301
        H1 = -0x10325477
        H2 = -0x67452302
        H3 = 0x10325476
        H4 = -0x3c2d1e10
        xOff = 0
        for (i in X.indices) {
            X[i] = 0
        }
    }

    /*
     * rotate int x left n bits.
     */
    private fun RL(
        x: Int,
        n: Int
    ): Int {
        return x shl n or (x ushr 32 - n)
    }

    /*
     * f1,f2,f3,f4,f5 are the basic RIPEMD160 functions.
     */
    /*
     * rounds 0-15
     */
    private fun f1(
        x: Int,
        y: Int,
        z: Int
    ): Int {
        return x xor y xor z
    }

    /*
     * rounds 16-31
     */
    private fun f2(
        x: Int,
        y: Int,
        z: Int
    ): Int {
        return x and y or (x.inv() and z)
    }

    /*
     * rounds 32-47
     */
    private fun f3(
        x: Int,
        y: Int,
        z: Int
    ): Int {
        return x or y.inv() xor z
    }

    /*
     * rounds 48-63
     */
    private fun f4(
        x: Int,
        y: Int,
        z: Int
    ): Int {
        return x and z or (y and z.inv())
    }

    /*
     * rounds 64-79
     */
    private fun f5(
        x: Int,
        y: Int,
        z: Int
    ): Int {
        return x xor (y or z.inv())
    }

    override fun processBlock() {
        var a: Int
        var aa: Int
        var b: Int
        var bb: Int
        var c: Int
        var cc: Int
        var d: Int
        var dd: Int
        var e: Int
        var ee: Int
        aa = H0
        a = aa
        bb = H1
        b = bb
        cc = H2
        c = cc
        dd = H3
        d = dd
        ee = H4
        e = ee

        //
        // Rounds 1 - 16
        //
        // left
        a = RL(a + f1(b, c, d) + X[0], 11) + e
        c = RL(c, 10)
        e = RL(e + f1(a, b, c) + X[1], 14) + d
        b = RL(b, 10)
        d = RL(d + f1(e, a, b) + X[2], 15) + c
        a = RL(a, 10)
        c = RL(c + f1(d, e, a) + X[3], 12) + b
        e = RL(e, 10)
        b = RL(b + f1(c, d, e) + X[4], 5) + a
        d = RL(d, 10)
        a = RL(a + f1(b, c, d) + X[5], 8) + e
        c = RL(c, 10)
        e = RL(e + f1(a, b, c) + X[6], 7) + d
        b = RL(b, 10)
        d = RL(d + f1(e, a, b) + X[7], 9) + c
        a = RL(a, 10)
        c = RL(c + f1(d, e, a) + X[8], 11) + b
        e = RL(e, 10)
        b = RL(b + f1(c, d, e) + X[9], 13) + a
        d = RL(d, 10)
        a = RL(a + f1(b, c, d) + X[10], 14) + e
        c = RL(c, 10)
        e = RL(e + f1(a, b, c) + X[11], 15) + d
        b = RL(b, 10)
        d = RL(d + f1(e, a, b) + X[12], 6) + c
        a = RL(a, 10)
        c = RL(c + f1(d, e, a) + X[13], 7) + b
        e = RL(e, 10)
        b = RL(b + f1(c, d, e) + X[14], 9) + a
        d = RL(d, 10)
        a = RL(a + f1(b, c, d) + X[15], 8) + e
        c = RL(c, 10)

        // right
        aa = RL(aa + f5(bb, cc, dd) + X[5] + 0x50a28be6, 8) + ee
        cc = RL(cc, 10)
        ee = RL(ee + f5(aa, bb, cc) + X[14] + 0x50a28be6, 9) + dd
        bb = RL(bb, 10)
        dd = RL(dd + f5(ee, aa, bb) + X[7] + 0x50a28be6, 9) + cc
        aa = RL(aa, 10)
        cc = RL(cc + f5(dd, ee, aa) + X[0] + 0x50a28be6, 11) + bb
        ee = RL(ee, 10)
        bb = RL(bb + f5(cc, dd, ee) + X[9] + 0x50a28be6, 13) + aa
        dd = RL(dd, 10)
        aa = RL(aa + f5(bb, cc, dd) + X[2] + 0x50a28be6, 15) + ee
        cc = RL(cc, 10)
        ee = RL(ee + f5(aa, bb, cc) + X[11] + 0x50a28be6, 15) + dd
        bb = RL(bb, 10)
        dd = RL(dd + f5(ee, aa, bb) + X[4] + 0x50a28be6, 5) + cc
        aa = RL(aa, 10)
        cc = RL(cc + f5(dd, ee, aa) + X[13] + 0x50a28be6, 7) + bb
        ee = RL(ee, 10)
        bb = RL(bb + f5(cc, dd, ee) + X[6] + 0x50a28be6, 7) + aa
        dd = RL(dd, 10)
        aa = RL(aa + f5(bb, cc, dd) + X[15] + 0x50a28be6, 8) + ee
        cc = RL(cc, 10)
        ee = RL(ee + f5(aa, bb, cc) + X[8] + 0x50a28be6, 11) + dd
        bb = RL(bb, 10)
        dd = RL(dd + f5(ee, aa, bb) + X[1] + 0x50a28be6, 14) + cc
        aa = RL(aa, 10)
        cc = RL(cc + f5(dd, ee, aa) + X[10] + 0x50a28be6, 14) + bb
        ee = RL(ee, 10)
        bb = RL(bb + f5(cc, dd, ee) + X[3] + 0x50a28be6, 12) + aa
        dd = RL(dd, 10)
        aa = RL(aa + f5(bb, cc, dd) + X[12] + 0x50a28be6, 6) + ee
        cc = RL(cc, 10)

        //
        // Rounds 16-31
        //
        // left
        e = RL(e + f2(a, b, c) + X[7] + 0x5a827999, 7) + d
        b = RL(b, 10)
        d = RL(d + f2(e, a, b) + X[4] + 0x5a827999, 6) + c
        a = RL(a, 10)
        c = RL(c + f2(d, e, a) + X[13] + 0x5a827999, 8) + b
        e = RL(e, 10)
        b = RL(b + f2(c, d, e) + X[1] + 0x5a827999, 13) + a
        d = RL(d, 10)
        a = RL(a + f2(b, c, d) + X[10] + 0x5a827999, 11) + e
        c = RL(c, 10)
        e = RL(e + f2(a, b, c) + X[6] + 0x5a827999, 9) + d
        b = RL(b, 10)
        d = RL(d + f2(e, a, b) + X[15] + 0x5a827999, 7) + c
        a = RL(a, 10)
        c = RL(c + f2(d, e, a) + X[3] + 0x5a827999, 15) + b
        e = RL(e, 10)
        b = RL(b + f2(c, d, e) + X[12] + 0x5a827999, 7) + a
        d = RL(d, 10)
        a = RL(a + f2(b, c, d) + X[0] + 0x5a827999, 12) + e
        c = RL(c, 10)
        e = RL(e + f2(a, b, c) + X[9] + 0x5a827999, 15) + d
        b = RL(b, 10)
        d = RL(d + f2(e, a, b) + X[5] + 0x5a827999, 9) + c
        a = RL(a, 10)
        c = RL(c + f2(d, e, a) + X[2] + 0x5a827999, 11) + b
        e = RL(e, 10)
        b = RL(b + f2(c, d, e) + X[14] + 0x5a827999, 7) + a
        d = RL(d, 10)
        a = RL(a + f2(b, c, d) + X[11] + 0x5a827999, 13) + e
        c = RL(c, 10)
        e = RL(e + f2(a, b, c) + X[8] + 0x5a827999, 12) + d
        b = RL(b, 10)

        // right
        ee = RL(ee + f4(aa, bb, cc) + X[6] + 0x5c4dd124, 9) + dd
        bb = RL(bb, 10)
        dd = RL(dd + f4(ee, aa, bb) + X[11] + 0x5c4dd124, 13) + cc
        aa = RL(aa, 10)
        cc = RL(cc + f4(dd, ee, aa) + X[3] + 0x5c4dd124, 15) + bb
        ee = RL(ee, 10)
        bb = RL(bb + f4(cc, dd, ee) + X[7] + 0x5c4dd124, 7) + aa
        dd = RL(dd, 10)
        aa = RL(aa + f4(bb, cc, dd) + X[0] + 0x5c4dd124, 12) + ee
        cc = RL(cc, 10)
        ee = RL(ee + f4(aa, bb, cc) + X[13] + 0x5c4dd124, 8) + dd
        bb = RL(bb, 10)
        dd = RL(dd + f4(ee, aa, bb) + X[5] + 0x5c4dd124, 9) + cc
        aa = RL(aa, 10)
        cc = RL(cc + f4(dd, ee, aa) + X[10] + 0x5c4dd124, 11) + bb
        ee = RL(ee, 10)
        bb = RL(bb + f4(cc, dd, ee) + X[14] + 0x5c4dd124, 7) + aa
        dd = RL(dd, 10)
        aa = RL(aa + f4(bb, cc, dd) + X[15] + 0x5c4dd124, 7) + ee
        cc = RL(cc, 10)
        ee = RL(ee + f4(aa, bb, cc) + X[8] + 0x5c4dd124, 12) + dd
        bb = RL(bb, 10)
        dd = RL(dd + f4(ee, aa, bb) + X[12] + 0x5c4dd124, 7) + cc
        aa = RL(aa, 10)
        cc = RL(cc + f4(dd, ee, aa) + X[4] + 0x5c4dd124, 6) + bb
        ee = RL(ee, 10)
        bb = RL(bb + f4(cc, dd, ee) + X[9] + 0x5c4dd124, 15) + aa
        dd = RL(dd, 10)
        aa = RL(aa + f4(bb, cc, dd) + X[1] + 0x5c4dd124, 13) + ee
        cc = RL(cc, 10)
        ee = RL(ee + f4(aa, bb, cc) + X[2] + 0x5c4dd124, 11) + dd
        bb = RL(bb, 10)

        //
        // Rounds 32-47
        //
        // left
        d = RL(d + f3(e, a, b) + X[3] + 0x6ed9eba1, 11) + c
        a = RL(a, 10)
        c = RL(c + f3(d, e, a) + X[10] + 0x6ed9eba1, 13) + b
        e = RL(e, 10)
        b = RL(b + f3(c, d, e) + X[14] + 0x6ed9eba1, 6) + a
        d = RL(d, 10)
        a = RL(a + f3(b, c, d) + X[4] + 0x6ed9eba1, 7) + e
        c = RL(c, 10)
        e = RL(e + f3(a, b, c) + X[9] + 0x6ed9eba1, 14) + d
        b = RL(b, 10)
        d = RL(d + f3(e, a, b) + X[15] + 0x6ed9eba1, 9) + c
        a = RL(a, 10)
        c = RL(c + f3(d, e, a) + X[8] + 0x6ed9eba1, 13) + b
        e = RL(e, 10)
        b = RL(b + f3(c, d, e) + X[1] + 0x6ed9eba1, 15) + a
        d = RL(d, 10)
        a = RL(a + f3(b, c, d) + X[2] + 0x6ed9eba1, 14) + e
        c = RL(c, 10)
        e = RL(e + f3(a, b, c) + X[7] + 0x6ed9eba1, 8) + d
        b = RL(b, 10)
        d = RL(d + f3(e, a, b) + X[0] + 0x6ed9eba1, 13) + c
        a = RL(a, 10)
        c = RL(c + f3(d, e, a) + X[6] + 0x6ed9eba1, 6) + b
        e = RL(e, 10)
        b = RL(b + f3(c, d, e) + X[13] + 0x6ed9eba1, 5) + a
        d = RL(d, 10)
        a = RL(a + f3(b, c, d) + X[11] + 0x6ed9eba1, 12) + e
        c = RL(c, 10)
        e = RL(e + f3(a, b, c) + X[5] + 0x6ed9eba1, 7) + d
        b = RL(b, 10)
        d = RL(d + f3(e, a, b) + X[12] + 0x6ed9eba1, 5) + c
        a = RL(a, 10)

        // right
        dd = RL(dd + f3(ee, aa, bb) + X[15] + 0x6d703ef3, 9) + cc
        aa = RL(aa, 10)
        cc = RL(cc + f3(dd, ee, aa) + X[5] + 0x6d703ef3, 7) + bb
        ee = RL(ee, 10)
        bb = RL(bb + f3(cc, dd, ee) + X[1] + 0x6d703ef3, 15) + aa
        dd = RL(dd, 10)
        aa = RL(aa + f3(bb, cc, dd) + X[3] + 0x6d703ef3, 11) + ee
        cc = RL(cc, 10)
        ee = RL(ee + f3(aa, bb, cc) + X[7] + 0x6d703ef3, 8) + dd
        bb = RL(bb, 10)
        dd = RL(dd + f3(ee, aa, bb) + X[14] + 0x6d703ef3, 6) + cc
        aa = RL(aa, 10)
        cc = RL(cc + f3(dd, ee, aa) + X[6] + 0x6d703ef3, 6) + bb
        ee = RL(ee, 10)
        bb = RL(bb + f3(cc, dd, ee) + X[9] + 0x6d703ef3, 14) + aa
        dd = RL(dd, 10)
        aa = RL(aa + f3(bb, cc, dd) + X[11] + 0x6d703ef3, 12) + ee
        cc = RL(cc, 10)
        ee = RL(ee + f3(aa, bb, cc) + X[8] + 0x6d703ef3, 13) + dd
        bb = RL(bb, 10)
        dd = RL(dd + f3(ee, aa, bb) + X[12] + 0x6d703ef3, 5) + cc
        aa = RL(aa, 10)
        cc = RL(cc + f3(dd, ee, aa) + X[2] + 0x6d703ef3, 14) + bb
        ee = RL(ee, 10)
        bb = RL(bb + f3(cc, dd, ee) + X[10] + 0x6d703ef3, 13) + aa
        dd = RL(dd, 10)
        aa = RL(aa + f3(bb, cc, dd) + X[0] + 0x6d703ef3, 13) + ee
        cc = RL(cc, 10)
        ee = RL(ee + f3(aa, bb, cc) + X[4] + 0x6d703ef3, 7) + dd
        bb = RL(bb, 10)
        dd = RL(dd + f3(ee, aa, bb) + X[13] + 0x6d703ef3, 5) + cc
        aa = RL(aa, 10)

        //
        // Rounds 48-63
        //
        // left
        c = RL(c + f4(d, e, a) + X[1] + -0x70e44324, 11) + b
        e = RL(e, 10)
        b = RL(b + f4(c, d, e) + X[9] + -0x70e44324, 12) + a
        d = RL(d, 10)
        a = RL(a + f4(b, c, d) + X[11] + -0x70e44324, 14) + e
        c = RL(c, 10)
        e = RL(e + f4(a, b, c) + X[10] + -0x70e44324, 15) + d
        b = RL(b, 10)
        d = RL(d + f4(e, a, b) + X[0] + -0x70e44324, 14) + c
        a = RL(a, 10)
        c = RL(c + f4(d, e, a) + X[8] + -0x70e44324, 15) + b
        e = RL(e, 10)
        b = RL(b + f4(c, d, e) + X[12] + -0x70e44324, 9) + a
        d = RL(d, 10)
        a = RL(a + f4(b, c, d) + X[4] + -0x70e44324, 8) + e
        c = RL(c, 10)
        e = RL(e + f4(a, b, c) + X[13] + -0x70e44324, 9) + d
        b = RL(b, 10)
        d = RL(d + f4(e, a, b) + X[3] + -0x70e44324, 14) + c
        a = RL(a, 10)
        c = RL(c + f4(d, e, a) + X[7] + -0x70e44324, 5) + b
        e = RL(e, 10)
        b = RL(b + f4(c, d, e) + X[15] + -0x70e44324, 6) + a
        d = RL(d, 10)
        a = RL(a + f4(b, c, d) + X[14] + -0x70e44324, 8) + e
        c = RL(c, 10)
        e = RL(e + f4(a, b, c) + X[5] + -0x70e44324, 6) + d
        b = RL(b, 10)
        d = RL(d + f4(e, a, b) + X[6] + -0x70e44324, 5) + c
        a = RL(a, 10)
        c = RL(c + f4(d, e, a) + X[2] + -0x70e44324, 12) + b
        e = RL(e, 10)

        // right
        cc = RL(cc + f2(dd, ee, aa) + X[8] + 0x7a6d76e9, 15) + bb
        ee = RL(ee, 10)
        bb = RL(bb + f2(cc, dd, ee) + X[6] + 0x7a6d76e9, 5) + aa
        dd = RL(dd, 10)
        aa = RL(aa + f2(bb, cc, dd) + X[4] + 0x7a6d76e9, 8) + ee
        cc = RL(cc, 10)
        ee = RL(ee + f2(aa, bb, cc) + X[1] + 0x7a6d76e9, 11) + dd
        bb = RL(bb, 10)
        dd = RL(dd + f2(ee, aa, bb) + X[3] + 0x7a6d76e9, 14) + cc
        aa = RL(aa, 10)
        cc = RL(cc + f2(dd, ee, aa) + X[11] + 0x7a6d76e9, 14) + bb
        ee = RL(ee, 10)
        bb = RL(bb + f2(cc, dd, ee) + X[15] + 0x7a6d76e9, 6) + aa
        dd = RL(dd, 10)
        aa = RL(aa + f2(bb, cc, dd) + X[0] + 0x7a6d76e9, 14) + ee
        cc = RL(cc, 10)
        ee = RL(ee + f2(aa, bb, cc) + X[5] + 0x7a6d76e9, 6) + dd
        bb = RL(bb, 10)
        dd = RL(dd + f2(ee, aa, bb) + X[12] + 0x7a6d76e9, 9) + cc
        aa = RL(aa, 10)
        cc = RL(cc + f2(dd, ee, aa) + X[2] + 0x7a6d76e9, 12) + bb
        ee = RL(ee, 10)
        bb = RL(bb + f2(cc, dd, ee) + X[13] + 0x7a6d76e9, 9) + aa
        dd = RL(dd, 10)
        aa = RL(aa + f2(bb, cc, dd) + X[9] + 0x7a6d76e9, 12) + ee
        cc = RL(cc, 10)
        ee = RL(ee + f2(aa, bb, cc) + X[7] + 0x7a6d76e9, 5) + dd
        bb = RL(bb, 10)
        dd = RL(dd + f2(ee, aa, bb) + X[10] + 0x7a6d76e9, 15) + cc
        aa = RL(aa, 10)
        cc = RL(cc + f2(dd, ee, aa) + X[14] + 0x7a6d76e9, 8) + bb
        ee = RL(ee, 10)

        //
        // Rounds 64-79
        //
        // left
        b = RL(b + f5(c, d, e) + X[4] + -0x56ac02b2, 9) + a
        d = RL(d, 10)
        a = RL(a + f5(b, c, d) + X[0] + -0x56ac02b2, 15) + e
        c = RL(c, 10)
        e = RL(e + f5(a, b, c) + X[5] + -0x56ac02b2, 5) + d
        b = RL(b, 10)
        d = RL(d + f5(e, a, b) + X[9] + -0x56ac02b2, 11) + c
        a = RL(a, 10)
        c = RL(c + f5(d, e, a) + X[7] + -0x56ac02b2, 6) + b
        e = RL(e, 10)
        b = RL(b + f5(c, d, e) + X[12] + -0x56ac02b2, 8) + a
        d = RL(d, 10)
        a = RL(a + f5(b, c, d) + X[2] + -0x56ac02b2, 13) + e
        c = RL(c, 10)
        e = RL(e + f5(a, b, c) + X[10] + -0x56ac02b2, 12) + d
        b = RL(b, 10)
        d = RL(d + f5(e, a, b) + X[14] + -0x56ac02b2, 5) + c
        a = RL(a, 10)
        c = RL(c + f5(d, e, a) + X[1] + -0x56ac02b2, 12) + b
        e = RL(e, 10)
        b = RL(b + f5(c, d, e) + X[3] + -0x56ac02b2, 13) + a
        d = RL(d, 10)
        a = RL(a + f5(b, c, d) + X[8] + -0x56ac02b2, 14) + e
        c = RL(c, 10)
        e = RL(e + f5(a, b, c) + X[11] + -0x56ac02b2, 11) + d
        b = RL(b, 10)
        d = RL(d + f5(e, a, b) + X[6] + -0x56ac02b2, 8) + c
        a = RL(a, 10)
        c = RL(c + f5(d, e, a) + X[15] + -0x56ac02b2, 5) + b
        e = RL(e, 10)
        b = RL(b + f5(c, d, e) + X[13] + -0x56ac02b2, 6) + a
        d = RL(d, 10)

        // right
        bb = RL(bb + f1(cc, dd, ee) + X[12], 8) + aa
        dd = RL(dd, 10)
        aa = RL(aa + f1(bb, cc, dd) + X[15], 5) + ee
        cc = RL(cc, 10)
        ee = RL(ee + f1(aa, bb, cc) + X[10], 12) + dd
        bb = RL(bb, 10)
        dd = RL(dd + f1(ee, aa, bb) + X[4], 9) + cc
        aa = RL(aa, 10)
        cc = RL(cc + f1(dd, ee, aa) + X[1], 12) + bb
        ee = RL(ee, 10)
        bb = RL(bb + f1(cc, dd, ee) + X[5], 5) + aa
        dd = RL(dd, 10)
        aa = RL(aa + f1(bb, cc, dd) + X[8], 14) + ee
        cc = RL(cc, 10)
        ee = RL(ee + f1(aa, bb, cc) + X[7], 6) + dd
        bb = RL(bb, 10)
        dd = RL(dd + f1(ee, aa, bb) + X[6], 8) + cc
        aa = RL(aa, 10)
        cc = RL(cc + f1(dd, ee, aa) + X[2], 13) + bb
        ee = RL(ee, 10)
        bb = RL(bb + f1(cc, dd, ee) + X[13], 6) + aa
        dd = RL(dd, 10)
        aa = RL(aa + f1(bb, cc, dd) + X[14], 5) + ee
        cc = RL(cc, 10)
        ee = RL(ee + f1(aa, bb, cc) + X[0], 15) + dd
        bb = RL(bb, 10)
        dd = RL(dd + f1(ee, aa, bb) + X[3], 13) + cc
        aa = RL(aa, 10)
        cc = RL(cc + f1(dd, ee, aa) + X[9], 11) + bb
        ee = RL(ee, 10)
        bb = RL(bb + f1(cc, dd, ee) + X[11], 11) + aa
        dd = RL(dd, 10)
        dd += c + H1
        H1 = H2 + d + ee
        H2 = H3 + e + aa
        H3 = H4 + a + bb
        H4 = H0 + b + cc
        H0 = dd

        //
        // reset the offset and clean out the word buffer.
        //
        xOff = 0
        for (i in X.indices) {
            X[i] = 0
        }
    }

    override val algorithmName: String
        get() = "RIPEMD160"
    override val digestSize: Int
        get() = DIGEST_LENGTH

    override fun copy(): Memoable {
        return RIPEMD160Digest(this)
    }

    override fun reset(other: Memoable) {
        val d = other as RIPEMD160Digest
        copyIn(d)
    }

    companion object {
        private const val DIGEST_LENGTH = 20
    }
}
