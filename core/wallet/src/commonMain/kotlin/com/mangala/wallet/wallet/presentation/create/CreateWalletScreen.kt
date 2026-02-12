package com.mangala.wallet.wallet.presentation.create

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.sp
import androidx.compose.material.AlertDialog
import androidx.compose.material.TextButton
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaButtonStyle
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow

class CreateWalletScreen(
    private val blockchainUid: String? = null,
    private val antelopeAccountName: String? = null,
    private val listString: List<String>? = null,
    private val name: String? = null,
    private val createWalletCase: SharedScreen.CreateWalletScreen.CreateWalletScreenCase
) : BaseScreen<CreateWalletScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_CREATE_WALLET
    override val screenClassName: String = CreateWalletScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): CreateWalletScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: CreateWalletScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val homeScreen = rememberScreen(SharedScreen.HomeScreen())
        val walletState = screenModel.state.collectAsStateMultiplatform().value

        // Error dialog
        if (walletState is CreateWalletState.Error) {
            AlertDialog(
                onDismissRequest = { screenModel.dismissError() },
                title = {
                    Text(
                        text = MR.strings.all_error_no_params.desc().localized(),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                text = {
                    Text(
                        text = walletState.message,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        screenModel.dismissError()
                        navigator.pop()
                    }) {
                        Text(
                            text = MR.strings.all_ok.desc().localized(),
                            color = Color(0xFF3B90FF)
                        )
                    }
                },
                backgroundColor = Color(0xFF1E1F32),
                contentColor = Color.White
            )
        }

        when (createWalletCase) {
            SharedScreen.CreateWalletScreen.CreateWalletScreenCase.CREATE_NEW_WALLET -> {
                val blockchainUid = blockchainUid ?: ""
                val antelopeAccountName = antelopeAccountName

                LaunchedEffect(Unit) {
                    screenModel.createWallet(blockchainUid, antelopeAccountName)
                }

                val backupWalletAlertScreen = rememberScreen(
                    SharedScreen.BackupWalletAlertScreen(
                        blockchainUid,
                        antelopeAccountName
                    )
                )
                GenerateWalletAnimation {
                    navigator.replaceAll(backupWalletAlertScreen)
                }
            }

            SharedScreen.CreateWalletScreen.CreateWalletScreenCase.IMPORT_NEW_ACCOUNT -> {
                val blockchainUid = blockchainUid ?: ""
                val antelopeAccountName = antelopeAccountName

                LaunchedEffect(Unit) {
                    screenModel.createWallet(blockchainUid, antelopeAccountName)
                }

                GenerateWalletAnimation {
                    navigator.replaceAll(homeScreen)
                }
            }

            SharedScreen.CreateWalletScreen.CreateWalletScreenCase.IMPORT_WALLET -> {
                val listString = listString ?: return
                val name = name ?: ""

                GenerateWalletAnimation {
                    screenModel.restoreWallet(listString, name)
                }
                LaunchedEffect(Unit) {
                    screenModel.onCreateDone.receiveAsFlow().collectLatest {
                        navigator.replaceAll(homeScreen)
                    }
                }
            }
        }
    }

}

@Composable
fun GenerateWalletAnimation(onClickStart: () -> Unit) {
    val step1 = MR.strings.create_wallet_step_1.desc().localized()
    val step2 = MR.strings.create_wallet_step_2.desc().localized()
    val step3 = MR.strings.create_wallet_step_3.desc().localized()
    val step4 = MR.strings.create_wallet_step_4.desc().localized()
    val steps = remember(step1, step2, step3, step4) {
        listOf(step1, step2, step3, step4)
    }

    var currentStep by remember { mutableIntStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }

    // Pulse animation for the button when completed
    val pulseScale = remember { Animatable(1f) }

    // Mascot animations - reacts to progress
    val mascotScale = remember { Animatable(1f) }
    val mascotRotation = remember { Animatable(0f) }
    val mascotBounce = remember { Animatable(0f) }

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

    // Celebration pulse animation when completed
    LaunchedEffect(isCompleted) {
        if (isCompleted) {
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

    OnboardingGradientBackground(
        afterBackgroundModifier = Modifier.safeDrawingPadding().imePadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

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

            // Title and subtitle - outside the card
            Text(
                text = if (isCompleted) MR.strings.create_wallet_title_ready.desc().localized() else MR.strings.create_wallet_title_generating.desc().localized(),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = getInterFontFamily(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isCompleted) MR.strings.create_wallet_subtitle_ready.desc().localized() else MR.strings.create_wallet_subtitle_generating.desc().localized(),
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = if (isCompleted) Color(0xFF22C55E) else Color(0xFFA5B4CB),
                fontFamily = getInterFontFamily(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Progress card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.25f),
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1E1F32).copy(alpha = 0.95f),
                                Color(0xFF14152A).copy(alpha = 0.98f)
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
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    // Progress steps
                    steps.forEachIndexed { index, step ->
                        val isLastStep = index == steps.lastIndex
                        val stepCompleted = currentStep > index
                        val stepInProgress = currentStep == index

                        val shouldDim = isCompleted && stepCompleted && !isLastStep

                        GeneratingStepItem(
                            stepText = step,
                            isCompleted = stepCompleted,
                            isInProgress = stepInProgress,
                            isDimmed = shouldDim,
                            isHighlighted = isCompleted && isLastStep
                        )
                        if (index < steps.lastIndex) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Button - using design system component
            MangalaGradientButton(
                label = if (isCompleted) MR.strings.create_wallet_button_start.desc().localized() else MR.strings.create_wallet_button_setting_up.desc().localized(),
                onClick = onClickStart,
                enabled = isCompleted,
                buttonStyle = MangalaButtonStyle.GRADIENT,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .graphicsLayer {
                        scaleX = pulseScale.value
                        scaleY = pulseScale.value
                    }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun GeneratingStepItem(
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
            fontSize = 15.sp,
            fontWeight = when {
                isHighlighted -> FontWeight.SemiBold
                isCompleted || isInProgress -> FontWeight.Medium
                else -> FontWeight.Normal
            },
            color = when {
                isCompleted || isInProgress || isHighlighted -> Color.White
                else -> Color(0xFF6B7280)
            },
            fontFamily = getInterFontFamily(),
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Status indicator on the right
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(24.dp)
        ) {
            when {
                isCompleted -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = if (isHighlighted) Color(0xFF4ADE80) else Color(0xFF22C55E),
                        modifier = Modifier.size(22.dp)
                    )
                }
                isInProgress -> {
                    CircularProgressIndicator(
                        color = Color(0xFF8B5CF6),
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

