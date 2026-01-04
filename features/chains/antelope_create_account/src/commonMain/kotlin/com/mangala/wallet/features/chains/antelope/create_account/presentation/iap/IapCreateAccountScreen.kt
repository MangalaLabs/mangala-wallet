package com.mangala.wallet.features.chains.antelope.create_account.presentation.iap

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.chains.antelope.create_account.presentation.ui.ProductAlreadyOwnedDialog
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.MangalaWrappedTextButton
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.resolve
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.delay
import org.koin.core.parameter.parametersOf

class IapCreateAccountScreen(
    private val accountNameWithSuffix: String,
    private val accountNameType: String,
    private val skipToCreateAccountStep: Boolean = false, // for when payment already confirmed/ existing payment
    private val retryCreateAccountName: Boolean = false,
    private val purchaseToken: String? = null,
    private val purchaseId: String? = null
) : BaseScreen<IapCreateAccountScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_IAP_CREATE_ACCOUNT
    override val screenClassName: String = IapCreateAccountScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): IapCreateAccountScreenModel {
        return getScreenModel<IapCreateAccountScreenModel>(
            parameters = {
                parametersOf(
                    accountNameWithSuffix,
                    accountNameType,
                    skipToCreateAccountStep,
                    retryCreateAccountName,
                    purchaseToken,
                    purchaseId
                )
            }
        )
    }

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun ScreenContent(screenModel: IapCreateAccountScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiModel = screenModel.uiModel.collectAsStateMultiplatform().value

        screenModel.onStartPurchaseFlow.collectAsState(null).value?.let {
            screenModel.purchaseManager.launchPurchaseFlow(
                it,
                screenModel.getObfuscatedProfileId()
            )
            screenModel.onRequestIapPurchaseInitiated()
        }

        if (uiModel.iapProductAlreadyOwnedDialog != null) {
            val accountName = uiModel.iapProductAlreadyOwnedDialog.accountName

            ProductAlreadyOwnedDialog(
                accountName,
                onDismiss = {
                    screenModel.onDismissProductAlreadyOwnedDialog()
                },
                onConfirmCreate = {
                    screenModel.onDismissProductAlreadyOwnedDialog()
                    screenModel.onClickCreateWithExistingPurchase()
                }
            )
        }

        CreateAccountScreen(
            uiModel = uiModel,
            onClickBack = {
                navigator.pop()
            },
            onClickButton = {
                when (uiModel.currentStep) {
                    is CreateAccountStep.ExploreMangalaOrSetupPin -> {
                        val nextScreen = if (uiModel.isPinSetUp) {
                            SharedScreen.BackupAntelopeAccountScreen(
                                accountName = accountNameWithSuffix,
                                blockchainUid = uiModel.blockchainType?.uid
                            )
                        } else {
                            SharedScreen.SetupPinScreen(
                                blockchainUid = uiModel.blockchainType?.uid,
                                antelopeAccountName = accountNameWithSuffix,
                                pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN_AND_BACKUP_ANTELOPE.name
                            )
                        }
                        navigator.push(ScreenRegistry.get(nextScreen))
                    }
                    is CreateAccountStep.CreateAccount -> screenModel.onClickMainButtonCreateAccount()
                    is CreateAccountStep.Payment -> screenModel.onClickMainButtonPayment()
                }
            },
            onClickContactSupport = {
                screenModel.onClickContactSupport()
            }
        )
    }

    @Composable
    fun CreateAccountScreen(
        uiModel: IapCreateAccountUiModel,
        onClickBack: () -> Unit,
        onClickButton: () -> Unit,
        onClickContactSupport: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.appleBg)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            MangalaWalletTopBar(
                text = "",
                onBackClicked = { onClickBack() }
            )

            Column(Modifier.padding(horizontal = Dimensions.Padding.default).weight(1f).verticalScroll(rememberScrollState())) {
                VerticalSpacer(Dimensions.Padding.default)
                AnimatedContent(
                    uiModel.exploreMangalaOrSetupPinStepStatus,
                    transitionSpec = {
                        (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                                scaleIn(
                                    initialScale = 0.92f,
                                    animationSpec = tween(220, delayMillis = 90)
                                ))
                            .togetherWith(fadeOut(animationSpec = tween(90)))
                    }) { status ->
                    TextNormal(
                        text = when (status) {
                            CreateAccountStepStatus.DONE_FOCUSED -> {
                                StringDesc.ResourceFormatted(
                                    MR.strings.message_step_3_create_account_payment_done,
                                    uiModel.accountTypeName.resolve(),
                                ).localized()
                            }
                            CreateAccountStepStatus.IN_PROGRESS_PROMPT -> {
                                StringDesc.ResourceFormatted(
                                    MR.strings.message_step_3_create_account_payment_setup_pin,
                                    uiModel.accountTypeName.resolve(),
                                ).localized()
                            }
                            else -> {
                                StringDesc.ResourceFormatted(
                                    MR.strings.message_step_3_create_account_payment_introduction,
                                    uiModel.accountTypeName.resolve(),
                                ).localized()
                            }
                        },
                        fontWeight = FontWeight.Medium,
                        color = Colors.slateGray
                    )
                }

                VerticalSpacer(80.dp)
                MaxWidthColumn(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    StepIndicatorRow(
                        stepStatuses = uiModel.allStepStatuses
                    )
                    VerticalSpacer(Spacing.XXBASE)
                    StepsVerticalList(uiModel)
                }
            }

            Column(modifier = Modifier.padding(horizontal = Dimensions.Padding.default)) {
                ButtonNormal(
                    text = uiModel.mainButtonTextRes.stringResource.desc().localized(),
                    modifier = Modifier.fillMaxWidth(),
                    buttonModifier = Modifier.defaultMinSize(minHeight = 44.dp).fillMaxWidth(),
                    fontSize = FontType.REGULAR,
                    onClick = onClickButton,
                    enabled = uiModel.mainButtonEnabled
                )
                MangalaWrappedTextButton(
                    text = MR.strings.all_contact_support.desc().localized(),
                    fontSize = FontType.REGULAR,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onClickContactSupport,
                    isEnabled = true,
                    color = Colors.darkDarkGray
                )
                VerticalSpacer(Spacing.SMALL)
            }
        }
    }

    @Composable
    fun StepIndicatorRow(
        stepStatuses: List<CreateAccountStepStatus>
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                stepStatuses.forEachIndexed { index, step ->
                    val statusColor = when (step) {
                        CreateAccountStepStatus.DONE, CreateAccountStepStatus.DONE_FOCUSED -> Colors.teal
                        CreateAccountStepStatus.FAILED -> Colors.deepCrimson
                        else -> Colors.darkDarkGray
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Box(
                            modifier = Modifier.size(Dimensions.ButtonIconSize).clip(CircleShape)
                                .border(
                                    width = 1.dp,
                                    color = statusColor,
                                    shape = CircleShape
                                ), contentAlignment = Alignment.Center
                        ) {
                            when (step) {
                                CreateAccountStepStatus.DONE, CreateAccountStepStatus.DONE_FOCUSED -> {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Completed",
                                        tint = Colors.white,
                                        modifier = Modifier.background(Colors.teal).fillMaxSize()
                                            .size(10.dp).padding(2.dp)
                                    )
                                }

                                CreateAccountStepStatus.FAILED -> {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Failed",
                                        tint = Colors.white,
                                        modifier = Modifier.background(Colors.deepCrimson)
                                            .fillMaxSize()
                                            .size(10.dp).padding(2.dp)
                                    )
                                }

                                else -> {
                                    Text(
                                        text = (index + 1).toString(),
                                        color = Colors.darkDarkGray,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 14.sp,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }

                        }
                    }

                    if (stepStatuses.size > 1 && index < stepStatuses.size - 1) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(2.dp)
                            ) {
                                drawLine(
                                    color = Colors.paleGray,
                                    start = Offset(0f, size.height / 2),
                                    end = Offset(size.width, size.height / 2),
                                    strokeWidth = 2f
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun StepsVerticalList(uiModel: IapCreateAccountUiModel) {
        MaxWidthColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.MEDIUM),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StepText(
                error = (uiModel.currentStep as? CreateAccountStep.Payment)?.error,
                label = uiModel.paymentStepText.desc().localized(),
                statusStep = uiModel.paymentStepStatus
            )
            StepText(
                error = (uiModel.currentStep as? CreateAccountStep.CreateAccount)?.error,
                label = uiModel.createAccountStepText.desc().localized(),
                statusStep = uiModel.createAccountStepStatus
            )
            StepText(
                error = null,
                label = if (uiModel.isPinSetUp) MR.strings.message_step_3_create_account_payment_step_explore.desc()
                    .localized() else MR.strings.message_step_3_create_account_payment_step_setup_pin.desc().localized(),
                statusStep = uiModel.exploreMangalaOrSetupPinStepStatus
            )
        }
    }

    @Composable
    fun StepText(
        error: WrappedStringResource?,
        label: String,
        statusStep: CreateAccountStepStatus
    ) {
        val targetFontSize =
            if (statusStep.isInFocus) {
                FontType.TITLE_3.value
            } else {
                FontType.SMALL.value
            }

        val targetColor = when (statusStep) {
            CreateAccountStepStatus.DONE, CreateAccountStepStatus.DONE_FOCUSED -> Colors.teal
            CreateAccountStepStatus.IN_PROGRESS, CreateAccountStepStatus.IN_PROGRESS_PROMPT -> Colors.darkDarkGray
            CreateAccountStepStatus.NOT_SELECTED, CreateAccountStepStatus.DEFAULT -> Colors.gray
            CreateAccountStepStatus.FAILED -> Colors.deepCrimson
        }

        val animatedColor by animateColorAsState(
            targetValue = targetColor,
            animationSpec = if (statusStep == CreateAccountStepStatus.DONE) {
                tween(
                    durationMillis = COLOR_ANIMATION_DURATION_TO_DONE_MS,
                    easing = FastOutSlowInEasing
                )
            } else {
                tween(durationMillis = 800, easing = FastOutSlowInEasing)
            }
        )

        val animatedFontSize by animateFloatAsState(
            targetValue = targetFontSize,
            animationSpec = tween(
                durationMillis = FONT_SIZE_ANIMATION_DURATION_TO_DONE_MS,
                easing = FastOutSlowInEasing
            )
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var dotCount by remember { mutableStateOf(0) }

            LaunchedEffect(statusStep) {
                while (statusStep == CreateAccountStepStatus.IN_PROGRESS) {
                    delay(LOADING_ELLIPSIS_ANIMATION_DELAY_MS)
                    dotCount = (dotCount + 1) % 4
                }

                if (statusStep != CreateAccountStepStatus.IN_PROGRESS) {
                    dotCount = 0
                }
            }

            TextNormal(
                text = label + ".".repeat(dotCount),
                fontSize = animatedFontSize.sp,
                color = animatedColor,
                fontWeight = if (statusStep.isInFocus) FontWeight.SemiBold else FontWeight.Normal,
            )
            VerticalSpacer(Spacing.TINY)
            if (error != null) {
                TextNormal(
                    text = error.resolve().desc().localized(),
                    color = Colors.deepCrimson,
                    fontSize = FontType.SMALL,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    companion object {
        private const val LOADING_ELLIPSIS_ANIMATION_DELAY_MS = 750L
        private const val COLOR_ANIMATION_DURATION_TO_DONE_MS = 100
        private const val FONT_SIZE_ANIMATION_DURATION_TO_DONE_MS = 1500
    }
}