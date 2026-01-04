package com.mangala.wallet.features.conversationui.presentation.test

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen

class ConversationUiEntryPointTestScreen : BaseScreen<ConversationUiEntryPointTestScreenModel>() {

    @Composable
    override fun createScreenModel(): ConversationUiEntryPointTestScreenModel {
        return getScreenModel()
    }

    override val screenName: String
        get() = "CONVERSATION_UI_ENTRY_POINT_TEST"
    override val screenClassName: String
        get() = ConversationUiEntryPointTestScreen::class.simpleName.orEmpty()

    @Composable
    override fun ScreenContent(screenModel: ConversationUiEntryPointTestScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        OnboardingGradientBackground(
            modifier = Modifier.fillMaxSize(),
        ) {
            MangalaCircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.mangalaColors.iconPrimary
            )
        }

        LaunchedEffect(Unit) {
            screenModel.navigationEvents.collect { event ->
                when (event) {
                    is ConversationUiEntryPointTestScreenModel.NavigationEvent.NavigateToConversationUi -> {
                        val conversationScreen = ScreenRegistry.get(SharedScreen.ConversationUiScreen(null))
                        navigator.replace(conversationScreen)
                    }

                    is ConversationUiEntryPointTestScreenModel.NavigationEvent.NavigateToSessionList -> {
                        val sessionListScreen = ScreenRegistry.get(SharedScreen.ConversationSessionListScreen)
                        navigator.replace(sessionListScreen)
                    }

                    is ConversationUiEntryPointTestScreenModel.NavigationEvent.NavigateToSignIn -> {
                        val signInScreen = ScreenRegistry.get(SharedScreen.SignInScreen())
                        navigator.replace(signInScreen)
                    }

                    null -> {

                    }
                }
            }
        }
    }
}