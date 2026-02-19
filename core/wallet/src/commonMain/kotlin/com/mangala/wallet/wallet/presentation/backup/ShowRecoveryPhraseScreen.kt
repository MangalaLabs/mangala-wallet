package com.mangala.wallet.wallet.presentation.backup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SecureScreen
import com.mangala.wallet.ui.component.OnboardingButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import cafe.adriel.voyager.core.registry.rememberScreen
import com.mangala.wallet.ui.SharedScreen
import dev.icerock.moko.resources.compose.stringResource

class ShowRecoveryPhraseScreen(
    private val walletId: String? = null
) : BaseScreen<ShowRecoveryPhraseScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_SHOW_RECOVERY_PHRASE
    override val screenClassName: String = ShowRecoveryPhraseScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ShowRecoveryPhraseScreenModel {
        return getScreenModel<ShowRecoveryPhraseScreenModel>(
            parameters = { org.koin.core.parameter.parametersOf(walletId) }
        )
    }

    @Composable
    override fun ScreenContent(screenModel: ShowRecoveryPhraseScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val clipboardManager = LocalClipboardManager.current
        val scope = rememberCoroutineScope()
        var showCopiedMessage by remember { mutableStateOf(false) }
        var isRecoveryPhraseRevealed by remember { mutableStateOf(false) }
        val lifecycleOwner = LocalLifecycleOwner.current
        val verifyRecoveryPhraseScreen = rememberScreen(SharedScreen.VerifyRecoveryPhraseScreen)

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                    isRecoveryPhraseRevealed = false
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
                isRecoveryPhraseRevealed = false
            }
        }

        SecureScreen {
            OnboardingGradientBackground(
            circleBackgroundEnabled = true,
            afterBackgroundModifier = Modifier.navigationBarsPadding().imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar with back button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .clickable { navigator.pop() }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.ArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Title and Description
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Back up your wallet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        letterSpacing = (-0.2).sp,
                        lineHeight = 28.sp,
                        fontFamily = getInterFontFamily()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Your secret recovery phrase is used to recover your crypto if you lose your phone or switch to a different wallet. Save these 12 words in a secure location, such as a password manager, and never share them with anyone.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFA5B4CB),
                        textAlign = TextAlign.Start,
                        letterSpacing = (-0.14).sp,
                        lineHeight = 19.6.sp,
                        fontFamily = getInterFontFamily()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Recovery Phrase Grid
                RecoveryPhraseGrid(
                    recoveryPhrase = uiState.recoveryPhrase,
                    isRevealed = isRecoveryPhraseRevealed,
                    onReveal = { isRecoveryPhraseRevealed = true }
                )

                if (!isRecoveryPhraseRevealed) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(MR.strings.warning_reveal_recovery_phrase),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFA5B4CB),
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp,
                        fontFamily = getInterFontFamily(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Copy to clipboard button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable(enabled = isRecoveryPhraseRevealed) {
                                val phraseText = uiState.recoveryPhrase.joinToString(" ")
                                clipboardManager.setText(AnnotatedString(phraseText))
                                showCopiedMessage = true
                                scope.launch {
                                    delay(2000)
                                    showCopiedMessage = false
                                }
                            }
                            .background(Color.Transparent)
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.Copy,
                            contentDescription = "Copy",
                            tint = if (isRecoveryPhraseRevealed) Color(0xFF3B90FF) else Color(0xFF6B7A99),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (showCopiedMessage) "Copied!" else "Copy to clipboard",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = if (isRecoveryPhraseRevealed) Color(0xFF3B90FF) else Color(0xFF6B7A99),
                            fontFamily = getInterFontFamily()
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Continue Button
                OnboardingButton(
                    text = "Continue",
                    onClick = {
                        screenModel.onContinueClick()
                        navigator.push(verifyRecoveryPhraseScreen)
                    },
                    isPrimary = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp)
                )
            }
        }
        } // SecureScreen
    }

    override val isBottomBarVisible: Boolean = false
}

@Composable
private fun RecoveryPhraseGrid(
    recoveryPhrase: List<String>,
    isRevealed: Boolean,
    onReveal: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = Color(0xFF2A3E6C),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.blur(if (isRevealed) 0.dp else 12.dp)
        ) {
            // First row (1-3)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PhraseItem(index = 1, word = recoveryPhrase.getOrElse(0) { "" }, modifier = Modifier.weight(1f))
                PhraseItem(index = 2, word = recoveryPhrase.getOrElse(1) { "" }, modifier = Modifier.weight(1f))
                PhraseItem(index = 3, word = recoveryPhrase.getOrElse(2) { "" }, modifier = Modifier.weight(1f))
            }

            // Second row (4-6)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PhraseItem(index = 4, word = recoveryPhrase.getOrElse(3) { "" }, modifier = Modifier.weight(1f))
                PhraseItem(index = 5, word = recoveryPhrase.getOrElse(4) { "" }, modifier = Modifier.weight(1f))
                PhraseItem(index = 6, word = recoveryPhrase.getOrElse(5) { "" }, modifier = Modifier.weight(1f))
            }

            // Third row (7-9)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PhraseItem(index = 7, word = recoveryPhrase.getOrElse(6) { "" }, modifier = Modifier.weight(1f))
                PhraseItem(index = 8, word = recoveryPhrase.getOrElse(7) { "" }, modifier = Modifier.weight(1f))
                PhraseItem(index = 9, word = recoveryPhrase.getOrElse(8) { "" }, modifier = Modifier.weight(1f))
            }

            // Fourth row (10-12)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PhraseItem(index = 10, word = recoveryPhrase.getOrElse(9) { "" }, modifier = Modifier.weight(1f))
                PhraseItem(index = 11, word = recoveryPhrase.getOrElse(10) { "" }, modifier = Modifier.weight(1f))
                PhraseItem(index = 12, word = recoveryPhrase.getOrElse(11) { "" }, modifier = Modifier.weight(1f))
            }
        }

        if (!isRevealed) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xB31A2237))
                    .clickable(onClick = onReveal),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(MR.strings.tap_to_reveal_recovery_phrase),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontFamily = getInterFontFamily()
                )
            }
        }
    }
}

@Composable
private fun PhraseItem(
    index: Int,
    word: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = index.toString(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFF1F5F9),
            fontFamily = getInterFontFamily(),
            modifier = Modifier.width(18.dp)
        )
        Text(
            text = word,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFF1F5F9),
            fontFamily = getInterFontFamily()
        )
    }
}
