package com.mangala.wallet.core.hdwallet.domain.usecases

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.Sign
import com.mangala.wallet.core.hdwallet.domain.model.HDKey
import com.mangala.wallet.cryptography.pbkdf2sha512
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.Blockchain
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import fr.acinq.secp256k1.Secp256k1
import okio.ByteString.Companion.toByteString

class GenerateHDKeyUseCase {

    operator fun invoke(
        seedPhrase: List<String>,
        passphrase: String = "",
        blockchain: Blockchain,
        addressType: AddressType,
        accountNumber: Int = 0,
        chain: Int = 0,
        derivationPathIndex: Int = 0
    ): HDKey {
        // Refer to this for list of coin type index https://github.com/satoshilabs/slips/blob/master/slip-0044.md
        val coinTypeIndex = when (blockchain.type.networkType) {
            NetworkType.EVM -> 60
            NetworkType.BITCOIN -> if (blockchain.type.isTestnet) 1 else 0
            NetworkType.ANTELOPE -> 194
            NetworkType.OTHER, NetworkType.UNSUPPORTED -> 60
        }
        val path = "m/${addressType.derivationPathPurpose}'/${coinTypeIndex}'/${accountNumber}'/${chain}/${derivationPathIndex}"
        println("GenerateHDKeyUseCase path: $path")

        return derivePath(seedPhrase, passphrase, path)
    }

    internal fun derivePath(seedPhrase: List<String>, passphrase: String, path: String): HDKey {
        val rootKey = generateBip32RootKey(seedPhrase, passphrase)
        var hdKey = rootKey

        val segments = path.replace("m/", "").split("/")
        for (segment in segments) {
            if (segment.isBlank()) continue
            val isHardened = segment.endsWith("'")
            val index = segment.trimEnd('\'').toInt()
            hdKey = deriveChildKey(hdKey, index, isHardened)
        }
        return hdKey
    }

    private fun generateBip32RootKey(seedPhrase: List<String>, passphrase: String): HDKey {
        val bip39Seed = pbkdf2sha512(
            password = seedPhrase.joinToString(" "),
            salt = "mnemonic$passphrase",
            rounds = 2048,
            derivedKeyLength = 64
        )
        val i = hmacSha512(
            key = "Bitcoin seed".encodeToByteArray(),
            input = bip39Seed
        )

        val masterPrivateKey = i.copyOfRange(0, 32)
        val masterChainCode = i.copyOfRange(32, 64)

        return HDKey(masterPrivateKey, masterChainCode, accountIndex = 0)
    }

    private fun deriveChildKey(parentKey: HDKey, index: Int, isHardened: Boolean): HDKey {
        val data = if (isHardened) {
            parentKey.privateKey.getPaddedBytes(33) + (index or HARDENED_FLAG).toBytes(4, false)
        } else {
            val parentPublicKey = Secp256k1.pubkeyCreate(parentKey.privateKey)
            val parentPublicKeyCompressed = Secp256k1.pubKeyCompress(parentPublicKey)
            parentPublicKeyCompressed + index.toBytes(4, false)
        }
        val i = hmacSha512(parentKey.chainCode, data)
        val iLeft = i.copyOfRange(0, 32)
        val iRight = i.copyOfRange(32, 64)
        val privateKey = BigInteger.fromByteArray(iLeft, Sign.POSITIVE)
            .plus(BigInteger.fromByteArray(parentKey.privateKey, Sign.POSITIVE))
            .mod(N)
        return HDKey(
            privateKey.toByteArray().getPaddedBytes(32),
            chainCode = iRight,
            accountIndex = index
        )
    }

    private fun hmacSha512(key: ByteArray, input: ByteArray): ByteArray {
        return input.toByteString().hmacSha512(key.toByteString()).toByteArray()
    }

    private fun Int.toBytes(size: Int, littleEndian: Boolean): ByteArray {
        val buffer = ByteArray(size)
        if (littleEndian) {
            for (i in 0 until size) {
                buffer[i] = (this shr (i * 8)).toByte()
            }
        } else {
            for (i in 0 until size) {
                buffer[size - i - 1] = (this shr (i * 8)).toByte()
            }
        }
        return buffer
    }

    private fun ByteArray.getPaddedBytes(size: Int): ByteArray {
        val paddedBytes = ByteArray(size)
        this.copyInto(paddedBytes, size - this.size)
        return paddedBytes
    }

    companion object {
        private const val HARDENED_FLAG: Int = 0x80000000.toInt()

        val N = BigInteger.parseString(
            "fffffffffffffffffffffffffffffffebaaedce6af48a03bbfd25e8cd0364141",
            16
        ) // order of the secp256k1 elliptic curve used in Ethereum
    }
}