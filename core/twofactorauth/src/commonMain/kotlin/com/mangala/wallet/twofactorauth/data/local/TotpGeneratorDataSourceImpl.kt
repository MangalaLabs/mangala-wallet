package com.mangala.wallet.twofactorauth.data.local

import com.mangala.wallet.twofactorauth.data.EncryptionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.experimental.and
import kotlin.math.pow

class TotpGeneratorDataSourceImpl : TotpGeneratorDataSource {
    companion object {
        private const val TIME_STEP_SECONDS = 30
        private const val OTP_LENGTH = 6
    }

    override suspend fun generateTOTP(secret: ByteArray, timeMillis: Long): String =
        withContext(Dispatchers.Default) {
            val counter = timeMillis / (TIME_STEP_SECONDS * 1000)
            generateTOTPForCounter(secret, counter)
        }

    override suspend fun validateTOTP(secret: ByteArray, inputCode: String, timeMillis: Long): Boolean =
        withContext(Dispatchers.Default) {
            println("validateTOTP called with secret length: ${secret.size}")
            println("inputCode: $inputCode")

            if (inputCode.length != OTP_LENGTH || !inputCode.all { it.isDigit() }) {
                println("Invalid code format: length=${inputCode.length}, all digits=${inputCode.all { it.isDigit() }}")
                return@withContext false
            }

            val currentCounter = timeMillis / (TIME_STEP_SECONDS * 1000)
            println("Current counter: $currentCounter")

            // Check current, previous, and next time steps for clock skew tolerance
            for (counter in listOf(currentCounter - 1, currentCounter, currentCounter + 1)) {
                println("Checking counter: $counter")
                try {
                    val code = generateTOTPForCounter(secret, counter)
                    println("Generated code for counter $counter: $code")
                    if (code == inputCode) {
                        println("Code matched!")
                        return@withContext true
                    }
                } catch (e: Exception) {
                    println("Error generating TOTP: ${e.message}")
                    println("Stack trace: ${e.stackTraceToString()}")
                }
            }

            println("No code matched")
            return@withContext false
        }

    private suspend fun generateTOTPForCounter(secret: ByteArray, counter: Long): String {
        println("generateTOTPForCounter called with secret length: ${secret.size}, counter: $counter")

        try {
            // Create HMAC-SHA1 hash
            val counterBytes = EncryptionUtils.longToByteArray(counter)
            println("counterBytes length: ${counterBytes.size}")

            val hash = EncryptionUtils.hmacSha1(secret, counterBytes)
            println("hash length: ${hash.size}")

            // Dynamic truncation
            val offset = (hash[hash.size - 1] and 0xF).toInt()
            println("offset: $offset")

            // Generate 31-bit integer (mask with 0x7FFFFFFF to remove sign bit)
            val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                    ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                    ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                    (hash[offset + 3].toInt() and 0xFF)
            println("binary: $binary")

            // Generate OTP_LENGTH digits
            val otp = (binary % 10.0.pow(OTP_LENGTH.toDouble())).toInt()
            println("otp: $otp")

            // Pad with leading zeros if necessary
            return otp.toString().padStart(OTP_LENGTH, '0')
        } catch (e: Exception) {
            println("Error in generateTOTPForCounter: ${e.message}")
            println("Stack trace: ${e.stackTraceToString()}")
            throw e
        }
    }
}