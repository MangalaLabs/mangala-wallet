package com.mangala.wallet.core.address.domain.usecases

import com.appmattus.crypto.Algorithm
import com.mangala.wallet.cryptography.base58
import com.mangala.wallet.cryptography.bech32.bech32Encode
import com.mangala.wallet.model.blockchain.AddressType
import com.soywiz.krypto.sha256
import fr.acinq.secp256k1.Secp256k1

class DeriveBitcoinAddressUseCase {

    operator fun invoke(publicKey: ByteArray, addressType: AddressType, isTestnet: Boolean): String {
        return when (addressType) {
            AddressType.Bip44 -> generateBitcoinBip44Address(publicKey)
            AddressType.Bip49 -> generateBitcoinBip49Address(publicKey)
            AddressType.Bip84 -> generateBitcoinBip84Address(publicKey, isTestnet)
        }
    }

    private fun generateBitcoinBip44Address(publicKey: ByteArray): String {
        val publicKeyCompressed = Secp256k1.pubKeyCompress(publicKey)

        val sha256Hash = publicKeyCompressed.sha256().bytes
        val digest = Algorithm.RipeMD160.createDigest()
        digest.update(sha256Hash)
        val publicKeyHash = digest.digest()

        val extendedPublicKeyHash = byteArrayOf(0x00) + publicKeyHash

        val firstChecksum = extendedPublicKeyHash.sha256().bytes
        val secondChecksum = firstChecksum.sha256().bytes

        val binaryAddress = extendedPublicKeyHash + secondChecksum.copyOfRange(0, 4)

        return binaryAddress.base58()
    }

    private fun generateBitcoinBip49Address(publicKey: ByteArray): String {
        val publicKeyCompressed = Secp256k1.pubKeyCompress(publicKey)

        val sha256Hash = publicKeyCompressed.sha256().bytes
        val digest = Algorithm.RipeMD160.createDigest()
        digest.update(sha256Hash)
        val publicKeyHash = digest.digest()

        // Create a P2WPKH (Pay-to-Witness-Public-Key-Hash) script
        val p2wpkhScript = byteArrayOf(0x00, 0x14) + publicKeyHash

        // Hash the P2WPKH script with SHA256 and RIPEMD160
        val scriptHashDigest = Algorithm.RipeMD160.createDigest()
        scriptHashDigest.update(p2wpkhScript.sha256().bytes)
        val scriptHash = scriptHashDigest.digest()

        // Use 0x05 as a prefix for mainnet P2SH addresses
        val extendedScriptHash = byteArrayOf(0x05) + scriptHash

        val firstChecksum = extendedScriptHash.sha256().bytes
        val secondChecksum = firstChecksum.sha256().bytes

        val binaryAddress = extendedScriptHash + secondChecksum.copyOfRange(0, 4)

        return binaryAddress.base58()
    }

    private fun generateBitcoinBip84Address(publicKey: ByteArray, testnet: Boolean = false): String {
        val publicKeyCompressed = Secp256k1.pubKeyCompress(publicKey)

        val sha256Hash = publicKeyCompressed.sha256().bytes
        val digest = Algorithm.RipeMD160.createDigest()
        digest.update(sha256Hash)
        val publicKeyHash = digest.digest()

        // Use the appropriate HRP (human-readable part) for the specific network
        val hrp = if (testnet) "tb" else "bc"

        // Use the witness version 0x00 and convert the public key hash to a 5-bit representation
        val data = convertBits(publicKeyHash, 0, publicKeyHash.size, 8, 5)

        // Encode the address using the custom Bip84 encoding implementation
        return bech32Encode(hrp, intArrayOf(0x00) + data)
    }

    private fun convertBits(data: ByteArray, start: Int, size: Int, fromBits: Int, toBits: Int, pad: Boolean = true): IntArray {
        var acc = 0
        var bits = 0
        val maxv = (1 shl toBits) - 1
        val result = mutableListOf<Int>()

        for (i in start until start + size) {
            val value = data[i].toInt() and 0xff
            if (value ushr fromBits != 0) {
                throw IllegalArgumentException("Invalid data range: data[$i]=$value (fromBits=$fromBits)")
            }
            acc = (acc shl fromBits) or value
            bits += fromBits

            while (bits >= toBits) {
                bits -= toBits
                result.add((acc ushr bits) and maxv)
            }
        }

        if (pad && bits > 0) {
            result.add((acc shl (toBits - bits)) and maxv)
        } else if (bits >= fromBits || ((acc shl (toBits - bits)) and maxv) != 0) {
            throw IllegalArgumentException("Invalid padding")
        }

        return result.toIntArray()
    }
}