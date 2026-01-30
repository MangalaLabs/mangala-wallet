package com.mangala.wallet.wallet.presentation.restore

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.FontSizeNew
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.delay

class ImportWalletSuccessScreen(
    private val mnemonicWords: List<String>,
    private val walletName: String
) : BaseScreen<ImportWalletSuccessScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_IMPORT_WALLET_SUCCESS
    override val screenClassName: String = ImportWalletSuccessScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): ImportWalletSuccessScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: ImportWalletSuccessScreenModel) {
        val globalNavigator = LocalGlobalNavigator.current
        val homeScreen = rememberScreen(SharedScreen.HomeScreen())

        ImportWalletAnimation(
            onAnimationComplete = {
                // Start restore in background
                screenModel.restoreWallet(mnemonicWords, walletName)
            },
            onNavigateToHome = {
                globalNavigator.replaceAll(homeScreen)
            }
        )
    }
}

@Composable
fun ImportWalletAnimation(
    onAnimationComplete: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val steps = listOf(
        stringResource(MR.strings.step_import_wallet_verifying),
        stringResource(MR.strings.step_import_wallet_deriving),
        stringResource(MR.strings.step_import_wallet_encrypting),
        stringResource(MR.strings.step_import_wallet_restoring)
    )

    var currentStep by remember { mutableIntStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }

    // Pulse animation for the button when completed
    val pulseScale = remember { Animatable(1f) }

    // Mascot animations - reacts to progress
    val mascotScale = remember { Animatable(1f) }
    val mascotRotation = remember { Animatable(0f) }
    val mascotBounce = remember { Animatable(0f) }

    // Bottom navigation bar padding
    val bottomNavBarPadding = WindowInsets.navigationBars

    LaunchedEffect(Unit) {
        for (i in steps.indices) {
            delay(1200L)
            currentStep = i + 1

            // Mascot reaction for each step completion
            when (i) {
                0 -> {
                    // Step 1: Small bounce
                    mascotScale.animateTo(1.08f, tween(100))
                    mascotScale.animateTo(1f, tween(150))
                }
                1 -> {
                    // Step 2: Slight tilt (wink effect)
                    mascotRotation.animateTo(5f, tween(100))
                    mascotRotation.animateTo(-3f, tween(100))
                    mascotRotation.animateTo(0f, tween(100))
                }
                2 -> {
                    // Step 3: Another bounce
                    mascotScale.animateTo(1.1f, tween(100))
                    mascotScale.animateTo(1f, tween(150))
                }
                3 -> {
                    // Step 4 (final): Celebration bounce
                    mascotBounce.animateTo(-15f, tween(150))
                    mascotBounce.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                    mascotScale.animateTo(1.15f, tween(200, easing = FastOutSlowInEasing))
                    mascotScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                }
            }
        }
        delay(300L)
        isCompleted = true
    }

    // Celebration pulse animation when completed and trigger restore
    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            // Start restore when animation completes
            onAnimationComplete()

            // Pulse animation: scale up then back to normal
            pulseScale.animateTo(
                targetValue = 1.05f,
                animationSpec = tween(durationMillis = 150)
            )
            pulseScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 150)
            )
        }
    }

    OnboardingGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Dimensions.Padding.extraLarge))

            // Character image with reactive animations
            LocalImage(
                imageResource = MR.images.character,
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        scaleX = mascotScale.value
                        scaleY = mascotScale.value
                        rotationZ = mascotRotation.value
                        translationY = mascotBounce.value
                    }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Title
            Text(
                text = if (isCompleted) {
                    stringResource(MR.strings.title_import_wallet_success_welcome)
                } else {
                    stringResource(MR.strings.title_import_wallet_success_restoring)
                },
                fontSize = FontSizeNew.LARGE_TITLE,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = getInterFontFamily(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.Padding.large)
            )

            Spacer(modifier = Modifier.height(Dimensions.Padding.half))

            // Subtitle
            Text(
                text = if (isCompleted) {
                    stringResource(MR.strings.message_import_wallet_success_complete)
                } else {
                    stringResource(MR.strings.message_import_wallet_success_restoring)
                },
                fontSize = FontSizeNew.BODY,
                fontWeight = FontWeight.Normal,
                color = if (isCompleted) ImportWalletColors.success else ImportWalletColors.textSecondary,
                fontFamily = getInterFontFamily(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.Padding.large)
            )

            Spacer(modifier = Modifier.height(Dimensions.Padding.large))

            // Progress card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.Padding.large)
                    .clip(RoundedCornerShape(CornerRadius.BottomSheet))
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.25f),
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(CornerRadius.BottomSheet)
                    )
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                ImportWalletColors.cardBackgroundTop,
                                ImportWalletColors.cardBackgroundBottom
                            )
                        )
                    )
                    .drawBehind {
                        drawLine(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.White.copy(alpha = 0.15f),
                                    Color.White.copy(alpha = 0.2f),
                                    Color.White.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            ),
                            start = Offset(size.width * 0.1f, 1f),
                            end = Offset(size.width * 0.9f, 1f),
                            strokeWidth = 1.5f
                        )
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimensions.Padding.large, vertical = Dimensions.Padding.xDefault)
                ) {
                    // Progress steps
                    steps.forEachIndexed { index, step ->
                        val isLastStep = index == steps.lastIndex
                        val stepCompleted = currentStep > index
                        val stepInProgress = currentStep == index

                        val shouldDim = isCompleted && stepCompleted && !isLastStep

                        ImportStepItem(
                            stepText = step,
                            isCompleted = stepCompleted,
                            isInProgress = stepInProgress,
                            isDimmed = shouldDim,
                            isHighlighted = isCompleted && isLastStep
                        )
                        if (index < steps.lastIndex) {
                            Spacer(modifier = Modifier.height(Dimensions.Padding.default))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.Padding.double))

            // Button - pill shape matching design system
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimensions.Padding.large)
                    .graphicsLayer {
                        scaleX = pulseScale.value
                        scaleY = pulseScale.value
                    }
            ) {
                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimensions.Height.xxxxLarge)
                            .clip(RoundedCornerShape(1000.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        ImportWalletColors.buttonGradientStart,
                                        ImportWalletColors.buttonGradientEnd
                                    )
                                )
                            )
                            .clickable(onClick = onNavigateToHome),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(MR.strings.button_import_wallet_go_to_wallet),
                            fontSize = FontSizeNew.LARGE_BODY,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontFamily = getInterFontFamily()
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimensions.Height.xxxxLarge)
                            .clip(RoundedCornerShape(1000.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        ImportWalletColors.buttonGradientStart.copy(alpha = 0.2f),
                                        ImportWalletColors.buttonGradientEnd.copy(alpha = 0.2f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = ImportWalletColors.progressIndicator,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(Dimensions.Width.mediumLarge)
                            )
                            Spacer(modifier = Modifier.width(Dimensions.Padding.small))
                            Text(
                                text = stringResource(MR.strings.button_import_wallet_restoring),
                                fontSize = FontSizeNew.LARGE_BODY,
                                fontWeight = FontWeight.SemiBold,
                                color = ImportWalletColors.buttonDisabledText,
                                fontFamily = getInterFontFamily()
                            )
                        }
                    }
                }
            }

            // Safe area padding for bottom navigation bar
            Spacer(modifier = Modifier.height(Dimensions.Padding.small))
            Spacer(modifier = Modifier.windowInsetsPadding(bottomNavBarPadding))
        }
    }
}

@Composable
private fun ImportStepItem(
    stepText: String,
    isCompleted: Boolean,
    isInProgress: Boolean,
    isDimmed: Boolean = false,
    isHighlighted: Boolean = false
) {
    val itemAlpha = when {
        isDimmed -> 0.5f
        else -> 1f
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(itemAlpha)
    ) {
        // Step text on the left
        Text(
            text = stepText,
            fontSize = FontSizeNew.BODY,
            fontWeight = when {
                isHighlighted -> FontWeight.SemiBold
                isCompleted || isInProgress -> FontWeight.Medium
                else -> FontWeight.Normal
            },
            color = when {
                isCompleted || isInProgress || isHighlighted -> Color.White
                else -> ImportWalletColors.textTertiary
            },
            fontFamily = getInterFontFamily(),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(Dimensions.Padding.small))

        // Status indicator on the right
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(Dimensions.IconButtonSize)
        ) {
            when {
                isCompleted -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isHighlighted) ImportWalletColors.successHighlight else ImportWalletColors.success,
                        modifier = Modifier.size(22.dp)
                    )
                }
                isInProgress -> {
                    CircularProgressIndicator(
                        color = ImportWalletColors.progressIndicatorActive,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(Dimensions.Width.mediumLarge)
                    )
                }
            }
        }
    }
}

/**
 * Color constants for Import Wallet Success Screen
 */
private object ImportWalletColors {
    val success = Color(0xFF22C55E)
    val successHighlight = Color(0xFF4ADE80)
    val textSecondary = Color(0xFFA5B4CB)
    val textTertiary = Color(0xFF6B7280)
    val cardBackgroundTop = Color(0xFF1E1F32).copy(alpha = 0.95f)
    val cardBackgroundBottom = Color(0xFF14152A).copy(alpha = 0.98f)
    val buttonGradientStart = Color(0xFF3B90FF)
    val buttonGradientEnd = Color(0xFFC27DFF)
    val buttonDisabledText = Color(0xFF94A3B8)
    val progressIndicator = Color(0xFF6B7280)
    val progressIndicatorActive = Color(0xFF8B5CF6)
}
