package com.mangala.wallet.features.chains.antelope.create_account.presentation.step2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.Step3AccountReadyToClaimScreen
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.GradientTermsCheckbox
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.OnboardingButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.component.StepIndicator
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.coroutines.delay
import org.koin.core.parameter.parametersOf

class Step2SelectAccountNameScreenV2 : BaseScreen<Step2SelectAccountNameScreenModel>() {

    override val screenName: String =
        MangalaAnalytics.Screens.ANTELOPE_CREATE_ACCOUNT_SELECT_ACCOUNT_NAME
    override val screenClassName: String =
        Step2SelectAccountNameScreenV2::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): Step2SelectAccountNameScreenModel {
        return getScreenModel<Step2SelectAccountNameScreenModel> {
            parametersOf(
                "",
                null,
                AccountNameType.Premium
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: Step2SelectAccountNameScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        var isTermsAgreed by remember { mutableStateOf(false) }

        MangalaBottomSheetNavigator(
            sheetShape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp
            )
        ) {
            val bottomNavigator = LocalBottomSheetNavigator.current

            AccountNameSelectionScreen(
                uiState = uiState,
                isTermsAgreed = isTermsAgreed,
                onTermsAgreedChange = { isTermsAgreed = it },
                onClickBack = {
                    navigator.pop()
                },
                onAccountNameChange = {
                    screenModel.onAccountNameChange(it)
                },
                onClickSuggest = {
                    screenModel.suggestValidName()
                },
                onClickSetAccountType = {
                    val screen = ScreenRegistry.get(
                        SharedScreen.SelectAccountTypeScreen(
                            it
                        ) { newAccountType ->
                            screenModel.onAccountTypeChange(newAccountType)
                            bottomNavigator.hide()
                        }
                    )
                    bottomNavigator.show(screen)
                },
                onClickCreate = {
                    if (!isTermsAgreed) return@AccountNameSelectionScreen
                    val currentState = uiState as? Step2SelectAccountNameUiState.Ready
                        ?: return@AccountNameSelectionScreen
                    navigator.push(
                        Step3AccountReadyToClaimScreen(
                            initialAccountName = it,
                            initialAccountSuffix = currentState.accountNameSuffix,
                            initialAccountType = AccountNameType.Premium
                        )
                    )
                },
                onClickCreateFromEvm = {
                    if (!isTermsAgreed) return@AccountNameSelectionScreen
                    val currentState = uiState as? Step2SelectAccountNameUiState.Ready
                        ?: return@AccountNameSelectionScreen
                    navigator.push(
                        ScreenRegistry.get(
                            SharedScreen.CreateEosAccountViaEVMScreen(
                                accountName = it,
                                accountNameSuffix = currentState.accountNameSuffix,
                                accountNameType = AccountNameType.Premium
                            )
                        )
                    )
                },
                onAccountNameReset = {
                    screenModel.onAccountNameChange(TextFieldValue(""))
                },
                onTermsClick = {
                    navigator.push(
                        ScreenRegistry.get(SharedScreen.TermsAndPolicyScreen)
                    )
                }
            )
        }
    }

    override val isBottomBarVisible: Boolean = false

    @Composable
    fun AccountNameSelectionScreen(
        uiState: Step2SelectAccountNameUiState,
        isTermsAgreed: Boolean,
        onTermsAgreedChange: (Boolean) -> Unit,
        onClickBack: () -> Unit,
        onAccountNameChange: (TextFieldValue) -> Unit,
        onClickSuggest: () -> Unit,
        onClickSetAccountType: (AccountNameType) -> Unit,
        onClickCreate: (accountName: String) -> Unit,
        onClickCreateFromEvm: (accountName: String) -> Unit,
        onAccountNameReset: () -> Unit,
        onTermsClick: () -> Unit
    ) {
        val focusManager = LocalFocusManager.current
        val keyboardController = LocalSoftwareKeyboardController.current
        var contentVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(100)
            contentVisible = true
        }

        if (uiState is Step2SelectAccountNameUiState.Ready) {
            OnboardingGradientBackground(
                circleBackgroundEnabled = true,
                afterBackgroundModifier = Modifier.navigationBarsPadding().imePadding()
            ) {
                MaxSizeColumn(
                    modifier = Modifier
                        .pointerInput(Unit) {
                            detectTapGestures {
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        }
                ) {
                    // Top Bar with step indicator
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
                                .clickable { onClickBack() }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = MangalaWalletPack.ArrowLeft,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        // Step indicator (1 step completed for account name selection)
                        StepIndicator(
                            totalSteps = 4,
                            currentStep = 1
                        )

                        Spacer(modifier = Modifier.size(40.dp)) // Balance the layout
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))

                        // Title with animation
                        AnimatedVisibility(
                            visible = contentVisible,
                            enter = fadeIn(animationSpec = tween(600, delayMillis = 200)) +
                                    slideInVertically(
                                        initialOffsetY = { it / 6 },
                                        animationSpec = tween(600, delayMillis = 200)
                                    )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "Pick Your VAULTA Account Name",
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
                                    text = "Your account name is your wallet address. Once claimed, it's yours permanently.",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = Color(0xFFA5B4CB),
                                    textAlign = TextAlign.Start,
                                    letterSpacing = (-0.14).sp,
                                    lineHeight = 19.6.sp,
                                    fontFamily = getInterFontFamily()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        // Account Name Input Section with animation
                        AnimatedVisibility(
                            visible = contentVisible,
                            enter = fadeIn(animationSpec = tween(600, delayMillis = 600)) +
                                    slideInVertically(
                                        initialOffsetY = { it / 4 },
                                        animationSpec = tween(600, delayMillis = 600)
                                    )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                horizontalAlignment = Alignment.Start
                            ) {
                                // Account Name Label
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Account name",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFF1F5F9),
                                        fontFamily = getInterFontFamily()
                                    )
                                    Text(
                                        text = "*",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFFA0000),
                                        fontFamily = getInterFontFamily()
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Account Name Input
                                AccountNameInputField(
                                    value = uiState.accountName,
                                    onValueChange = onAccountNameChange,
                                    suffix = uiState.accountNameSuffix,
                                    isError = uiState.accountName.text.isNotEmpty() && !uiState.validationResult.isValid,
                                    onClear = onAccountNameReset
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Character Count and Suggest
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${uiState.accountName.text.length + uiState.accountNameSuffix.length}/12 characters",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xFFA5B4CB),
                                        fontFamily = getInterFontFamily()
                                    )
                                    Text(
                                        text = "Suggest",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xFF3B90FF),
                                        fontFamily = getInterFontFamily(),
                                        modifier = Modifier.clickable { onClickSuggest() }
                                    )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                // Validation Checklist
                                ValidationChecklistV2(uiState)
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.height(24.dp))

                        // Terms and Continue Button with animation
                        AnimatedVisibility(
                            visible = contentVisible,
                            enter = fadeIn(animationSpec = tween(600, delayMillis = 800)) +
                                    slideInVertically(
                                        initialOffsetY = { it / 4 },
                                        animationSpec = tween(600, delayMillis = 800)
                                    )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .padding(bottom = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                GradientTermsCheckbox(
                                    isChecked = isTermsAgreed,
                                    onCheckedChange = onTermsAgreedChange,
                                    onTermsClick = onTermsClick
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                MangalaGradientButton(
                                    label = "Create Vaulta account",
                                    onClick = {
                                        if (isTermsAgreed && uiState.isAccountNameValid) {
                                            onClickCreate(uiState.accountName.text)
                                        }
                                    },
                                    enabled = isTermsAgreed && uiState.isAccountNameValid,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

//                            OnboardingButton(
//                                text = "Create from EVM",
//                                onClick = {
//                                    if (isTermsAgreed && uiState.isAccountNameValid) {
//                                        onClickCreateFromEvm(uiState.accountName.text)
//                                    }
//                                },
//                                isPrimary = false,
//                                modifier = Modifier.fillMaxWidth()
//                            )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun AccountNameInputField(
        value: TextFieldValue,
        onValueChange: (TextFieldValue) -> Unit,
        suffix: String?,
        isError: Boolean,
        onClear: () -> Unit
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0A0E1A))
                .border(
                    width = 1.dp,
                    color = when {
                        isError -> Color(0xFFFF5252)
                        isFocused -> Color(0xFF3B90FF)
                        else -> Color(0xFF2A3E6C)
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        fontFamily = getInterFontFamily()
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    interactionSource = interactionSource,
                    cursorBrush = SolidColor(Color(0xFF3B90FF)),
                    visualTransformation = VisualTransformation.None,
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (value.text.isEmpty()) {
                                    Text(
                                        text = "Enter account name",
                                        color = Color(0xFFA5B4CB),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontFamily = getInterFontFamily()
                                    )
                                }
                                innerTextField()
                            }
                            if (suffix != null) {
                                Text(
                                    text = suffix,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = getInterFontFamily()
                                )
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (value.text.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6B7280))
                            .clickable { onClear() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun ValidationChecklistV2(uiState: Step2SelectAccountNameUiState.Ready) {
        val validationResult = uiState.validationResult
        val isAccountNameEmpty = uiState.accountName.text.isEmpty()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1D263E))
                .padding(12.dp, 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val maxCharsText = if (uiState.accountNameSuffix != null) {
                "Maximum ${12 - uiState.accountNameSuffix.length} characters + ${uiState.accountNameSuffix}"
            } else {
                "Maximum 12 characters"
            }

            val conditions = listOf(
                "Use only a-z and 1-5" to (validationResult.containsOnlyValidCharacters && !isAccountNameEmpty),
                "Can't start/end with period (.)" to (validationResult.startsAndEndsCorrectly && !isAccountNameEmpty),
                "Can't start with numbers" to (validationResult.startsAndEndsCorrectly && !isAccountNameEmpty),
                maxCharsText to (validationResult.isValidLength && !isAccountNameEmpty)
            )

            conditions.forEach { (condition, isValid) ->
                ValidationItemV2(isValid, condition)
            }

            if (uiState.isCheckingAccountExistence) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier.size(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
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
                                .size(16.dp)
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

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Checking availability...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFD1D1D1),
                        fontFamily = getInterFontFamily()
                    )
                }
            } else if (uiState.isAccountNotTaken != null) {
                ValidationItemV2(
                    uiState.isAccountNotTaken == true,
                    "Name is available"
                )
            }
        }
    }

    @Composable
    private fun ValidationItemV2(isValid: Boolean, condition: String) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = if (isValid) "Valid" else "Invalid",
                    tint = if (isValid) Color(0xFFC27DFF) else Color(0xFF6B7280),
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = condition,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = if (isValid) Color.White else Color(0xFF6B7280),
                fontFamily = getInterFontFamily()
            )
        }
    }
}