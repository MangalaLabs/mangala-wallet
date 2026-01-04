package com.mangala.wallet.wallet.presentation.restore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextTitle4
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

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

        val nextImportedScreen = rememberScreen(
            when (nextScreen) {
                SharedScreen.ScreenType.HOME_SCREEN -> SharedScreen.HomeScreen()
                SharedScreen.ScreenType.IMPORT_EOS_VIA_EVM -> SharedScreen.ImportEOSAccountViaEVMScreen
                else -> SharedScreen.HomeScreen()
            }
        )

        val title = MR.strings.title_recovery_phrase.desc().localized()
        val description1 = MR.strings.description_recovery_phrase.desc().localized()

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        LaunchedEffect(uiState) {
            when (uiState) {
                is RestoreRecoveryPhraseScreenUiState.Imported -> {
                    val setUpPinScreen = ScreenRegistry.get(
                        SharedScreen.SetupPinScreen(
                            listString = uiState.mnemonicWords,
                            name = "main",
                            pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.RESTORE_WALLET.name
                        )
                    )

                    if (screenModel.isPinExist()) {
                        if (nextScreen == SharedScreen.ScreenType.HOME_SCREEN) {
                            globalNavigator.replaceAll(nextImportedScreen)
                        } else {
                            navigator.push(nextImportedScreen)
                        }
                    } else {
                        navigator.push(setUpPinScreen)
                    }
                    screenModel.resetUiState()
                }

                is RestoreRecoveryPhraseScreenUiState.NoImported -> {

                }
            }
        }

        BaseRecoveryPhraseScreen(
            screenModel = screenModel,
            title = title,
            description = description1,
            onBackClicked = { navigator.pop() },
            onRestoreWalletClicked = {
                screenModel.importWallet()
            }
        )
    }

    @Composable
    private fun BaseRecoveryPhraseScreen(
        screenModel: RestoreRecoveryPhraseScreenModel,
        title: String,
        description: String,
        onBackClicked: (Boolean) -> Unit,
        onRestoreWalletClicked: () -> Unit,
    ) {
        val isRestoreButtonEnabled =
            screenModel.isRestoreButtonEnabled.collectAsStateMultiplatform()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                MangalaWalletTopBar(
                    text = MR.strings.all_import_wallet.desc().localized(),
                    onBackClicked = { onBackClicked(true) }
                )

                Spacer(modifier = Modifier.height(Spacing.SMALL))

                TextTitle4(
                    text = title,
                    modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(Spacing.SMALL))

                RecoveryPhraseTextField(screenModel)

                Spacer(modifier = Modifier.height(Spacing.TINY))

                TextDescription2(
                    text = description,
                    modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL)
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = Spacing.SMALL, end = Spacing.SMALL, bottom = Spacing.BASE)
                    .fillMaxWidth()
            ) {
                ButtonNormal(
                    text = MR.strings.all_import_wallet.desc().localized(),
                    onClick = onRestoreWalletClicked,
                    buttonModifier = Modifier.fillMaxWidth(),
                    enabled = isRestoreButtonEnabled.value
                )
//
//                TextButton(
//                    onClick = {},
//                    modifier = Modifier.fillMaxWidth(),
//                ) {
//                    TextNormal(
//                        text = MR.strings.button_recoverty_phrase_secret_phrase_info.desc().localized(),
//                        color = Colors.main1Text,
//                        fontWeight = FontWeight.SemiBold
//                    )
//                }
            }
        }
    }

    @Composable
    private fun RecoveryPhraseTextField(
        screenModel: RestoreRecoveryPhraseScreenModel,
    ) {
        val clipboardManager = LocalClipboardManager.current
        val focusManager = LocalFocusManager.current


        val recoveryPhrase = screenModel.recoveryPhrase.collectAsStateMultiplatform().value
        val recoveryPhraseState =
            screenModel.recoveryPhraseState.collectAsStateMultiplatform().value

        val spanStyle = SpanStyle(
            color = Colors.main1Text,
            fontFamily = fontFamilyResource(MR.fonts.sfpro),
            fontSize = FontType.SMALL,
        )

        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimensions.TextFieldRestoreRecoveryPhraseHeight)
                .padding(start = Dimensions.Padding.default, end = Dimensions.Padding.default)
                .border(
                    width = 1.dp,
                    color = Colors.stroke,
                    shape = RoundedCornerShape(CornerRadius.Medium)
                )
                .padding(
                    start = Dimensions.Padding.default,
                    end = Dimensions.Padding.default,
                    top = Dimensions.Padding.default
                ),
            value = recoveryPhrase,
            onValueChange = {
                screenModel.onInputRecoveryPhrase(it)
            },
            visualTransformation = {
                TransformedText(
                    buildAnnotatedString {
                        if (recoveryPhraseState.isNotEmpty()) {
                            recoveryPhraseState.dropLast(1).forEach { wordState ->
                                if (wordState.second) {
                                    withStyle(style = spanStyle) {
                                        append("${wordState.first} ")
                                    }
                                } else {
                                    withStyle(style = spanStyle.copy(color = Colors.main2)) {
                                        append("${wordState.first} ")
                                    }
                                }
                            }
                            val lastWordState = recoveryPhraseState.last()
                            if (lastWordState.second) {
                                withStyle(style = spanStyle) {
                                    append(lastWordState.first)
                                }
                            } else {
                                withStyle(style = spanStyle.copy(color = Colors.main2)) {
                                    append(lastWordState.first)
                                }
                            }
                        }
                    },
                    offsetMapping = OffsetMapping.Identity
                )
            },
            decorationBox = { innerTextField ->
                Box() {
                    innerTextField()

                    TextDescription2(
                        text = MR.strings.all_paste.desc().localized(),
                        color = Colors.teal,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .clickable {
                                val clipboardText = clipboardManager.getText()?.text ?: ""
                                screenModel.onInputRecoveryPhrase(clipboardText)
                                focusManager.clearFocus()
                            }
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                autoCorrect = false,
                imeAction = ImeAction.Done
            )
        )
    }
}








