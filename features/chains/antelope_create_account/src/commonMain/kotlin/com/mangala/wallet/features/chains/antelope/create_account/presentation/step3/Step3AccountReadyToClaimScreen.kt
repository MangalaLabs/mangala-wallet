package com.mangala.wallet.features.chains.antelope.create_account.presentation.step3

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
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
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.features.chains.antelope.create_account.presentation.step4.Step4CreatingAccountScreen
import com.mangala.wallet.features.chains.antelope.create_account.presentation.ui.ProductAlreadyOwnedDialog
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.OnboardingButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.component.StepIndicator
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlinx.coroutines.delay
import org.koin.core.parameter.parametersOf

class Step3AccountReadyToClaimScreen(
    private val initialAccountName: String,
    private val initialAccountSuffix: String,
    private val initialAccountType: AccountNameType
) : BaseScreen<Step3AccountReadyToClaimScreenModel>() {

    override val screenName: String =
        MangalaAnalytics.Screens.ANTELOPE_CREATE_ACCOUNT_READY_TO_CLAIM
    override val screenClassName: String =
        Step3AccountReadyToClaimScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): Step3AccountReadyToClaimScreenModel {
        return getScreenModel<Step3AccountReadyToClaimScreenModel> {
            parametersOf(
                initialAccountName,
                initialAccountSuffix,
                initialAccountType
            )
        }
    }

    @OptIn(
        androidx.compose.material.ExperimentalMaterialApi::class,
        ExperimentalMaterial3Api::class
    )
    @Composable
    override fun ScreenContent(screenModel: Step3AccountReadyToClaimScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val scope = rememberCoroutineScope()
        val bottomSheetState = rememberModalBottomSheetState()
        var showCostBottomSheet by remember { mutableStateOf(false) }
        var showProductAlreadyOwnedDialog by remember { mutableStateOf(false) }
        var contentVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(100)
            contentVisible = true
        }

        // Handle navigation to creating account screen
        LaunchedEffect(Unit) {
            screenModel.navigateToCreatingAccountScreen.collect {
                val screen = ScreenRegistry.get(
                    SharedScreen.Step4CreatingAccountScreen(
                        accountName = initialAccountName,
                        accountSuffix = initialAccountSuffix,
                        operationType = SharedScreen.Step4CreatingAccountScreen.AccountOperationType.CREATE
                    )
                )
                navigator.push(screen)
            }
        }

        // Handle purchase flow initiation
        screenModel.onStartPurchaseFlow.collectAsState(null).value?.let { product ->
            screenModel.purchaseManager.launchPurchaseFlow(
                product,
                screenModel.getObfuscatedProfileId()
            )
            screenModel.onPurchaseFlowInitiated()
        }

        // Handle product already owned dialog
        LaunchedEffect(Unit) {
            screenModel.showProductAlreadyOwnedDialog.collect {
                showProductAlreadyOwnedDialog = true
            }
        }

        OnboardingGradientBackground(
            circleBackgroundEnabled = true,
            afterBackgroundModifier = Modifier.navigationBarsPadding().imePadding()
        ) {
            MaxSizeColumn {
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

                    // Step indicator (2 steps completed for ready to claim)
                    StepIndicator(
                        totalSteps = 4,
                        currentStep = 2
                    )

                    Spacer(modifier = Modifier.size(40.dp)) // Balance the layout
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top Bar with step indicator

                    Spacer(modifier = Modifier.height(24.dp))

                    // Title and Description with animation
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
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Ready to claim",
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
                                text = "You will only need to pay the fee once. Remember, this cannot be changed later",
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

                    Spacer(modifier = Modifier.height(32.dp))

                    // Account Name Display with animation
                    AnimatedVisibility(
                        visible = contentVisible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 400)) +
                                slideInVertically(
                                    initialOffsetY = { it / 4 },
                                    animationSpec = tween(600, delayMillis = 400)
                                )
                    ) {
                        AccountNameCard(
                            accountName = "$initialAccountName$initialAccountSuffix",
                            uiState = uiState
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // What you're getting section with animation
                    AnimatedVisibility(
                        visible = contentVisible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 600)) +
                                slideInVertically(
                                    initialOffsetY = { it / 4 },
                                    animationSpec = tween(600, delayMillis = 600)
                                )
                    ) {
                        WhatYouAreGettingSection()
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Purchase Button and link with animation
                    AnimatedVisibility(
                        visible = contentVisible,
                        enter = fadeIn(animationSpec = tween(600, delayMillis = 800))
                    ) {
                        Column {
                            OnboardingButton(
                                text = if (uiState.hasUnconsumedPurchase) {
                                    "Continue with existing purchase"
                                } else {
                                    "Purchase for ${uiState.displayPrice}"
                                },
                                onClick = {
                                    screenModel.onPurchaseClick()
                                },
                                isPrimary = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )

                            // Why does it cost money link
                            Text(
                                text = "Why does it cost money?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF3B90FF),
                                textAlign = TextAlign.Center,
                                letterSpacing = (-0.14).sp,
                                lineHeight = 19.6.sp,
                                fontFamily = getInterFontFamily(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showCostBottomSheet = true
                                    }
                                    .padding(vertical = 16.dp)
                                    .padding(bottom = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Why does it cost money bottom sheet
        if (showCostBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCostBottomSheet = false },
                sheetState = bottomSheetState,
                containerColor = Color(0xFF1D263E),
                contentColor = Color.White,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .background(
                                Color.White.copy(alpha = 0.3f),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            ) {
                WhyDoesItCostMoneyContent(
                    onDismiss = { showCostBottomSheet = false },
                    displayPrice = uiState.displayPrice
                )
            }
        }

        // Product already owned dialog
        if (showProductAlreadyOwnedDialog) {
            ProductAlreadyOwnedDialog(
                accountName = uiState.accountNameWithSuffix,
                onDismiss = {
                    showProductAlreadyOwnedDialog = false
                    screenModel.onDismissProductAlreadyOwnedDialog()
                },
                onConfirmCreate = {
                    showProductAlreadyOwnedDialog = false
                    screenModel.onConfirmProductAlreadyOwned()
                }
            )
        }
    }

    override val isBottomBarVisible: Boolean = false
}

@Composable
private fun AccountNameCard(
    accountName: String,
    uiState: Step3AccountReadyToClaimUiState
) {
    val gradientBrush = Brush.linearGradient(
        colorStops = arrayOf(
            0.0f to Color(0xFF227BFF).copy(alpha = 0.15f),
            0.5f to Color(0xFFB988EE).copy(alpha = 0.12f),
            1.0f to Color(0xFFEE4D5D).copy(alpha = 0.15f)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(gradientBrush)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Vaulta account icon
            LocalImage(
                imageResource = MR.images.vaulta_account_name_default,
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = accountName,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFF1F5F9),
                textAlign = TextAlign.Center,
                letterSpacing = (-0.17).sp,
                lineHeight = 23.8.sp,
                fontFamily = getInterFontFamily()
            )

            // Show additional info if user has unconsumed purchase
            if (uiState.hasUnconsumedPurchase) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You have an existing purchase for this account",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF10B981),
                    textAlign = TextAlign.Center,
                    letterSpacing = (-0.12).sp,
                    lineHeight = 16.8.sp,
                    fontFamily = getInterFontFamily()
                )
            }
        }
    }
}

@Composable
private fun WhyDoesItCostMoneyContent(
    displayPrice: String,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title with emoji
        Text(
            text = "💭 Why $displayPrice?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontFamily = getInterFontFamily()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = "Unlike regular wallets, Vaulta gives you a real identity on the blockchain.",
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFD1D1D1),
            textAlign = TextAlign.Center,
            lineHeight = 23.8.sp,
            fontFamily = getInterFontFamily()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Comparison Table
        ComparisonTable()

        Spacer(modifier = Modifier.height(32.dp))

        // Got it button
        OnboardingButton(
            text = "Got it",
            onClick = onDismiss,
            isPrimary = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ComparisonTable() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF0F172A))
            .border(
                width = 1.dp,
                color = Color(0xFF1E293B),
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        // Headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Text(
                text = "",
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Vaulta",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontFamily = getInterFontFamily(),
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Regular Wallets",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontFamily = getInterFontFamily(),
                modifier = Modifier.weight(1f)
            )
        }

        // Comparison rows
        ComparisonRow(
            feature = "Easy to share",
            vaultaHas = true,
            regularHas = false
        )

        Divider(color = Color(0xFF1E293B), thickness = 1.dp)

        ComparisonRow(
            feature = "Human-readable",
            vaultaHas = true,
            regularHas = false
        )

        Divider(color = Color(0xFF1E293B), thickness = 1.dp)

        ComparisonRow(
            feature = "Permanent identity",
            vaultaHas = true,
            regularHas = false
        )

        Divider(color = Color(0xFF1E293B), thickness = 1.dp)

        ComparisonRow(
            feature = "No renewal fees",
            vaultaHas = true,
            regularHas = false
        )

        Divider(color = Color(0xFF1E293B), thickness = 1.dp)

        ComparisonRow(
            feature = "Can transfer/sell",
            vaultaHas = true,
            regularHas = false
        )

        Divider(color = Color(0xFF1E293B), thickness = 1.dp)

        ComparisonRow(
            feature = "Professional look",
            vaultaHas = true,
            regularHas = false
        )
    }
}

@Composable
private fun ComparisonRow(
    feature: String,
    vaultaHas: Boolean,
    regularHas: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = feature,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFA5B4CB),
            fontFamily = getInterFontFamily(),
            modifier = Modifier.weight(1f)
        )

        // Vaulta column
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (vaultaHas) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Has feature",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Doesn't have feature",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Regular wallets column
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (regularHas) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Has feature",
                    tint = Color(0xFF10B981),
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Doesn't have feature",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun WhatYouAreGettingSection() {
    Column(
        modifier = Modifier
            .padding(horizontal = 89.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "What you're getting",
            fontSize = 17.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFF1F5F9),
            textAlign = TextAlign.Center,
            letterSpacing = (-0.17).sp,
            lineHeight = 23.8.sp,
            fontFamily = getInterFontFamily()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            WhatYouAreGettingItem("Permanent blockchain identity")
            WhatYouAreGettingItem("Easy-to-share wallet address")
            WhatYouAreGettingItem("No renewal fees ever")
            WhatYouAreGettingItem("Transfer or sell anytime")
        }
    }
}

@Composable
private fun WhatYouAreGettingItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Check",
                tint = Color(0xFF8647F3),
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFF1F5F9),
            textAlign = TextAlign.Start,
            letterSpacing = (-0.12).sp,
            lineHeight = 16.8.sp,
            fontFamily = getInterFontFamily()
        )
    }
}