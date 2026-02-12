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
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcCopy
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.InfoCircle
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.OnboardingButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
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

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        LaunchedEffect(uiState) {
            when (uiState) {
                is RestoreRecoveryPhraseScreenUiState.Imported -> {
                    // Prevent duplicate navigation using ScreenModel state
                    if (!screenModel.shouldNavigate()) return@LaunchedEffect

                    val mnemonicWords = uiState.mnemonicWords
                    val walletName = "main"

                    // Reset state BEFORE navigation to prevent re-trigger
                    screenModel.resetUiState()

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
                }

                is RestoreRecoveryPhraseScreenUiState.NoImported -> {
                    // State is clean, no action needed
                }
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
        val validationError = screenModel.validationError.collectAsStateMultiplatform().value

        val wordCount = recoveryPhrase.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size

        OnboardingGradientBackground(
            circleBackgroundEnabled = true,
            afterBackgroundModifier = Modifier.navigationBarsPadding().imePadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with back button and title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = Spacing.SMALL, vertical = Spacing.XSMALL),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(Spacing.XXXBASE)
                            .clip(CircleShape)
                            .clickable { onBackClicked() }
                            .padding(Spacing.TINY),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.ArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.TINY))

                    Text(
                        text = MR.strings.all_import_wallet.desc().localized(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        fontFamily = getInterFontFamily()
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.BASE))

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.MEDIUM),
                    verticalArrangement = Arrangement.spacedBy(Spacing.BASE)
                ) {
                    // Info Banner
                    InfoBanner()

                    // Recovery Phrase Input Section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Spacing.TINY)
                    ) {
                        // Label and word count
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = MR.strings.label_recovery_phrase.desc().localized(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.6f),
                                fontFamily = getInterFontFamily(),
                                modifier = Modifier.padding(start = Spacing.XTINY)
                            )

                            // Word count badge
                            Text(
                                text = StringDesc.ResourceFormatted(
                                    MR.strings.word_count_format,
                                    wordCount
                                ).localized(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF3B90FF),
                                fontFamily = getInterFontFamily(),
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFF3B90FF).copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(Spacing.STINY)
                                    )
                                    .padding(horizontal = Spacing.TINY, vertical = Spacing.XTINY)
                            )
                        }

                        // Input area
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(CornerRadius.Medium))
                                .border(
                                    width = 1.dp,
                                    color = Color(0xFF2A3E6C),
                                    shape = RoundedCornerShape(CornerRadius.Medium)
                                )
                                .background(Color.White.copy(alpha = 0.02f))
                                .padding(Spacing.SMALL)
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
                                        fontSize = 16.sp,
                                        color = Color(0xFFF1F5F9),
                                        lineHeight = 25.6.sp,
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
                                        autoCorrectEnabled = false,
                                        imeAction = ImeAction.Done
                                    ),
                                    decorationBox = { innerTextField ->
                                        Box {
                                            if (recoveryPhrase.isEmpty()) {
                                                Text(
                                                    text = MR.strings.placeholder_recovery_phrase.desc().localized(),
                                                    fontSize = 16.sp,
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
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(Spacing.TINY))
                                            .background(Color.White.copy(alpha = 0.05f))
                                            .clickable {
                                                val clipboardText = clipboardManager.getText()?.text ?: ""
                                                screenModel.onInputRecoveryPhrase(clipboardText)
                                                focusManager.clearFocus()
                                            }
                                            .padding(horizontal = Spacing.XSMALL, vertical = Spacing.STINY),
                                        horizontalArrangement = Arrangement.spacedBy(Spacing.STINY),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = MangalaWalletPack.IcCopy,
                                            contentDescription = "Paste",
                                            tint = Color(0xFF3B90FF),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = MR.strings.all_paste.desc().localized(),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color(0xFF3B90FF),
                                            fontFamily = getInterFontFamily()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Validation error message
                    if (validationError != null) {
                        val errorMessage = when (validationError) {
                            is ValidationError.InvalidLength ->
                                MR.strings.error_recovery_phrase_invalid_length.desc().localized()
                            is ValidationError.InvalidWord ->
                                MR.strings.error_recovery_phrase_invalid_word.desc().localized()
                            is ValidationError.InvalidChecksum ->
                                MR.strings.error_recovery_phrase_invalid_checksum.desc().localized()
                        }
                        Text(
                            text = errorMessage,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFFA0000),
                            fontFamily = getInterFontFamily(),
                            modifier = Modifier.padding(start = Spacing.XTINY)
                        )
                    }

                    // Warning box
                    WarningBox()
                }

                Spacer(modifier = Modifier.weight(1f))

                // Import Wallet Button
                OnboardingButton(
                    text = MR.strings.all_import_wallet.desc().localized(),
                    onClick = onImportWalletClicked,
                    isPrimary = isImportButtonEnabled.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.MEDIUM)
                        .padding(bottom = Spacing.BASE)
                )
            }
        }
    }

    @Composable
    private fun InfoBanner() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.Medium))
                .background(Color.White.copy(alpha = 0.05f))
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(CornerRadius.Medium)
                )
                .padding(Spacing.SMALL),
            horizontalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
        ) {
            Icon(
                imageVector = MangalaWalletPack.InfoCircle,
                contentDescription = "Info",
                tint = Color(0xFF3B90FF),
                modifier = Modifier.size(Spacing.MEDIUM)
            )

            Text(
                text = buildAnnotatedString {
                    append("Enter your ")
                    withStyle(style = SpanStyle(color = Color.White, fontWeight = FontWeight.Medium)) {
                        append("12-24 words")
                    }
                    append(" recovery phrase in the correct order to restore your existing wallet.")
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFA5B4CB),
                lineHeight = 19.5.sp,
                fontFamily = getInterFontFamily()
            )
        }
    }

    @Composable
    private fun WarningBox() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.Small))
                .background(Color(0xFFFACC15).copy(alpha = 0.05f))
                .border(
                    width = 1.dp,
                    color = Color(0xFFFACC15).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(CornerRadius.Small)
                )
                .padding(Spacing.SMALL)
        ) {
            Text(
                text = "⚠️ ${MR.strings.warning_never_share_phrase.desc().localized()}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFFACC15).copy(alpha = 0.8f),
                lineHeight = 19.2.sp,
                fontFamily = getInterFontFamily()
            )
        }
    }
}
