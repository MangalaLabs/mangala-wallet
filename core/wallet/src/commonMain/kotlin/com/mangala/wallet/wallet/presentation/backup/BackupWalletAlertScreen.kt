package com.mangala.wallet.wallet.presentation.backup

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.OnboardingButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

data class BackupWalletAlertScreen(
    val blockchainUid: String,
    val antelopeAccountName: String?
) : BaseScreen<BackupWalletAlertScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.BACKUP_WALLET_ALERT
    override val screenClassName: String = BackupWalletAlertScreen::class.simpleName.orEmpty()
    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): BackupWalletAlertScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: BackupWalletAlertScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val globalNavigator = LocalGlobalNavigator.current
        val showRiskDialog by screenModel.showRiskDialog.collectAsStateMultiplatform()

        val title = MR.strings.title_backup_wallet_alert.desc().localized()
        val fullMessage = MR.strings.message_backup_wallet_alert.desc().localized()
        val backupNowText = MR.strings.backup_now.desc().localized()
        val riskText = MR.strings.i_will_risk_it.desc().localized()

        val messageParts = fullMessage.split("\n").filter { it.isNotBlank() }
        val warningParagraph1 = messageParts.getOrElse(0) { fullMessage }
        val warningParagraph2 = messageParts.getOrElse(1) { "" }

        val homeScreen = rememberScreen(SharedScreen.HomeScreen())

        // Determine unlock PIN case based on network type
        val networkType = BlockchainType.fromUid(blockchainUid).networkType
        val unlockPinCase = when (networkType) {
            NetworkType.EVM -> SharedScreen.UnlockPinScreen.SHOW_WORDS_PHRASE
            NetworkType.ANTELOPE -> SharedScreen.UnlockPinScreen.BACKUP_ANTELOPE_ACCOUNT
            else -> SharedScreen.UnlockPinScreen.SHOW_WORDS_PHRASE
        }
        val unlockPinScreen = ScreenRegistry.get(
            SharedScreen.UnlockPinScreen(unlockPinCase, antelopeAccountName = antelopeAccountName)
        )

        val infiniteTransition = rememberInfiniteTransition()
        val mascotBounceY by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -24f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = EaseInOut),
                repeatMode = RepeatMode.Reverse
            )
        )

        OnboardingGradientBackground(
            circleBackgroundEnabled = true,
            afterBackgroundModifier = Modifier.navigationBarsPadding().imePadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with back button
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

                // Title and warning text
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = getInterFontFamily(),
                        lineHeight = 38.sp,
                        letterSpacing = (-0.32).sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = warningParagraph1,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.6f),
                        fontFamily = getInterFontFamily(),
                        lineHeight = 24.sp,
                        letterSpacing = (-0.17).sp
                    )

                    if (warningParagraph2.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = warningParagraph2,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.6f),
                            fontFamily = getInterFontFamily(),
                            lineHeight = 24.sp,
                            letterSpacing = (-0.17).sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Mascot with gentle bounce animation
                LocalImage(
                    imageResource = MR.images.character,
                    modifier = Modifier
                        .size(140.dp)
                        .graphicsLayer {
                            translationY = mascotBounceY
                        }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Bottom section: buttons + security indicator
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Primary: "Backup now"
                    OnboardingButton(
                        text = backupNowText,
                        onClick = { navigator.push(unlockPinScreen) },
                        isPrimary = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Secondary: "I will risk it"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(1000.dp))
                            .background(Color.White.copy(alpha = 0.1f))
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(1000.dp)
                            )
                            .clickable { screenModel.onRiskButtonClicked() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = riskText,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontFamily = getInterFontFamily(),
                            letterSpacing = (-0.17).sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Security indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "\uD83D\uDD12",
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "PIN locked and encrypted",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.6f),
                            fontFamily = getInterFontFamily()
                        )
                    }
                }
            }
        }

        // Risk confirmation dialog
        if (showRiskDialog) {
            RiskConfirmationDialog(
                onDismiss = { screenModel.dismissRiskDialog() },
                onBackupNow = {
                    screenModel.dismissRiskDialog()
                    navigator.push(unlockPinScreen)
                },
                onUnderstandRisk = {
                    screenModel.dismissRiskDialog()
                    globalNavigator.replaceAll(homeScreen)
                }
            )
        }
    }
}

@Composable
private fun RiskConfirmationDialog(
    onDismiss: () -> Unit,
    onBackupNow: () -> Unit,
    onUnderstandRisk: () -> Unit
) {
    val dialogTitle = MR.strings.title_risk_confirmation.desc().localized()
    val dialogMessage = MR.strings.message_risk_confirmation.desc().localized()
    val backUpNowText = MR.strings.back_up_now.desc().localized()
    val understandRiskText = MR.strings.button_understand_risk.desc().localized()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF1A1E3D))
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Warning icon in amber circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\u26A0\uFE0F",
                    fontSize = 28.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = dialogTitle,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = getInterFontFamily(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = dialogMessage,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.6f),
                fontFamily = getInterFontFamily(),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                letterSpacing = (-0.17).sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Primary: "Back up now"
            OnboardingButton(
                text = backUpNowText,
                onClick = onBackupNow,
                isPrimary = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Text button: "I understand the risk"
            Text(
                text = understandRiskText,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.6f),
                fontFamily = getInterFontFamily(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onUnderstandRisk
                    )
                    .padding(vertical = 12.dp)
            )
        }
    }
}
