package com.mangala.wallet.wallet.presentation.reset

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.PlatformType
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.getPlatform
import com.mangala.wallet.wallet.presentation.reset.model.ResetState
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.delay

class ResetWalletScreenV2 : BaseScreen<ResetWalletScreenModel>() {
    @Composable
    override fun createScreenModel(): ResetWalletScreenModel = getScreenModel()

    override val screenName: String
        get() = MangalaAnalytics.Screens.EVM_RESET_WALLET

    override val screenClassName: String
        get() = ResetWalletScreenV2::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun ScreenContent(screenModel: ResetWalletScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val globalNavigator = LocalGlobalNavigator.current
        val (showConfirmation, onShowConfirmationChange) = remember { mutableStateOf(false) }
        val resetState by screenModel.resetState.collectAsState()

        val onboardingScreen = remember {
            ScreenRegistry.get(SharedScreen.OnboardingScreen)
        }

        LaunchedEffect(resetState) {
            if (resetState is ResetState.Success) {
                globalNavigator.replaceAll(onboardingScreen)
            }
        }

        ResetWalletScreenV2Content(
            onBackClicked = navigator::pop,
            onResetConfirmed = {
                screenModel.resetWallet()
            },
            showConfirmation = showConfirmation,
            onShowConfirmation = onShowConfirmationChange,
            resetState = resetState
        )
    }
}

@Composable
private fun ResetWalletScreenV2Content(
    onBackClicked: () -> Unit,
    onResetConfirmed: () -> Unit,
    showConfirmation: Boolean,
    onShowConfirmation: (Boolean) -> Unit,
    resetState: ResetState
) {
    val platformTexts = getResetWalletPlatformSpecificTexts()
    var contentVisible by remember { mutableStateOf(false) }
    var isChecked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        contentVisible = true
    }

    OnboardingGradientBackground {
        MaxSizeColumn(
            modifier = Modifier
                .safeDrawingPadding(),
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                MangalaWalletTopBarCenteredTitle(
                    title = "",
                    onBackClicked = onBackClicked
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = platformTexts.title,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Start,
                        letterSpacing = (-0.28).sp,
                        lineHeight = 39.2.sp,
                        fontFamily = getInterFontFamily()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = platformTexts.subtitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFEF4444),
                        textAlign = TextAlign.Start,
                        letterSpacing = (-0.18).sp,
                        lineHeight = 25.2.sp,
                        fontFamily = getInterFontFamily()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = platformTexts.description,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFA5B4CB),
                        textAlign = TextAlign.Start,
                        letterSpacing = (-0.16).sp,
                        lineHeight = 22.4.sp,
                        fontFamily = getInterFontFamily()
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 300)) +
                            slideInVertically(
                                initialOffsetY = { it / 4 },
                                animationSpec = tween(600, delayMillis = 300)
                            ),
                    exit = fadeOut(animationSpec = tween(400)) +
                            slideOutVertically(
                                targetOffsetY = { it },
                                animationSpec = tween(400)
                            )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        WarningItem(
                            icon = "💰",
                            text = platformTexts.warning1
                        )

                        WarningItem(
                            icon = "🔑",
                            text = platformTexts.warning2
                        )

                        WarningItem(
                            icon = "♻️",
                            text = platformTexts.warning3
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(animationSpec = tween(600, delayMillis = 450)) +
                        slideInVertically(
                            initialOffsetY = { it / 3 },
                            animationSpec = tween(600, delayMillis = 450)
                        ),
                exit = fadeOut(animationSpec = tween(400)) +
                        slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(400)
                        )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 24.dp,
                            start = 24.dp,
                            end = 24.dp,
                            bottom = 8.dp,
                        ),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                            .clickable { isChecked = !isChecked }
                            .padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { isChecked = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF3B90FF),
                                uncheckedColor = Color(0xFF64748B),
                                checkmarkColor = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = platformTexts.confirmText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFFA5B4CB),
                            letterSpacing = (-0.14).sp,
                            lineHeight = 19.6.sp,
                            fontFamily = getInterFontFamily(),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    val buttonAlpha by animateFloatAsState(
                        targetValue = if (isChecked) 1f else 0.5f,
                        animationSpec = tween(300)
                    )

                    Button(
                        onClick = {
                            if (isChecked && resetState !is ResetState.Loading) {
                                onShowConfirmation(true)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .alpha(buttonAlpha),
                        enabled = isChecked && resetState !is ResetState.Loading,
                        shape = RoundedCornerShape(28.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(0xFFEF4444),
                            disabledBackgroundColor = Color(0xFFEF4444).copy(alpha = 0.5f)
                        )
                    ) {
                        if (resetState is ResetState.Loading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = platformTexts.buttonText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                letterSpacing = (-0.16).sp,
                                fontFamily = getInterFontFamily()
                            )
                        }
                    }
                }
            }
        }

        if (showConfirmation) {
            ConfirmationDialog(
                onConfirm = {
                    onShowConfirmation(false)
                    onResetConfirmed()
                },
                onDismiss = {
                    onShowConfirmation(false)
                }
            )
        }
    }
}

@Composable
private fun WarningItem(
    icon: String,
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFEF4444).copy(alpha = 0.1f),
                            Color(0xFFDC2626).copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 12.dp)
                )

                Text(
                    text = text,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFFF1F5F9),
                    letterSpacing = (-0.14).sp,
                    lineHeight = 19.6.sp,
                    fontFamily = getInterFontFamily(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFF1E293B),
        title = {
            Text(
                text = "Final Confirmation",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontFamily = getInterFontFamily()
            )
        },
        text = {
            Text(
                text = "This action cannot be undone. Your wallet will be permanently deleted.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFA5B4CB),
                lineHeight = 22.4.sp,
                fontFamily = getInterFontFamily()
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFEF4444)
                )
            ) {
                Text(
                    text = "Reset Wallet",
                    fontWeight = FontWeight.Medium,
                    fontFamily = getInterFontFamily()
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF64748B)
                )
            ) {
                Text(
                    text = "Cancel",
                    fontWeight = FontWeight.Medium,
                    fontFamily = getInterFontFamily()
                )
            }
        }
    )
}

data class ResetWalletScreenTexts(
    val title: String,
    val subtitle: String,
    val description: String,
    val warning1: String,
    val warning2: String,
    val warning3: String,
    val confirmText: String,
    val buttonText: String
)

@Composable
fun getResetWalletPlatformSpecificTexts(): ResetWalletScreenTexts {
    val baseTitle = MR.strings.reset_wallet.desc().localized()
    val baseSubtitle = MR.strings.title_reset_wallet.desc().localized()
    val baseDescription = MR.strings.message_reset_wallet.desc().localized()
    val baseConfirm = MR.strings.confirm_reset_wallet.desc().localized()

    return when (getPlatform().type) {
        PlatformType.ANDROID -> ResetWalletScreenTexts(
            title = baseTitle,
            subtitle = "⚠️ This is permanent!",
            description = baseDescription,
            warning1 = "All your accounts and balances will be lost forever",
            warning2 = "Your recovery phrase is the ONLY way to restore",
            warning3 = "This action cannot be undone or reversed",
            confirmText = baseConfirm,
            buttonText = "Reset My Wallet 🔄"
        )

        PlatformType.IOS -> ResetWalletScreenTexts(
            title = baseTitle,
            subtitle = baseSubtitle,
            description = baseDescription,
            warning1 = "All wallet data will be permanently erased",
            warning2 = "Ensure you have saved your recovery phrase",
            warning3 = "You will need to set up a new wallet",
            confirmText = baseConfirm,
            buttonText = "Reset Wallet"
        )

        PlatformType.DESKTOP -> ResetWalletScreenTexts(
            title = "Factory Reset",
            subtitle = "Permanent Data Deletion",
            description = baseDescription,
            warning1 = "Complete removal of all wallet data",
            warning2 = "Recovery phrase required for restoration",
            warning3 = "Irreversible operation",
            confirmText = baseConfirm,
            buttonText = "Proceed with Reset"
        )
    }
}