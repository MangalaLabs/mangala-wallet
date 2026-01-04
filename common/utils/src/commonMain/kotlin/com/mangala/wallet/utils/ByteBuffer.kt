package com.mangala.wallet.utils

/**
 * A simple ByteBuffer implementation for Kotlin Multiplatform
 */
class ByteBuffer private constructor(
    private val buffer: ByteArray,
    private var position: Int = 0
) {
    companion object {
        /**
         * Create a buffer with the specified capacity
         */
        fun allocate(capacity: Int): ByteBuffer {
            return ByteBuffer(ByteArray(capacity))
        }

        /**
         * Wrap an existing byte array
         */
        fun wrap(array: ByteArray): ByteBuffer {
            return ByteBuffer(array)
        }
    }

    /**
     * Put an Int into the buffer
     */
    fun putInt(value: Int) {
        buffer[position++] = (value shr 24).toByte()
        buffer[position++] = (value shr 16).toByte()
        buffer[position++] = (value shr 8).toByte()
        buffer[position++] = value.toByte()
    }

    /**
     * Put a Long into the buffer
     */
    fun putLong(value: Long) {
        buffer[position++] = (value shr 56).toByte()
        buffer[position++] = (value shr 48).toByte()
        buffer[position++] = (value shr 40).toByte()
        buffer[position++] = (value shr 32).toByte()
        buffer[position++] = (value shr 24).toByte()
        buffer[position++] = (value shr 16).toByte()
        buffer[position++] = (value shr 8).toByte()
        buffer[position++] = value.toByte()
    }

    /**
     * Put a ByteArray into the buffer
     */
    fun put(src: ByteArray) {
        src.copyInto(buffer, position)
        position += src.size
    }

    /**
     * Get an Int from the buffer
     */
    fun getInt(): Int {
        return (buffer[position++].toInt() and 0xFF shl 24) or
                (buffer[position++].toInt() and 0xFF shl 16) or
                (buffer[position++].toInt() and 0xFF shl 8) or
                (buffer[position++].toInt() and 0xFF)
    }

    /**
     * Get a Long from the buffer
     */
    fun getLong(): Long {
        return (buffer[position++].toLong() and 0xFF shl 56) or
                (buffer[position++].toLong() and 0xFF shl 48) or
                (buffer[position++].toLong() and 0xFF shl 40) or
                (buffer[position++].toLong() and 0xFF shl 32) or
                (buffer[position++].toLong() and 0xFF shl 24) or
                (buffer[position++].toLong() and 0xFF shl 16) or
                (buffer[position++].toLong() and 0xFF shl 8) or
                (buffer[position++].toLong() and 0xFF)
    }

    /**
     * Get a ByteArray from the buffer
     */
    fun get(dst: ByteArray) {
        for (i in dst.indices) {
            dst[i] = buffer[position++]
        }
    }

    /**
     * Get the buffer as a ByteArray
     */
    fun array(): ByteArray {
        return buffer
    }
}