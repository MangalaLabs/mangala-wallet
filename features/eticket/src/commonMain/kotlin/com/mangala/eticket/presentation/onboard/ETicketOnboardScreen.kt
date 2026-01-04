package com.mangala.eticket.presentation.onboard

import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.eticket.ETicketSharedScreen
import com.mangala.eticket.presentation.ErrorPage
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform

/**
 * E-ticket onboard screen.
 * Has function login and register account
 * After login success, it will navigate to E-ticket home screen
 */
@Composable
fun ETicketOnboardScreen(screenModel: ETicketOnboardScreenModel) {
    val localNavigator = LocalNavigator.currentOrThrow
    val userFullName = screenModel.userFullName.collectAsStateMultiplatform().value
    val hasAccount = screenModel.hasAccount.collectAsStateMultiplatform().value
    val hasUnknownError = screenModel.unknownError.collectAsStateMultiplatform().value
    val isLoading = screenModel.isLoading.collectAsStateMultiplatform().value
    val messageToSign = screenModel.messageToSign.collectAsStateMultiplatform().value
    val isLoggedIn = screenModel.isLoggedIn.collectAsStateMultiplatform().value

    if (isLoading) {
        MaxSizeBox(
            contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.Red)
        }
    } else {
        if (hasUnknownError) {
            ErrorPage()
        }

        if (hasAccount) {
            LoginView(
                messageToSign
            ){
                val pinScreen = ScreenRegistry.get(SharedScreen.UnlockPinScreen(
                    SharedScreen.UnlockPinScreen.CONFIRM_DAPP,
                    unlockPinCallback = {isCorrectPin ->
                        if (isCorrectPin) {
                            screenModel.login()
                        }
                    },
                    antelopeAccountName = null
                ))
                localNavigator.push(pinScreen)
            }
        } else {
            Register(
                userFullName,
                screenModel::onCreateUserFullNameChange,
                screenModel::createUser
            )
        }

        if (isLoggedIn) {
            LaunchedEffect(Unit) {
                val homeScreen = ScreenRegistry.get(ETicketSharedScreen.ETicketHomeScreen)
                localNavigator.push(homeScreen)
            }
        }
    }
}