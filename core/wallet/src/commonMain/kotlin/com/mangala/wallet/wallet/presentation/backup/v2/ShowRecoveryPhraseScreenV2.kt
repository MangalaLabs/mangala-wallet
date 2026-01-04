package com.mangala.wallet.wallet.presentation.backup.v2

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.ui.component.OnboardingButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import cafe.adriel.voyager.core.registry.rememberScreen
import com.mangala.wallet.ui.SharedScreen

class ShowRecoveryPhraseScreenV2 : BaseScreen<ShowRecoveryPhraseScreenModelV2>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_SHOW_RECOVERY_PHRASE
    override val screenClassName: String = ShowRecoveryPhraseScreenV2::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ShowRecoveryPhraseScreenModelV2 {
        return getScreenModel<ShowRecoveryPhraseScreenModelV2>()
    }

    @Composable
    override fun ScreenContent(screenModel: ShowRecoveryPhraseScreenModelV2) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val clipboardManager = LocalClipboardManager.current
        val scope = rememberCoroutineScope()
        var showCopiedMessage by remember { mutableStateOf(false) }
        val verifyRecoveryPhraseScreenV2 = rememberScreen(SharedScreen.VerifyRecoveryPhraseScreenV2)
        
        OnboardingGradientBackground(
            circleBackgroundEnabled = true,
            afterBackgroundModifier = Modifier.navigationBarsPadding().imePadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar with progress dots
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
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
                    
                    // Progress dots (4 dots, all filled for final step)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(4) { index ->
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
                    }
                    
                    Spacer(modifier = Modifier.size(40.dp)) // Balance the layout
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
                    recoveryPhrase = uiState.recoveryPhrase
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Copy to clipboard button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable {
                                val phraseText = uiState.recoveryPhrase.joinToString(" ")
                                clipboardManager.setText(AnnotatedString(phraseText))
                                showCopiedMessage = true
                                scope.launch {
                                    delay(2000)
                                    showCopiedMessage = false
                                }
                            }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.Copy,
                            contentDescription = "Copy",
                            tint = Color(0xFF3B90FF),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = if (showCopiedMessage) "Copied!" else "Copy to clipboard",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF3B90FF),
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
                        navigator.push(verifyRecoveryPhraseScreenV2)
                    },
                    isPrimary = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp)
                )
            }
        }
    }

    override val isBottomBarVisible: Boolean = false
}

@Composable
private fun RecoveryPhraseGrid(
    recoveryPhrase: List<String>
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
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
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