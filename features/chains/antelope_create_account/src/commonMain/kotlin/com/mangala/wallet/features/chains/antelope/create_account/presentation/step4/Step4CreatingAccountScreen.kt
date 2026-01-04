package com.mangala.wallet.features.chains.antelope.create_account.presentation.step4

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step5.Step5BackupOptionsScreen
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.component.StepIndicator
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.coroutines.delay
import org.koin.core.parameter.parametersOf

class Step4CreatingAccountScreen(
    private val accountName: String,
    private val accountSuffix: String,
    private val operationType: SharedScreen.Step4CreatingAccountScreen.AccountOperationType = SharedScreen.Step4CreatingAccountScreen.AccountOperationType.CREATE
) : BaseScreen<Step4CreatingAccountScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_CREATE_ACCOUNT_CREATING
    override val screenClassName: String = Step4CreatingAccountScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): Step4CreatingAccountScreenModel {
        return getScreenModel<Step4CreatingAccountScreenModel> {
            parametersOf(
                accountName,
                accountSuffix,
                operationType
            )
        }
    }

    @Composable
    override fun ScreenContent(screenModel: Step4CreatingAccountScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        var contentVisible by remember { mutableStateOf(false) }

        // Start the creation process when screen loads
        LaunchedEffect(Unit) {
            screenModel.startAccountCreation()
            delay(100)
            contentVisible = true
        }

        LaunchedEffect(Unit) {
            screenModel.navigateToBackupScreen.collect {
                val nextScreen = when (operationType) {
                    SharedScreen.Step4CreatingAccountScreen.AccountOperationType.IMPORT -> {
                        ScreenRegistry.get(
                            SharedScreen.HomeScreen()
                        )
                    }

                    SharedScreen.Step4CreatingAccountScreen.AccountOperationType.CREATE -> {
                        Step5BackupOptionsScreen(
                            accountName = accountName,
                            accountSuffix = accountSuffix
                        )
                    }
                }

                navigator.replaceAll(nextScreen)
            }
        }

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

                    // Step indicator (3 steps completed for creating account)
                    StepIndicator(
                        totalSteps = 4,
                        currentStep = 3
                    )

                    Spacer(modifier = Modifier.size(40.dp)) // Balance the layout
                }

                Spacer(modifier = Modifier.height(120.dp))

                // Main content container with animation
                AnimatedVisibility(
                    visible = contentVisible,
                    enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) +
                            slideInVertically(
                                initialOffsetY = { it / 6 },
                                animationSpec = tween(600, delayMillis = 200)
                            )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1D263E))
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Header section
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Account icon
                                LocalImage(
                                    imageResource = MR.images.vaulta_account_name_default,
                                    modifier = Modifier.size(64.dp)
                                )

                                // Title
                                Text(
                                    text = when {
                                        uiState.hasError -> {
                                            if (operationType == SharedScreen.Step4CreatingAccountScreen.AccountOperationType.IMPORT) {
                                                "Account import failed"
                                            } else {
                                                "Account creation failed"
                                            }
                                        }

                                        uiState.isCompleted -> {
                                            if (operationType == SharedScreen.Step4CreatingAccountScreen.AccountOperationType.IMPORT) {
                                                "Account imported successfully!"
                                            } else {
                                                "Account created successfully!"
                                            }
                                        }

                                        else -> {
                                            if (operationType == SharedScreen.Step4CreatingAccountScreen.AccountOperationType.IMPORT) {
                                                "Importing your account..."
                                            } else {
                                                "Building your account..."
                                            }
                                        }
                                    },
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (uiState.hasError) Color(0xFFEF4444) else Color(
                                        0xFFF1F5F9
                                    ),
                                    letterSpacing = (-0.17).sp,
                                    lineHeight = 23.8.sp,
                                    fontFamily = getInterFontFamily()
                                )

                                // Error message
                                if (uiState.hasError) {
                                    Text(
                                        text = uiState.error ?: "An error occurred",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xFFEF4444),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        letterSpacing = (-0.14).sp,
                                        lineHeight = 19.6.sp,
                                        fontFamily = getInterFontFamily()
                                    )
                                }
                            }

                            // Progress bar
                            AnimatedVisibility(
                                visible = contentVisible,
                                enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) +
                                        slideInVertically(
                                            initialOffsetY = { it / 4 },
                                            animationSpec = tween(600, delayMillis = 400)
                                        )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(240.dp)
                                        .height(8.dp)
                                ) {
                                    // Background
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(100.dp))
                                            .background(Color(0xFF0A0E1A))
                                    )

                                    // Progress
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(uiState.progress)
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(100.dp))
                                            .background(Color(0xFF8647F3))
                                    )
                                }
                            }

                            // Loading steps with animation
                            AnimatedVisibility(
                                visible = contentVisible,
                                enter = fadeIn(animationSpec = tween(600, delayMillis = 600)) +
                                        slideInVertically(
                                            initialOffsetY = { it / 4 },
                                            animationSpec = tween(600, delayMillis = 600)
                                        )
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        if (operationType == SharedScreen.Step4CreatingAccountScreen.AccountOperationType.IMPORT) {
                                            LoadingStep(
                                                text = "Verifying account keys...",
                                                status = uiState.step1Status
                                            )

                                            LoadingStep(
                                                text = "Checking account status...",
                                                status = uiState.step2Status
                                            )

                                            LoadingStep(
                                                text = "Importing account data...",
                                                status = uiState.step3Status
                                            )
                                        } else {
                                            LoadingStep(
                                                text = "Generating security keys...",
                                                status = uiState.step1Status
                                            )

                                            LoadingStep(
                                                text = "Verifying purchase...",
                                                status = uiState.step2Status
                                            )

                                            LoadingStep(
                                                text = "Creating blockchain identity...",
                                                status = uiState.step3Status
                                            )
                                        }

                                        if (uiState.hasError) {
                                            Spacer(modifier = Modifier.height(16.dp))
                                            MangalaGradientButton(
                                                label = "Retry",
                                                onClick = { screenModel.retryAccountCreation() },
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override val isBottomBarVisible: Boolean = false
}

@Composable
private fun LoadingStep(
    text: String,
    status: StepStatus
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Status indicator
        Box(
            modifier = Modifier.size(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (status) {
                StepStatus.PENDING -> {
                    // Single circle for pending
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color(0xFFA5B4CB),
                            radius = size.minDimension / 2,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }

                StepStatus.IN_PROGRESS -> {
                    // Animated loading indicator
                    val infiniteTransition = rememberInfiniteTransition()
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(rotation)
                    ) {
                        // Outer circle
                        drawCircle(
                            color = Color(0xFFA5B4CB),
                            radius = size.minDimension / 2,
                            style = Stroke(width = 1.5.dp.toPx())
                        )

                        // Inner gradient arc
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFF8647F3),
                                    Color(0xFFA5B4CB),
                                    Color(0xFF8647F3)
                                )
                            ),
                            startAngle = -90f,
                            sweepAngle = 180f,
                            useCenter = false,
                            style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }

                StepStatus.COMPLETE -> {
                    // Purple check icon without background
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Complete",
                        tint = Color(0xFF8647F3),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Text
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = when (status) {
                StepStatus.COMPLETE -> Color(0xFFF1F5F9)
                else -> Color(0xFFA5B4CB)
            },
            letterSpacing = (-0.12).sp,
            lineHeight = 16.8.sp,
            fontFamily = getInterFontFamily()
        )
    }
}

enum class StepStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETE
}