package com.mangala.wallet.features.chains.bitcoin.domain.usecases.account

import com.mangala.wallet.core.address.domain.usecases.DeriveBitcoinAddressUseCase
import com.mangala.wallet.core.hdwallet.domain.usecases.GenerateHDKeyUseCase
import com.mangala.wallet.domain.wallet.usecases.account.AccountCreator
import com.mangala.wallet.features.chains.bitcoin.domain.repository.account.BitcoinAccountRepository
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.Blockchain
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.wallet.domain.WalletModel

class CreateBitcoinAccountUseCase(
    private val bitcoinAccountRepository: BitcoinAccountRepository,
    private val generateHDKeyUseCase: GenerateHDKeyUseCase,
    private val deriveBitcoinAddressUseCase: DeriveBitcoinAddressUseCase
) : AccountCreator {

    override suspend fun createAccount(
        accountId: String,
        derivationPathIndex: Int,
        wallet: WalletModel
    ) {
        val allBitcoinNetworks = BlockchainNetworkData.getAllBlockchainNetworkSupported(includeDebugNetworks = true).filter { it.blockchainType.networkType == NetworkType.BITCOIN }

        allBitcoinNetworks.forEach {
            val blockchainType = it.blockchainType

            val bip44Address = generateBip44Address(wallet, blockchainType, derivationPathIndex)
            val bip49Address = generateBip49Address(wallet, blockchainType, derivationPathIndex)
            val bip84Address = generateBip84Address(wallet, blockchainType, derivationPathIndex)

            bitcoinAccountRepository.saveAccount(blockchainType, accountId, bip44Address, bip49Address, bip84Address)
        }
    }

    private fun generateBip44Address(wallet: WalletModel, blockchainType: BlockchainType, derivationPathIndex: Int): String {
        val hdKey = generateHDKeyUseCase(
            wallet.words.split(" "),
            wallet.passphrase,
            blockchain = Blockchain(blockchainType, blockchainType.name, null),
            AddressType.Bip44,
            derivationPathIndex = derivationPathIndex
        )

        return deriveBitcoinAddressUseCase(
            publicKey = hdKey.publicKey,
            addressType = AddressType.Bip44,
            isTestnet = blockchainType.isTestnet
        )
    }

    private fun generateBip49Address(wallet: WalletModel, blockchainType: BlockchainType, derivationPathIndex: Int): String {
        val hdKey = generateHDKeyUseCase(
            wallet.words.split(" "),
            wallet.passphrase,
            blockchain = Blockchain(blockchainType, blockchainType.name, null),
            AddressType.Bip49,
            derivationPathIndex = derivationPathIndex
        )

        return deriveBitcoinAddressUseCase(
            publicKey = hdKey.publicKey,
            addressType = AddressType.Bip49,
            isTestnet = blockchainType.isTestnet
        )
    }

    private fun generateBip84Address(wallet: WalletModel, blockchainType: BlockchainType, derivationPathIndex: Int): String {
        val hdKey = generateHDKeyUseCase(
            wallet.words.split(" "),
            wallet.passphrase,
            blockchain = Blockchain(blockchainType, blockchainType.name, null),
            AddressType.Bip84,
            derivationPathIndex = derivationPathIndex
        )

        return deriveBitcoinAddressUseCase(
            publicKey = hdKey.publicKey,
            addressType = AddressType.Bip84,
            isTestnet = blockchainType.isTestnet
        )
    }
}