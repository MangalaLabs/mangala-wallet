package com.mangala.wallet.domain.wallet.usecases

import com.mangala.wallet.core.address.domain.usecases.DeriveBitcoinAddressUseCase
import com.mangala.wallet.core.address.domain.usecases.DeriveEthereumAddressUseCase
import com.mangala.wallet.core.hdwallet.domain.usecases.GenerateHDKeyUseCase
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.account.domain.AccountModel
import com.mangala.wallet.model.account.domain.BlockchainAddresses
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.Blockchain
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.wallet.domain.WalletModel
import com.mangala.wallet.utils.ext.toHexString2

class MapAccountToAccountBlockchainUseCase(
    private val generateHDKeyUseCase: GenerateHDKeyUseCase,
    private val deriveEvmCompatibleAddressUseCase: DeriveEthereumAddressUseCase,
    private val deriveBitcoinAddressUseCase: DeriveBitcoinAddressUseCase
) {

    @Deprecated("Use invoke(wallet: WalletModel, account: AccountModel, blockchainType: BlockchainType) instead since WalletModel contains address now")
    operator fun invoke(account: AccountModel, wallet: WalletModel, blockchainType: BlockchainType) = account.mapToAccountBlockchainModel(wallet, blockchainType)

    operator fun invoke(
        derivationPathIndex: Int,
        wallet: WalletModel,
        blockchainType: BlockchainType
    ): BlockchainAddresses {
        val words = wallet.words.split(" ")
        val passphrase = wallet.passphrase

        val bip44Address = deriveBip44Address(derivationPathIndex, blockchainType, words, passphrase)
        val bip49Address = deriveBip49Address(derivationPathIndex, blockchainType, words, passphrase)
        val bip84Address = deriveBip84Address(derivationPathIndex, blockchainType, words, passphrase)
        val hexString = deriveIdentifyingPublicKey(derivationPathIndex, blockchainType, words, passphrase)

        return BlockchainAddresses(
            bip44Address = bip44Address,
            bip49Address = bip49Address,
            bip84Address = bip84Address,
            publicKey = hexString
        )
    }

    private fun AccountModel.mapToAccountBlockchainModel(
        wallet: WalletModel,
        blockchainType: BlockchainType,
    ): AccountBlockchainModel {
        val derivationPathIndex = this.derivationPathIndex
        val words = wallet.words.split(" ")
        val passphrase = wallet.passphrase

        val bip44Address = deriveBip44Address(derivationPathIndex, blockchainType, words, passphrase)
        val bip49Address = deriveBip49Address(derivationPathIndex, blockchainType, words, passphrase)
        val bip84Address = deriveBip84Address(derivationPathIndex, blockchainType, words, passphrase)

        return AccountBlockchainModel(
            account = this,
            bip44Address = bip44Address,
            bip49Address = bip49Address,
            bip84Address = bip84Address
        )
    }

    private fun deriveBip84Address(
        derivationPathIndex: Int,
        blockchainType: BlockchainType,
        words: List<String>,
        passphrase: String
    ) = if (blockchainType.supportedAddressTypes.contains(AddressType.Bip84)) {
        val hdKey = generateHDKeyUseCase(
            words,
            passphrase,
            Blockchain(blockchainType, "", null),
            AddressType.Bip84,
            derivationPathIndex = derivationPathIndex
        )
        deriveBitcoinAddressUseCase(
            hdKey.publicKey,
            AddressType.Bip84,
            isTestnet = blockchainType.isTestnet
        )
    } else ""

    private fun deriveBip49Address(
        derivationPathIndex: Int,
        blockchainType: BlockchainType,
        words: List<String>,
        passphrase: String
    ) = if (blockchainType.supportedAddressTypes.contains(AddressType.Bip49)) {
        val hdKey = generateHDKeyUseCase(
            words,
            passphrase,
            Blockchain(blockchainType, "", null),
            AddressType.Bip49,
            derivationPathIndex = derivationPathIndex
        )
        deriveBitcoinAddressUseCase(
            hdKey.publicKey,
            AddressType.Bip49,
            isTestnet = blockchainType.isTestnet
        )
    } else ""

    private fun deriveBip44Address(
        derivationPathIndex: Int,
        blockchainType: BlockchainType,
        words: List<String>,
        passphrase: String
    ): String {
        val hdKey = generateHDKeyUseCase(
            words,
            passphrase,
            Blockchain(blockchainType, "", null),
            AddressType.Bip44,
            derivationPathIndex = derivationPathIndex
        )

        return when (blockchainType.networkType) {
            NetworkType.EVM -> deriveEvmCompatibleAddressUseCase(hdKey.publicKey)
            NetworkType.BITCOIN -> deriveBitcoinAddressUseCase(
                hdKey.publicKey,
                AddressType.Bip44,
                isTestnet = blockchainType.isTestnet
            )
            else -> throw IllegalArgumentException("Unsupported blockchain type for BIP44 derivation: $blockchainType")
        }
    }

    private fun deriveIdentifyingPublicKey(
        derivationPathIndex: Int,
        blockchainType: BlockchainType,
        words: List<String>,
        passphrase: String
    ): String {
        val hdKey = generateHDKeyUseCase(
            words,
            passphrase,
            Blockchain(blockchainType, "", null),
            AddressType.Bip44,
            derivationPathIndex = derivationPathIndex
        )
        return hdKey.publicKey.toHexString2()
    }
}