package com.mangala.wallet.features.chains.antelope.presentation.esr

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Dropdown
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.ui.component.*
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.ui.utils.toggle
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.memtrip.eos.chain.actions.transaction.esr.EsrSigningRequestArgs
import kotlinx.coroutines.delay
import org.koin.core.parameter.parametersOf

class EsrScreen(private val esrUri: String) : BaseScreen<EsrScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_ESR
    override val screenClassName: String = EsrScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): EsrScreenModel {
        return getScreenModel { parametersOf(esrUri) }
    }

    override val isBottomBarVisible: Boolean = false

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: EsrScreenModel) {
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value
        val navigator = LocalNavigator.currentOrThrow
        var contentVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(100)
            contentVisible = true
        }

        MangalaBottomSheetNavigator(
            sheetShape = RectangleShape
        ) {
            val bottomSheetNavigator = LocalBottomSheetNavigator.current

            OnboardingGradientBackground(
                circleBackgroundEnabled = true
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Top Bar
                        MangalaWalletTopBar(
                            text = "Signing Request",
                            onBackClicked = {
                                navigator.pop()
                            }
                        )

                        // Scrollable Content
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp)
                        ) {
                            AnimatedVisibility(
                                visible = contentVisible,
                                modifier = Modifier.weight(1f),
                                enter = fadeIn(
                                    animationSpec = tween(
                                        durationMillis = 600,
                                        delayMillis = 100
                                    )
                                ) + slideInVertically(
                                    initialOffsetY = { it / 4 },
                                    animationSpec = tween(
                                        durationMillis = 600,
                                        delayMillis = 100
                                    )
                                )
                            ) {
                                when (uiState) {
                                    is EsrScreenUiState.Loading -> {
                                        LoadingState()
                                    }

                                    is EsrScreenUiState.Error -> {
                                        ErrorState(uiState.error)
                                    }

                                    is EsrScreenUiState.Data -> {
                                        when (uiState.uiModel) {
                                            is EsrDataUiModel.Identity -> {
                                                IdentityRequestContent(
                                                    esrSigningRequest = uiState.uiModel.esrSigningRequest,
                                                    uiModel = uiState.uiModel,
                                                    screenModel = screenModel,
                                                    blockchainName = uiState.uiModel.blockchainType.name
                                                )
                                            }

                                            is EsrDataUiModel.SignTransaction -> {
                                                SignTransactionContent(
                                                    esrSigningRequest = uiState.uiModel.esrSigningRequest,
                                                    uiModel = uiState.uiModel,
                                                    screenModel = screenModel,
                                                    blockchainName = uiState.uiModel.blockchainType.name
                                                )
                                            }
                                        }
                                    }

                                    is EsrScreenUiState.Signing -> {
                                        SigningState()
                                    }

                                    is EsrScreenUiState.Success -> {
                                        SuccessState(
                                            onBackToHome = {
                                                navigator.popUntilRoot()
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Button - Anchored to bottom
                    when (uiState) {
                        is EsrScreenUiState.Data -> {
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 16.dp)
                            ) {
                                MangalaGradientButton(
                                    label = when (uiState.uiModel) {
                                        is EsrDataUiModel.Identity -> "Accept Request"
                                        is EsrDataUiModel.SignTransaction -> "Sign Transaction"
                                    },
                                    onClick = {
                                        val unlockPinScreen = ScreenRegistry.get(
                                            SharedScreen.UnlockPinScreen(
                                                SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                                                onUnlockSuccess = {
                                                    when (uiState.uiModel) {
                                                        is EsrDataUiModel.Identity -> screenModel.onAcceptIdentity()
                                                        is EsrDataUiModel.SignTransaction -> screenModel.onAcceptSignTransaction()
                                                    }
                                                    bottomSheetNavigator.hide()
                                                },
                                                antelopeAccountName = null
                                            )
                                        )
                                        bottomSheetNavigator.show(unlockPinScreen)
                                    },
                                    buttonStyle = MangalaButtonStyle.GRADIENT,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    @Composable
    private fun LoadingState() {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MangalaCircularProgressIndicator(
                    size = 48.dp,
                    strokeWidth = 4.dp,
                    color = MaterialTheme.mangalaColors.iconPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading signing request...",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }

    @Composable
    private fun ColumnScope.SigningState() {
        Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                MangalaCircularProgressIndicator(
                    size = 48.dp,
                    strokeWidth = 4.dp,
                    color = MaterialTheme.mangalaColors.iconPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Signing transaction...",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }

    @Composable
    private fun ErrorState(error: String) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0x14FFFFFF)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Text(
                    text = "Error",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFF6B6B)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = error,
                    fontSize = 15.sp,
                    color = Color(0xFFD1D1D1)
                )
            }
        }
    }

    @Composable
    private fun ColumnScope.SuccessState(onBackToHome: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓",
                        fontSize = 48.sp,
                        color = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Transaction Signed Successfully",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            MangalaGradientButton(
                label = "Back to Home",
                onClick = onBackToHome,
                buttonStyle = MangalaButtonStyle.GRADIENT,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
        }
    }

    @Composable
    private fun IdentityRequestContent(
        esrSigningRequest: EsrSigningRequestArgs,
        uiModel: EsrDataUiModel.Identity,
        screenModel: EsrScreenModel,
        blockchainName: String
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Identity Request",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Request on $blockchainName",
                fontSize = 14.sp,
                color = Color(0xFFD1D1D1)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Scope info
            Text(
                text = "Scope: ${esrSigningRequest.identityRequest?.scope}",
                fontSize = 15.sp,
                color = Color(0xFFD1D1D1)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Account Selection
            Text(
                text = "Select Account",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Dropdown(
                selectedItem = uiModel.selectedAccount.orEmpty(),
                items = uiModel.accounts.orEmpty(),
                onSelect = { screenModel.onSelectAccount(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Permission Selection
            Text(
                text = "Select Permission",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Dropdown(
                selectedItem = uiModel.selectedPermission.orEmpty(),
                items = uiModel.permissions.orEmpty(),
                onSelect = { screenModel.onSelectPermission(it) }
            )
        }
    }

    @Composable
    private fun SignTransactionContent(
        esrSigningRequest: EsrSigningRequestArgs,
        uiModel: EsrDataUiModel.SignTransaction,
        screenModel: EsrScreenModel,
        blockchainName: String
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Title
            Text(
                text = "Sign Transaction",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Request on $blockchainName",
                fontSize = 14.sp,
                color = Color(0xFFD1D1D1)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Actions Section
            Text(
                text = "Actions",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                esrSigningRequest.transaction?.actions?.forEach { action ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0x14FFFFFF)
                        )
                    ) {
                        Text(
                            text = action.toString(),
                            fontSize = 13.sp,
                            color = Color(0xFFD1D1D1),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Authorization Selection
            Text(
                text = "Select Authorization",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Dropdown(
                selectedItem = uiModel.selectedAuthorization.orEmpty(),
                items = uiModel.validAuthorizations,
                onSelect = { screenModel.onSelectAuthorization(it) }
            )
        }
    }

    @Composable
    private fun Dropdown(
        selectedItem: String,
        items: List<String>,
        onSelect: (String) -> Unit,
    ) {
        val isOpenDropdownMenuAccount = remember { mutableStateOf(false) }
        Box {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isOpenDropdownMenuAccount.toggle() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0x14FFFFFF)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedItem,
                        fontSize = 15.sp,
                        color = if (selectedItem.isNotEmpty()) Color.White else Color(0xFF808080)
                    )
                    MangalaWalletIconButton(
                        icon = MangalaWalletPack.Dropdown,
                        tint = Colors.caption,
                        onClick = { isOpenDropdownMenuAccount.toggle() }
                    )
                }
            }

            DropdownMenu(
                expanded = isOpenDropdownMenuAccount.value,
                onDismissRequest = { isOpenDropdownMenuAccount.value = false },
                modifier = Modifier.background(
                    color = WalletThemeV2.Colors.cardBackground,
                    shape = RoundedCornerShape(12.dp)
                )
            ) {
                items.forEach { optionName ->
                    DropdownMenuItem(
                        onClick = {
                            onSelect(optionName)
                            isOpenDropdownMenuAccount.value = false
                        },
                    ) {
                        Text(
                            text = optionName,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}