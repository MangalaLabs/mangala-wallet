package com.mangala.wallet.auth.presentation.signin

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.auth.presentation.RegisterScreen
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcBack
import com.mangala.wallet.core.auth.domain.model.AuthState
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.PasskeyBottomSheet
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * AI Assistant Sign In Screen with passkey authentication
 */
class SignInScreen(private val showTokenExpiredMessage: Boolean = false) : BaseScreen<SignInScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.LOGIN
    override val screenClassName: String = SignInScreen::class.simpleName.toString()

    @Composable
    override fun createScreenModel(): SignInScreenModel {
        return getScreenModel<SignInScreenModel>()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ScreenContent(screenModel: SignInScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val globalNavigator = LocalGlobalNavigator.current
        val state by screenModel.authState.collectAsState()
        val showHelpDialog by screenModel.showHelpDialog.collectAsState()
        val emailError by screenModel.emailError.collectAsState()
        val navigateToRegistration by screenModel.navigateToRegistration.collectAsState()
        val scope = rememberCoroutineScope()
        val hapticFeedback = LocalHapticFeedback.current
        val bottomSheetState = rememberModalBottomSheetState()

        var isAuthenticated by remember { mutableStateOf(false) }

        LaunchedEffect(state) {
            if (state is AuthState.Authenticated) {
                isAuthenticated = true
                println("Auth successful, attempting navigation to ConversationUiScreen")

                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)

                delay(1500) // Show success animation

                try {
                    val conversationScreen = ScreenRegistry.get(SharedScreen.ConversationSessionListScreen)
                    navigator.replace(conversationScreen)
                } catch (e: Exception) {
                    println("Error navigating to ConversationUiScreen: ${e.message}")
                    e.printStackTrace()
                    screenModel.navigateToConversationUi()
                }
            }
        }

        LaunchedEffect(navigateToRegistration) {
            if (navigateToRegistration) {
                println("SignInScreen: Navigating to RegisterScreen for registration")
                navigator.push(RegisterScreen())
                screenModel.onNavigationHandled()
            }
        }

        Scaffold { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                when (state) {
                    is AuthState.Initial -> {
                        SignInContent(
                            onPasskeyClick = {
                                scope.launch {
                                    screenModel.authenticateWithoutEmail()
                                }
                            },
                            onHelpClick = { screenModel.showPasskeyHelp() },
                            onSignUpClick = {
                                navigator.push(RegisterScreen())
                            },
                            onBack = if (navigator.canPop) {
                                {
                                    navigator.pop()
                                }
                            } else null,
                            isLoading = false,
                            errorMessage = emailError,
                            showTokenExpiredMessage = showTokenExpiredMessage
                        )
                    }

                    is AuthState.Loading -> {
                        SignInContent(
                            onPasskeyClick = {
                                scope.launch {
                                    screenModel.authenticateWithoutEmail()
                                }
                            },
                            onHelpClick = { screenModel.showPasskeyHelp() },
                            onSignUpClick = {
                                navigator.push(RegisterScreen())
                            },
                            onBack = if (navigator.canPop) {
                                {
                                    navigator.pop()
                                }
                            } else null,
                            isLoading = true,
                            errorMessage = null,
                            showTokenExpiredMessage = false
                        )
                    }

                    is AuthState.Error -> {
                        val errorMessage = (state as AuthState.Error).message
                        ErrorContent(
                            errorMessage = errorMessage,
                            onRetry = {
                                scope.launch {
                                    screenModel.authenticateWithoutEmail()
                                }
                            },
                            onCancel = { screenModel.resetState() }
                        )
                    }

                    is AuthState.Authenticated -> {
                        SuccessContent()
                    }

                    is AuthState.NotAuthenticated -> {
                        SignInContent(
                            onPasskeyClick = {
                                scope.launch {
                                    screenModel.authenticateWithoutEmail()
                                }
                            },
                            onHelpClick = { screenModel.showPasskeyHelp() },
                            onSignUpClick = {
                                navigator.push(RegisterScreen())
                            },
                            onBack = if (navigator.canPop) {
                                {
                                    navigator.pop()
                                }
                            } else null,
                            isLoading = false,
                            errorMessage = emailError,
                            showTokenExpiredMessage = showTokenExpiredMessage
                        )
                    }
                }

                // Passkey Bottom Sheet
                PasskeyBottomSheet(
                    showBottomSheet = showHelpDialog,
                    onDismiss = { screenModel.hidePasskeyHelp() },
                    sheetState = bottomSheetState
                )
            }
        }
    }
}

@Composable
private fun SignInContent(
    onPasskeyClick: () -> Unit,
    onHelpClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onBack: (() -> Unit)? = null,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    showTokenExpiredMessage: Boolean = false
) {
    val colors = MaterialTheme.mangalaColors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
            .padding(horizontal = 24.dp)
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        onBack?.let {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onBack
                ) {
                    Icon(
                        imageVector = MangalaWalletPack.IcBack,
                        contentDescription = "Back",
                        tint = colors.iconPrimary
                    )
                }
            }
        }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AnimatedRobotEmoji()

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Welcome Back",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Sign in with your passkey to continue",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            MangalaGradientButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = onPasskeyClick,
                enabled = !isLoading
            ) {
                if (isLoading)
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🔑",
                            fontSize = 16.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Sign in with Passkey",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            if (errorMessage != null || showTokenExpiredMessage) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = when {
                            showTokenExpiredMessage -> "Your session has expired. Please sign in again to continue."
                            else -> errorMessage ?: ""
                        },
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Don't have an account? Sign up.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onSignUpClick() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "What is a passkey?",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { onHelpClick() }
            )
        }
    }
}

@Composable
fun AnimatedRobotEmoji() {
    val colors = MaterialTheme.mangalaColors
    val infiniteTransition = rememberInfiniteTransition(label = "robot")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(colors.bgInnerCard)
            .shadow(
                elevation = 4.dp,
                shape = CircleShape
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "🤖",
            fontSize = 48.sp
        )
    }
}

@Composable
fun LoadingContent(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = Color(0xFF6366F1)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ErrorContent(
    errorMessage: String,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Authentication Failed",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(onClick = onCancel) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Try Again")
                    }
                }
            }
        }
    }
}

@Composable
fun SuccessContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "✅",
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Authentication Successful!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
