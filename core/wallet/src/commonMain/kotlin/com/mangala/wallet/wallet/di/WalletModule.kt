package com.mangala.wallet.wallet.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRendererPlugin
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.wallet.presentation.backup.*
import com.mangala.wallet.wallet.presentation.conversation.CoreWaleltConfirmationRendererPlugin
import com.mangala.wallet.wallet.presentation.create.CreateWalletGuideScreen
import com.mangala.wallet.wallet.presentation.create.CreateWalletGuideScreenModel
import com.mangala.wallet.wallet.presentation.create.CreateWalletScreen
import com.mangala.wallet.wallet.presentation.create.CreateWalletScreenModel
import com.mangala.wallet.wallet.presentation.import.ImportWalletGuideScreen
import com.mangala.wallet.wallet.presentation.reset.ResetWalletScreen
import com.mangala.wallet.wallet.presentation.reset.ResetWalletScreenModel
import com.mangala.wallet.wallet.presentation.reset.ResetWalletScreenV2
import com.mangala.wallet.wallet.presentation.restore.ImportWalletSuccessScreen
import com.mangala.wallet.wallet.presentation.restore.ImportWalletSuccessScreenModel
import com.mangala.wallet.wallet.presentation.restore.RestoreRecoveryPhraseScreen
import com.mangala.wallet.wallet.presentation.restore.RestoreRecoveryPhraseScreenModel
import com.mangala.wallet.wallet.presentation.restore.RestoreWalletGuideScreen
import com.mangala.wallet.wallet.presentation.restore.RestoreWalletGuideScreenModel
import com.mangala.wallet.wallet.presentation.restore.RestoreWalletGuideScreenV2
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val coreWalletModule = module {
    factory {
        CreateWalletScreenModel(
            createWalletUseCase = get(),
            updateAccountStatusUseCase = get(),
            restoreWalletUseCase = get(),
            getSelectedNetworkUseCase = get()
        )
    }
    factory { CreateWalletGuideScreenModel(get()) }
    factory { BackupWalletDoneScreenModel() }
    factory { ShowRecoveryPhraseScreenModel(getAllWalletsUseCase = get()) }
    factory { RestoreRecoveryPhraseScreenModel(get(), get(), get()) }
    factory { ImportWalletSuccessScreenModel(get(), get()) }
    factory { BackupWalletAlertScreenModel(get()) }
    single<ConfirmationRendererPlugin>(named("CoreWalletConfirmationRenderer")) { CoreWaleltConfirmationRendererPlugin() }
    factoryOf(::ResetWalletScreenModel)
    factoryOf(::RestoreWalletGuideScreenModel)
}

val coreWalletScreenModule = screenModule {
    register<SharedScreen.ResetWalletScreen> {
        ResetWalletScreenV2()
    }

    register<SharedScreen.RestoreWalletScreen> {
        RestoreWalletGuideScreenV2()
    }

    register<SharedScreen.RestoreRecoveryPhraseScreen> {
        RestoreRecoveryPhraseScreen(
            nextScreen = it.nextScreen
        )
    }

    register<SharedScreen.CreateWalletGuideScreen> {
        CreateWalletGuideScreen()
    }

    register<SharedScreen.CreateWalletScreen> { provider ->
        CreateWalletScreen(
            provider.blockchainUid,
            provider.antelopeAccountName,
            provider.listString,
            provider.name,
            provider.createWalletCase
        )
    }

    register<SharedScreen.ShowRecoveryPhraseScreen> {
        ShowRecoveryPhraseScreen()
    }

    register<SharedScreen.BackupWalletAlertScreen> {
        BackupWalletAlertScreen(it.blockchainUid, it.antelopeAccountName)
    }

    register<SharedScreen.VerifyRecoveryPhraseScreen> {
        VerifyRecoveryPhraseScreen()
    }

    register<SharedScreen.BackupWalletDoneScreen> {
        BackupWalletDoneScreen()
    }

    register<SharedScreen.ImportWalletGuideScreen> {
        ImportWalletGuideScreen(
            nextScreen = it.nextScreen
        )
    }

    register<SharedScreen.ImportWalletSuccessScreen> {
        ImportWalletSuccessScreen(
            mnemonicWords = it.mnemonicWords,
            walletName = it.walletName
        )
    }
}