package com.mangala.wallet.wallet.presentation.restore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.OnboardingButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics

class RestoreRecoveryPhraseScreen(
    val nextScreen: SharedScreen.ScreenType = SharedScreen.ScreenType.HOME_SCREEN
) : BaseScreen<RestoreRecoveryPhraseScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_RESTORE_RECOVERY_PHRASE
    override val screenClassName: String = RestoreRecoveryPhraseScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): RestoreRecoveryPhraseScreenModel = getScreenModel()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun ScreenContent(screenModel: RestoreRecoveryPhraseScreenModel) {
        val globalNavigator = LocalGlobalNavigator.current
        val navigator = LocalNavigator.currentOrThrow

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        LaunchedEffect(uiState) {
            when (uiState) {
                is RestoreRecoveryPhraseScreenUiState.Imported -> {
                    val mnemonicWords = uiState.mnemonicWords
                    val walletName = "main"

                    if (screenModel.isPinExist()) {
                        // PIN exists - verify user identity first (V2 callback approach)
                        val unlockPinScreen = ScreenRegistry.get(
                            SharedScreen.UnlockPinScreen(
                                onUnlockSuccess = {
                                    val importWalletSuccessScreen = ScreenRegistry.get(
                                        SharedScreen.ImportWalletSuccessScreen(
                                            mnemonicWords = mnemonicWords,
                                            walletName = walletName
                                        )
                                    )
                                    globalNavigator.replaceAll(importWalletSuccessScreen)
                                }
                            )
                        )
                        navigator.push(unlockPinScreen)
                    } else {
                        // PIN not set - go to SetupPinScreen with callback
                        val setUpPinScreen = ScreenRegistry.get(
                            SharedScreen.SetupPinScreen(
                                listString = mnemonicWords,
                                name = walletName,
                                pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.RESTORE_WALLET.name,
                                onPinSetupSuccess = {
                                    val importWalletSuccessScreen = ScreenRegistry.get(
                                        SharedScreen.ImportWalletSuccessScreen(
                                            mnemonicWords = mnemonicWords,
                                            walletName = walletName
                                        )
                                    )
                                    globalNavigator.replaceAll(importWalletSuccessScreen)
                                }
                            )
                        )
                        navigator.push(setUpPinScreen)
                    }
                    screenModel.resetUiState()
                }

                is RestoreRecoveryPhraseScreenUiState.NoImported -> {}
            }
        }

        RestoreRecoveryPhraseContent(
            screenModel = screenModel,
            onBackClicked = { navigator.pop() },
            onImportWalletClicked = { screenModel.importWallet() }
        )
    }

    @Composable
    private fun RestoreRecoveryPhraseContent(
        screenModel: RestoreRecoveryPhraseScreenModel,
        onBackClicked: () -> Unit,
        onImportWalletClicked: () -> Unit
    ) {
        val isImportButtonEnabled = screenModel.isRestoreButtonEnabled.collectAsStateMultiplatform()
        val clipboardManager = LocalClipboardManager.current
        val focusManager = LocalFocusManager.current
        val recoveryPhrase = screenModel.recoveryPhrase.collectAsStateMultiplatform().value
        val recoveryPhraseState = screenModel.recoveryPhraseState.collectAsStateMultiplatform().value

        OnboardingGradientBackground(
            circleBackgroundEnabled = true,
            afterBackgroundModifier = Modifier.navigationBarsPadding().imePadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar with back button and progress indicator
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { onBackClicked() }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.ArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    // Progress indicator (4 segments, 2 active)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // First 2 segments active (white)
                        repeat(2) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(2.dp)
                                    .background(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(1.dp)
                                    )
                            )
                        }
                        // Last 2 segments inactive (gray)
                        repeat(2) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(2.dp)
                                    .background(
                                        color = Color(0xFFA5B4CB),
                                        shape = RoundedCornerShape(1.dp)
                                    )
                            )
                        }
                    }

                    // Spacer to balance the layout
                    Spacer(modifier = Modifier.size(40.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Your recovery phrase",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFF1F5F9),
                        textAlign = TextAlign.Start,
                        letterSpacing = (-0.24).sp,
                        lineHeight = 28.8.sp,
                        fontFamily = getInterFontFamily()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Recovery Phrase Input Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(180.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            width = 1.dp,
                            color = Color(0xFF2A3E6C),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .background(Color.White.copy(alpha = 0.03f))
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Text input
                        BasicTextField(
                            value = recoveryPhrase,
                            onValueChange = { screenModel.onInputRecoveryPhrase(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 15.sp,
                                color = Color(0xFFF1F5F9),
                                lineHeight = 22.5.sp,
                                fontFamily = getInterFontFamily()
                            ),
                            visualTransformation = { text ->
                                TransformedText(
                                    buildAnnotatedString {
                                        if (recoveryPhraseState.isNotEmpty()) {
                                            recoveryPhraseState.dropLast(1).forEach { wordState ->
                                                val color = if (wordState.second) {
                                                    Color(0xFFF1F5F9)
                                                } else {
                                                    Color(0xFFFA0000)
                                                }
                                                withStyle(style = SpanStyle(color = color)) {
                                                    append("${wordState.first} ")
                                                }
                                            }
                                            val lastWordState = recoveryPhraseState.last()
                                            val lastColor = if (lastWordState.second) {
                                                Color(0xFFF1F5F9)
                                            } else {
                                                Color(0xFFFA0000)
                                            }
                                            withStyle(style = SpanStyle(color = lastColor)) {
                                                append(lastWordState.first)
                                            }
                                        }
                                    },
                                    offsetMapping = OffsetMapping.Identity
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                autoCorrect = false,
                                imeAction = ImeAction.Done
                            ),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (recoveryPhrase.isEmpty()) {
                                        Text(
                                            text = "Enter your recovery phrase...",
                                            fontSize = 15.sp,
                                            color = Color(0xFFA5B4CB).copy(alpha = 0.6f),
                                            fontFamily = getInterFontFamily()
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )

                        // Paste button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Paste",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF3B90FF),
                                fontFamily = getInterFontFamily(),
                                modifier = Modifier.clickable {
                                    val clipboardText = clipboardManager.getText()?.text ?: ""
                                    screenModel.onInputRecoveryPhrase(clipboardText)
                                    focusManager.clearFocus()
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    text = "Consists of 24 single words separated by spaces. If your recovery phrase is shorter than 24 words (i.e. 12, 15, 18 or 21 words) you can still use it to restore your wallet. Just enter as many words as you have in your phrase.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFA5B4CB).copy(alpha = 0.8f),
                    textAlign = TextAlign.Start,
                    letterSpacing = (-0.13).sp,
                    lineHeight = 19.5.sp,
                    fontFamily = getInterFontFamily(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Import Wallet Button
                OnboardingButton(
                    text = "Import Wallet",
                    onClick = onImportWalletClicked,
                    isPrimary = isImportButtonEnabled.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp)
                )
            }
        }
    }
}
