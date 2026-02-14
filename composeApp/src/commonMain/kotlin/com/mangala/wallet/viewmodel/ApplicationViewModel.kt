package com.mangala.wallet.viewmodel

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.core.notification.APP_NOTIFICATION_HOST
import com.mangala.wallet.core.notification.APP_NOTIFICATION_SCHEME
import com.mangala.wallet.core.notification.PATH_MULTISIG
import com.mangala.wallet.domain.datastore.usecases.CheckPrePermissionDoneUseCase
import com.mangala.wallet.domain.language.usecase.GetCurrentLanguageUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.pin.domain.GetIsPinSetupUseCase
import com.mangala.wallet.pin.presentation.unlock.UnlockPinScreen
import com.mangala.wallet.ui.SharedScreen
import kotlinx.coroutines.runBlocking
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import dev.theolm.rinku.DeepLink
import dev.theolm.rinku.Rinku
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class ApplicationViewModel(
    private val getCurrentLanguageUseCase: GetCurrentLanguageUseCase,
    private val getIsPinSetupUseCase: GetIsPinSetupUseCase,
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val checkPrePermissionDoneUseCase: CheckPrePermissionDoneUseCase
) : BaseViewModel() {

    init {
        lifecycle.whileAttached {
//            notificationSchedulingService.runScheduling()
        }

        lifecycle.whileAttached {
//            syncService.runSynchronization()
        }

        listenForDeepLinkNotifications()
    }

    fun onAppear() {
        lifecycle.whileAttached {
//            if (settingsGateway.settings().value.isFeedbackEnabled) {
//                presentNextFeedback()
//            }
        }
    }

    fun getCurrentLanguage() = getCurrentLanguageUseCase()

    fun processDeepLink(deepLink: DeepLink?): Screen {
        // Check if user is existing
        val isPinSetup = runBlocking { getIsPinSetupUseCase() }
        val hasWallet = runBlocking { getSelectedWalletUseCase() != null }
        val isExistingUser = isPinSetup || hasWallet

        if (!isExistingUser) {
            val isPrePermissionDone = runBlocking { checkPrePermissionDoneUseCase() }
            return if (isPrePermissionDone) {
                ScreenRegistry.get(SharedScreen.OnboardingScreen)
            } else {
                ScreenRegistry.get(SharedScreen.PrePermissionScreen)
            }
        }

        // Existing deep link processing logic
        val firstScreen = UnlockPinScreen(SharedScreen.UnlockPinScreen.OPEN_APP, antelopeAccountName = null)
        if (deepLink == null) {
            return firstScreen
        }

        // Handle ESR (EOSIO Signing Request) deep links
        if (deepLink.schema == "esr") {
            val esrUri = deepLink.data
            return ScreenRegistry.get(SharedScreen.EsrScreen(esrUri))
        }

        return if (deepLink.schema == APP_NOTIFICATION_SCHEME && deepLink.host == APP_NOTIFICATION_HOST) {
            when (deepLink.pathSegments.getOrNull(0)) {
                PATH_MULTISIG -> {
                    val proposalName = deepLink.parameters["proposalName"]
                    val submitter = deepLink.parameters["submitter"]
                    val chainId = deepLink.parameters["chainId"]

                    if (proposalName.isNullOrBlank() || submitter.isNullOrBlank()) {
                        return firstScreen
                    }

                    ScreenRegistry.get(
                        SharedScreen.MyProposalDetailScreen(
                            proposalName = proposalName,
                            submitter = submitter,
                            chainId = chainId
                        )
                    )
                }

                else ->
                    firstScreen
            }
        } else {
            firstScreen
        }
    }

    private fun listenForDeepLinkNotifications() {
        NotifierManager.addListener(object : NotifierManager.Listener {
            override fun onPayloadData(data: PayloadData) {
                super.onPayloadData(data)

                println("Rinku notification payload data $data")
            }

            override fun onNotificationClicked(data: PayloadData) {
                println("Rinku notification clicked payload data $data")


            }
        })
    }
}
