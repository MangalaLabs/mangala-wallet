package com.mangala.wallet.features.onboarding.domain.navigator

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.blockchain.PrimaryNetworkConfig
import com.mangala.wallet.pin.domain.GetIsPinSetupUseCase
import com.mangala.wallet.ui.SharedScreen

interface CreateWalletNavigator {
    /**
     * Returns true if PIN is already set up
     */
    fun isPinSetup(): Boolean

    /**
     * Returns the SetupPinScreen for this wallet type
     * @param onPinSetupSuccess callback to be invoked when PIN setup succeeds
     */
    fun getSetupPinScreen(onPinSetupSuccess: () -> Unit): SharedScreen

    /**
     * Returns the CreateWalletScreen for this wallet type (used when PIN is already set)
     */
    fun getCreateWalletScreen(): SharedScreen
}

class AntelopeCreateWalletNavigator(
    private val getIsPinSetupUseCase: GetIsPinSetupUseCase
) : CreateWalletNavigator {
    override fun isPinSetup(): Boolean = getIsPinSetupUseCase()

    override fun getSetupPinScreen(onPinSetupSuccess: () -> Unit): SharedScreen {
        return SharedScreen.SetupPinScreen(
            blockchainUid = BlockchainType.Eos.uid,
            pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_WALLET.name,
            onPinSetupSuccess = onPinSetupSuccess
        )
    }

    override fun getCreateWalletScreen(): SharedScreen {
        return SharedScreen.AntelopeCreateAccountV2Screen
    }
}

class EvmCreateWalletNavigator(
    private val getIsPinSetupUseCase: GetIsPinSetupUseCase
) : CreateWalletNavigator {
    override fun isPinSetup(): Boolean = getIsPinSetupUseCase()

    override fun getSetupPinScreen(onPinSetupSuccess: () -> Unit): SharedScreen {
        return SharedScreen.SetupPinScreen(
            blockchainUid = BlockchainType.Ethereum.uid,
            pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_WALLET.name,
            onPinSetupSuccess = onPinSetupSuccess
        )
    }

    override fun getCreateWalletScreen(): SharedScreen {
        return SharedScreen.CreateWalletScreen(
            blockchainUid = BlockchainType.Ethereum.uid,
            createWalletCase = SharedScreen.CreateWalletScreen.CreateWalletScreenCase.CREATE_NEW_WALLET
        )
    }
}

class BitcoinCreateWalletNavigator(
    private val getIsPinSetupUseCase: GetIsPinSetupUseCase
) : CreateWalletNavigator {
    override fun isPinSetup(): Boolean = getIsPinSetupUseCase()

    override fun getSetupPinScreen(onPinSetupSuccess: () -> Unit): SharedScreen {
        return SharedScreen.SetupPinScreen(
            blockchainUid = BlockchainType.Bitcoin.uid,
            pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_WALLET.name,
            onPinSetupSuccess = onPinSetupSuccess
        )
    }

    override fun getCreateWalletScreen(): SharedScreen {
        return SharedScreen.BitcoinCreateAccountScreen()
    }
}

object CreateWalletNavigatorFactory {
    fun create(
        blockchainType: BlockchainType = PrimaryNetworkConfig.primaryBlockchain,
        getIsPinSetupUseCase: GetIsPinSetupUseCase
    ): CreateWalletNavigator {
        return when (blockchainType.networkType) {
            NetworkType.EVM -> EvmCreateWalletNavigator(getIsPinSetupUseCase)
            NetworkType.ANTELOPE -> AntelopeCreateWalletNavigator(getIsPinSetupUseCase)
            NetworkType.BITCOIN -> BitcoinCreateWalletNavigator(getIsPinSetupUseCase)
            NetworkType.OTHER, NetworkType.UNSUPPORTED -> EvmCreateWalletNavigator(getIsPinSetupUseCase)
        }
    }
}