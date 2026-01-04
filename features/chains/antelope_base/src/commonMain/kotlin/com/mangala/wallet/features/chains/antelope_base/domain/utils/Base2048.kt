package com.mangala.wallet.features.chains.antelope_base.domain.utils

import com.mangala.wallet.utils.bip39.BIP39_WORDLIST_ENGLISH

object Base2048 {
    fun encode(data: ByteArray): List<Int> {
        if (data.isEmpty()) {
            return emptyList()
        }

        val words = mutableListOf(0)
        var carry: Int

        for (p in data.indices) {
            carry = data[p].toInt() and 0xFF
            for (i in words.indices) {
                carry += (words[i] shl 8)
                words[i] = carry % 2048
                carry = carry / 2048
            }
            while (carry > 0) {
                words.add(carry % 2048)
                carry /= 2048
            }
        }

        for (i in 0 until data.size - 1) {
            if (data[i].toInt() != 0) {
                break
            }
            words.add(0)
        }

        return words.reversed()
    }

    fun decode(wordsString: List<String>): ByteArray {
        if (wordsString.isEmpty()) {
            return byteArrayOf()
        }
        val words = wordsString.map { getBase2048Index(it) }.toMutableList()

        val data = mutableListOf(0)
        var carry: Int

        for (p in words.indices) {
            carry = words[p] or 0
            for (i in data.indices) {
                carry += (data[i] * 2048) or 0
                data[i] = carry and 0xff
                carry = carry shr 8
            }
            while (carry > 0) {
                data.add(carry and 0xff)
                carry = carry shr 8
            }
        }

        for (i in 0 until words.size - 1) {
            if (words[i] != 0) {
                break
            }
            data.add(0)
        }

        return data.reversed().map { it.toByte() }.toByteArray()
    }

    private fun getBase2048Index(word: String): Int {
        val processedWord = word.lowercase().trim()
        val rv = BIP39_WORDLIST_ENGLISH.indexOf(processedWord)
        if (rv == -1) {
            throw IllegalArgumentException("Word not found in BIP39 wordlist")
        }
        return rv
    }

    fun List<Int>.base2048ToWordList(): List<String> {
        return this.map { BIP39_WORDLIST_ENGLISH[it] }
    }
}