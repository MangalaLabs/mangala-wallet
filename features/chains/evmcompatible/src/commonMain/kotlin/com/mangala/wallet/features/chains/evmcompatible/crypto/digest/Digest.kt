package com.mangala.wallet.features.chains.evmcompatible.crypto.digest

interface Digest {

    /**
     * Insert one more input data byte.
     *
     * @param input   the input byte
     */
    fun update(input: Byte)

    /**
     * Insert some more bytes.
     *
     * @param inbuf   the data bytes
     */
    fun update(inbuf: ByteArray)

    /**
     * Insert some more bytes.
     *
     * @param inbuf   the data buffer
     * @param off     the data offset in `inbuf`
     * @param len     the data length (in bytes)
     */
    fun update(inbuf: ByteArray, off: Int, len: Int)

    /**
     * Finalize the current hash computation and return the hash value
     * in a newly-allocated array. The object is resetted.
     *
     * @return  the hash output
     */
    fun digest(): ByteArray

    /**
     * Input some bytes, then finalize the current hash computation
     * and return the hash value in a newly-allocated array. The object
     * is resetted.
     *
     * @param inbuf   the input data
     * @return  the hash output
     */
    fun digest(inbuf: ByteArray?): ByteArray

    /**
     * Finalize the current hash computation and store the hash value
     * in the provided output buffer. The `len` parameter
     * contains the maximum number of bytes that should be written;
     * no more bytes than the natural hash function output length will
     * be produced. If `len` is smaller than the natural
     * hash output length, the hash output is truncated to its first
     * `len` bytes. The object is resetted.
     *
     * @param outbuf   the output buffer
     * @param off      the output offset within `outbuf`
     * @param len      the requested hash output length (in bytes)
     * @return  the number of bytes actually written in `outbuf`
     */
    fun digest(outbuf: ByteArray?, off: Int, len: Int): Int

    /**
     * Get the natural hash function output length (in bytes).
     *
     * @return  the digest output length (in bytes)
     */
    fun getDigestLength(): Int

    /**
     * Reset the object: this makes it suitable for a new hash
     * computation. The current computation, if any, is discarded.
     */
    fun reset()

    /**
     * Clone the current state. The returned object evolves independantly
     * of this object.
     *
     * @return  the clone
     */
    fun copy(): Digest

    /**
     *
     * Return the "block length" for the hash function. This
     * value is naturally defined for iterated hash functions
     * (Merkle-Damgard). It is used in HMAC (that's what the
     * [HMAC specification](http://tools.ietf.org/html/rfc2104)
     * names the "`B`" parameter).
     *
     *
     * If the function is "block-less" then this function may
     * return `-n` where `n` is an integer such that the
     * block length for HMAC ("`B`") will be inferred from the
     * key length, by selecting the smallest multiple of `n`
     * which is no smaller than the key length. For instance, for
     * the Fugue-xxx hash functions, this function returns -4: the
     * virtual block length B is the HMAC key length, rounded up to
     * the next multiple of 4.
     *
     * @return  the internal block length (in bytes), or `-n`
     */
    fun getBlockLength(): Int

    /**
     *
     * Get the display name for this function (e.g. `"SHA-1"`
     * for SHA-1).
     *
     * @see Object
     */
    override fun toString(): String
}