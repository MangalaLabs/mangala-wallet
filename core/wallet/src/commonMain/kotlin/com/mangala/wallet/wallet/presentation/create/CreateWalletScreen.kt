package com.mangala.wallet.wallet.presentation.create

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
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
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

        when (createWalletCase) {
            SharedScreen.CreateWalletScreen.CreateWalletScreenCase.CREATE_NEW_WALLET -> {
                val blockchainUid = blockchainUid ?: ""
                val antelopeAccountName = antelopeAccountName

                screenModel.createWallet(blockchainUid, antelopeAccountName)

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

                screenModel.createWallet(blockchainUid, antelopeAccountName)

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
                LaunchedEffect(true) {
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
    val steps = listOf(
        "Generating secure key pairs...",
        "Encrypting with PIN...",
        "Saving to local vault",
        "Preparing your space in multichain..."
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

    OnboardingGradientBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
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
                text = if (isCompleted) "Your Wallet is Ready!" else "Generating Your Mangala Wallet",
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
                text = if (isCompleted) "Tap below to start to journey" else "This might take a few moments",
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

            // Button - pill shape matching design system
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .graphicsLayer {
                        scaleX = pulseScale.value
                        scaleY = pulseScale.value
                    }
            ) {
                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(1000.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF3B90FF),
                                        Color(0xFFC27DFF)
                                    )
                                )
                            )
                            .clickable(onClick = onClickStart),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "START YOUR JOURNEY",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            letterSpacing = (-0.17).sp,
                            fontFamily = getInterFontFamily()
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(1000.dp))
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF3B90FF).copy(alpha = 0.2f),
                                        Color(0xFFC27DFF).copy(alpha = 0.2f)
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
                                color = Color(0xFF6B7280),
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "SETTING UP...",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF94A3B8),
                                letterSpacing = (-0.17).sp,
                                fontFamily = getInterFontFamily()
                            )
                        }
                    }
                }
            }

            // Safe area padding for bottom navigation bar
            Spacer(modifier = Modifier.height(12.dp))
            Spacer(modifier = Modifier.windowInsetsPadding(bottomNavBarPadding))
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

