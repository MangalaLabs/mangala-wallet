package com.mangala.wallet.core.address.domain.usecases

import com.appmattus.crypto.Algorithm
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign

class DeriveEthereumAddressUseCase {

    operator fun invoke(publicKey: ByteArray): String {
        val publicKeyBytes = publicKey.drop(1).toByteArray()
        val digest = Algorithm.Keccak256.createDigest()
        digest.update(publicKeyBytes)
        val publicKeyHash = digest.digest()

        val ethereumAddressBytes = publicKeyHash.drop(12).toByteArray() // Last 20 bytes of hash of public key
        val ethereumAddress = BigInteger.fromByteArray(ethereumAddressBytes, Sign.POSITIVE).toString(16).padStart(40, '0')

        return "0x$ethereumAddress"
    }
}