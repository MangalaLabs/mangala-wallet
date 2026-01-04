package com.mangala.wallet.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.mangala.wallet.core.auth.domain.model.AuthState
import com.mangala.wallet.auth.presentation.signin.ErrorContent
import com.mangala.wallet.auth.presentation.signin.SuccessContent
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.component.KeyboardDismissBox
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.PasskeyBottomSheet
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Register Screen for creating new AI Assistant accounts with passkey
 */
class RegisterScreen : BaseScreen<RegisterScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.REGISTER
    override val screenClassName: String = RegisterScreen::screenName.name
    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): RegisterScreenModel = getScreenModel<RegisterScreenModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ScreenContent(screenModel: RegisterScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val state by screenModel.authState.collectAsState()
        val showHelpDialog by screenModel.showHelpDialog.collectAsState()
        val email by screenModel.email.collectAsState()
        val emailError by screenModel.emailError.collectAsState()
        val isEmailValid by screenModel.isEmailValid.collectAsState()
        val showValidIcon by screenModel.showValidIcon.collectAsState()
        val isValidating by screenModel.isValidating.collectAsState()
        val isApiError by screenModel.isApiError.collectAsState()
        val scope = rememberCoroutineScope()
        val hapticFeedback = LocalHapticFeedback.current
        
        LaunchedEffect(state) {
            if (state is AuthState.Authenticated) {
                hapticFeedback.performHapticFeedback(HapticFeedbackType.Confirm)
                
                delay(1500) // Show success animation
                
                try {
                    val conversationScreen = ScreenRegistry.get(SharedScreen.ConversationSessionListScreen)
                    navigator.replaceAll(conversationScreen)
                } catch (e: Exception) {
                    println("Error navigating to ConversationUiScreen: ${e.message}")
                    e.printStackTrace()
                    screenModel.navigateToConversationUi()
                }
            }
        }
        
        Scaffold { paddingValues ->
            KeyboardDismissBox(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                when (state) {
                    is AuthState.Initial -> {
                        RegisterContent(
                            email = email,
                            emailError = emailError,
                            isEmailValid = isEmailValid,
                            showValidIcon = showValidIcon,
                            isValidating = isValidating,
                            isApiError = isApiError,
                            onEmailChange = { screenModel.updateEmail(it) },
                            onCreatePasskeyClick = {
                                scope.launch {
                                    screenModel.createPasskey()
                                }
                            },
                            onSignInClick = {
                                navigator.pop() // Go back to sign in screen
                            },
                            onHelpClick = { screenModel.showPasskeyHelp() },
                            onBackClick = { navigator.pop() },
                            isLoading = false
                        )
                    }
                    is AuthState.Loading -> {
                        RegisterContent(
                            email = email,
                            emailError = emailError,
                            isEmailValid = isEmailValid,
                            showValidIcon = showValidIcon,
                            isValidating = isValidating,
                            isApiError = isApiError,
                            onEmailChange = { screenModel.updateEmail(it) },
                            onCreatePasskeyClick = {
                                scope.launch {
                                    screenModel.createPasskey()
                                }
                            },
                            onSignInClick = {
                                navigator.pop() // Go back to sign in screen
                            },
                            onHelpClick = { screenModel.showPasskeyHelp() },
                            onBackClick = { navigator.pop() },
                            isLoading = true
                        )
                    }
                    is AuthState.Error -> {
                        val errorMessage = (state as AuthState.Error).message
                        ErrorContent(
                            errorMessage = errorMessage,
                            onRetry = { 
                                scope.launch {
                                    screenModel.createPasskey()
                                }
                            },
                            onCancel = { screenModel.resetState() }
                        )
                    }
                    is AuthState.Authenticated -> {
                        SuccessContent()
                    }
                    is AuthState.NotAuthenticated -> {
                        RegisterContent(
                            email = email,
                            emailError = emailError,
                            isEmailValid = isEmailValid,
                            showValidIcon = showValidIcon,
                            isValidating = isValidating,
                            isApiError = isApiError,
                            onEmailChange = { screenModel.updateEmail(it) },
                            onCreatePasskeyClick = {
                                scope.launch {
                                    screenModel.createPasskey()
                                }
                            },
                            onSignInClick = {
                                navigator.pop() // Go back to sign in screen
                            },
                            onHelpClick = { screenModel.showPasskeyHelp() },
                            onBackClick = { navigator.pop() },
                            isLoading = false
                        )
                    }
                }
                
                PasskeyBottomSheet(
                    showBottomSheet = showHelpDialog,
                    onDismiss = { screenModel.hidePasskeyHelp() }
                )
            }
        }
    }
}

@Composable
private fun RegisterContent(
    email: String,
    emailError: String?,
    isEmailValid: Boolean,
    showValidIcon: Boolean,
    isValidating: Boolean,
    isApiError: Boolean,
    onEmailChange: (String) -> Unit,
    onCreatePasskeyClick: () -> Unit,
    onSignInClick: () -> Unit,
    onHelpClick: () -> Unit,
    onBackClick: () -> Unit,
    isLoading: Boolean = false
) {
    val colors = MaterialTheme.mangalaColors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colors.bg,
                        Color(0xFF111111)
                    )
                )
            )
            .padding(horizontal = 24.dp)
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status bar space
        Spacer(modifier = Modifier.height(24.dp))
        
        // Back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = colors.textPrimary
                )
            }
        }

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            // Header section
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Robot emoji (larger for registration)
                Text(
                    text = "🤖",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Title
                Text(
                    text = "Create Your AI Assistant Account",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 30.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Description
            Text(
                text = "Create a secure passkey so your chats stay private and synced across devices.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Input fields
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Email TextField
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    placeholder = {
                        Text(
                            "Email address",
                            color = colors.textSecondary
                        )
                    },
                    isError = emailError != null,
                    trailingIcon = {
                        if (showValidIcon) {
                            Text(
                                text = "✓",
                                color = Color(0xFF4CAF50),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    supportingText = {
                        Text(
                            text = emailError.orEmpty(),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Allow retry on API errors, require validation for other cases
                            val canSubmit = !isLoading && !isValidating && (isEmailValid || isApiError)
                            if (canSubmit) {
                                onCreatePasskeyClick()
                            }
                        }
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = colors.bgInnerCard,
                        focusedContainerColor = colors.bgInnerCard,
                        unfocusedBorderColor = if (showValidIcon) Color(0xFF4CAF50) else Color.Transparent,
                        focusedBorderColor = if (emailError != null) MaterialTheme.colorScheme.error
                                            else if (showValidIcon) Color(0xFF4CAF50)
                                            else Color(0xFF5B5BD6),
                        unfocusedTextColor = colors.textPrimary,
                        focusedTextColor = colors.textPrimary,
                        errorBorderColor = MaterialTheme.colorScheme.error
                    )
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            MangalaGradientButton(
                onClick = onCreatePasskeyClick,
                enabled = !isLoading && !isValidating && (isEmailValid || isApiError),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
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
                            text = "Create Passkey & Continue",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Helper note
            Text(
                text = "We'll only use your email to sync chats.",
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Secondary links
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Already have an account? Sign in",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onSignInClick() }
                )

                Text(
                    text = "What is a passkey?",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onHelpClick() }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}