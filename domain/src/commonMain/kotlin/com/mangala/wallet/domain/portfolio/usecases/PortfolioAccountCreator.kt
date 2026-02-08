package com.mangala.wallet.domain.portfolio.usecases

import com.mangala.wallet.domain.portfolio.mapper.PortfolioNetworkMapper
import com.mangala.wallet.domain.portfolio.model.CreatePortfolioRequest
import com.mangala.wallet.domain.portfolio.model.InitialWallet
import com.mangala.wallet.domain.wallet.usecases.MapAccountToAccountBlockchainUseCase
import com.mangala.wallet.domain.wallet.usecases.account.AccountCreator
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.wallet.domain.WalletModel


/**
 * AccountCreator implementation that automatically registers portfolios
 * with the Mangala Portfolio API when accounts are created
 */
class PortfolioAccountCreator(
    private val createPortfolioUseCase: CreatePortfolioUseCase,
    private val portfolioNetworkMapper: PortfolioNetworkMapper,
    private val mapAccountToAccountBlockchainUseCase: MapAccountToAccountBlockchainUseCase
) : AccountCreator {

    override suspend fun createAccount(
        accountId: String,
        derivationPathIndex: Int,
        wallet: WalletModel
    ) {
        try {
            // Register portfolios for all supported network types
            portfolioNetworkMapper.getSupportedNetworkTypes().forEach { networkType ->
                registerPortfolioForNetwork(
                    derivationPathIndex = derivationPathIndex,
                    wallet = wallet,
                    networkType = networkType
                )
            }
        } catch (e: Exception) {
            println("PortfolioAccountCreator: Failed to register portfolios: ${e.message}")
            // Don't throw - portfolio registration failure shouldn't block account creation
        }
    }

    private suspend fun registerPortfolioForNetwork(
        derivationPathIndex: Int,
        wallet: WalletModel,
        networkType: NetworkType
    ) {
        try {
            val address = deriveAddressForNetwork(wallet, derivationPathIndex, networkType) ?: return
            val networkId = portfolioNetworkMapper.mapNetworkTypeToNetworkId(networkType)

            val request = CreatePortfolioRequest(
                name = "${wallet.name} - ${networkType.name} Portfolio",
                description = "Auto-created portfolio for ${networkType.name} network",
                networkId = networkId,
                initialWallet = InitialWallet(
                    address = address,
                    label = wallet.name
                )
            )

            createPortfolioUseCase(request)
            println("PortfolioAccountCreator: Successfully registered portfolio on network ${networkType.name}")

        } catch (e: Exception) {
            println("PortfolioAccountCreator: Failed to register portfolio on network ${networkType.name}: ${e.message}")
            // Continue with other networks even if one fails
        }
    }

    private fun deriveAddressForNetwork(
        wallet: WalletModel,
        derivationPathIndex: Int,
        networkType: NetworkType
    ): String? {
        val blockchainType = when (networkType) {
            NetworkType.EVM -> BlockchainType.Ethereum
            NetworkType.BITCOIN -> BlockchainType.Bitcoin
            NetworkType.ANTELOPE -> return null // Antelope uses account names, handled separately
            NetworkType.OTHER, NetworkType.UNSUPPORTED -> return null
        }

        val addresses = mapAccountToAccountBlockchainUseCase(derivationPathIndex, wallet, blockchainType)
        return when (networkType) {
            NetworkType.EVM -> addresses.bip44Address
            NetworkType.BITCOIN -> addresses.bip84Address.ifEmpty { addresses.bip44Address }
            else -> null
        }
    }
}
