package com.mangala.wallet.features.receive.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Elevation
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.BackgroundDefaultQrDark
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcAdd
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcCopy
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcEdit
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcSave
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcShare
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcWarning
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.token.TokenEntity
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.qrcode.ComposeUIWrapper
import com.mangala.wallet.ui.GalleryHelper
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.ClipboardFactory
import com.mangala.wallet.utils.ShareFactory
import com.mangala.wallet.utils.ToastFactory
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class ReceiveTokenScreen(
    private val accountId: String?,
    private val address: String? = null,
    private val networkType: NetworkType,
    private val initialBlockchainUid: String?,
    @Transient private val onBackPressedButton: () -> Unit = {}
) : BaseScreen<ReceiveTokenScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.RECEIVE_TOKEN
    override val screenClassName: String = ReceiveTokenScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): ReceiveTokenScreenModel = getScreenModel(
        parameters = {
            parametersOf(
                accountId,
                address,
                networkType,
                initialBlockchainUid
            )
        }
    )

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: ReceiveTokenScreenModel) {
        val clipboardFactory = get<ClipboardFactory>()
        val shareFactory = get<ShareFactory>()
        val toastFactory = get<ToastFactory>()
        val galleryHelper = get<GalleryHelper>()
        val scope = rememberCoroutineScope()

        val parentNavigator = LocalNavigator.currentOrThrow

        MangalaBottomSheetNavigator(
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.BottomSheet,
                topEnd = CornerRadius.BottomSheet,
            )
        ) {
            val bottomSheetNavigator = LocalBottomSheetNavigator.current

            QRScreen(
                screenModel = screenModel,
                clipboardFactory = clipboardFactory,
                shareFactory = shareFactory,
                toastFactory = toastFactory,
                galleryHelper = galleryHelper,
                onBackClick = {
                    onBackPressedButton()
                    parentNavigator.pop()
                },
                onEditClick = {
                    val screen = ScreenRegistry.get(
                        SharedScreen.ReceiveTokenPickAccountScreen(
                            onClickAccountInfo = { accountId ->
                                screenModel.onSelectAccount(accountId)
                                bottomSheetNavigator.hide()
                            },
                            networkType = networkType
                        )
                    )
                    bottomSheetNavigator.show(screen)
                },
                onAddAmountClick = { currentAmount, nativeCoin ->
                    bottomSheetNavigator.show(
                        AddAmountToReceiveQrScreen(
                            initialAmount = currentAmount,
                            coinName = nativeCoin.reference.orEmpty(),
                            decimals = nativeCoin.decimals,
                            onSaveAmount = {
                                screenModel.onAmountChange(it)
                                bottomSheetNavigator.hide()
                            }
                        )
                    )
                },
                onEditAmountClick = { currentAmount, nativeCoin ->
                    bottomSheetNavigator.show(
                        EditReceiveAmountScreen(
                            onEditAmount = {
                                scope.launch {
                                    bottomSheetNavigator.hide()
                                    delay(100)
                                    bottomSheetNavigator.show(
                                        AddAmountToReceiveQrScreen(
                                            initialAmount = currentAmount,
                                            coinName = nativeCoin.reference.orEmpty(),
                                            decimals = nativeCoin.decimals,
                                            onSaveAmount = {
                                                screenModel.onAmountChange(it)
                                                bottomSheetNavigator.hide()
                                            }
                                        )
                                    )
                                }
                            },
                            onRemoveAmount = {
                                screenModel.onAmountChange("")
                                bottomSheetNavigator.hide()
                            }
                        )
                    )
                }
            )
        }
    }

    @Composable
    fun QRScreen(
        screenModel: ReceiveTokenScreenModel,
        clipboardFactory: ClipboardFactory,
        shareFactory: ShareFactory,
        toastFactory: ToastFactory,
        galleryHelper: GalleryHelper,
        onBackClick: () -> Unit,
        onEditClick: () -> Unit,
        onAddAmountClick: (String, TokenEntity) -> Unit,
        onEditAmountClick: (String, TokenEntity) -> Unit,
    ) {
        val uiState = screenModel.uiState.collectAsStateMultiplatform()
        val scope = rememberCoroutineScope()
        val graphicsLayer = rememberGraphicsLayer()
        val composeUIWrapper = remember { ComposeUIWrapper() }

        val address = remember {
            derivedStateOf {
                when (val it = uiState.value) {
                    is AccountUiModelReceiveTokenUiState.Evm -> it.address.orEmpty()
                    is AccountUiModelReceiveTokenUiState.Antelope -> it.address.orEmpty()
                    AccountUiModelReceiveTokenUiState.Initial -> ""
                }
            }
        }

        val coinName = remember {
            derivedStateOf {
                uiState.value.nativeCoin.reference.orEmpty()
            }
        }

        MaxSizeBox(
            modifier = Modifier
                .drawWithContent {
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    drawLayer(graphicsLayer)
                },
        ) {
            Image(
                imageVector = MangalaWalletPack.BackgroundDefaultQrDark,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            // Screen Content
            MaxSizeColumn(
                modifier = Modifier
                    .padding(horizontal = Dimensions.Padding.default)
                    .safeDrawingPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(Spacing.BASE))

                TitleAndInstruction(coinName.value)

                Spacer(modifier = Modifier.height(Spacing.XBASE))

                QRCodeCard(
                    address = address.value,
                    network = uiState.value.selectedNetwork,
                    qrCodeData = uiState.value.qrCodeData,
                    composeUIWrapper = composeUIWrapper
                )

                Spacer(modifier = Modifier.height(Spacing.MEDIUM))

                if (uiState.value.amount != null)
                    AmountSection(
                        coinName = coinName.value,
                        amount = uiState.value.amount.orEmpty(),
                        onEditClick = {},
                        shouldShowEditIcon = false,
                    )

                Spacer(modifier = Modifier.weight(1f))

                WarningMessage(
                    networkName = uiState.value.selectedNetwork.name,
                    coinName = coinName.value
                )

                Spacer(modifier = Modifier.height(Spacing.BASE))
            }
        }

        MaxSizeBox {
            Image(
                imageVector = MangalaWalletPack.BackgroundDefaultQrDark,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            // Screen Content
            MaxSizeColumn(
                modifier = Modifier
                    .padding(horizontal = Dimensions.Padding.default)
                    .safeDrawingPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopBar(onBackClick = onBackClick, onEditClick = onEditClick)

                Spacer(modifier = Modifier.height(Spacing.BASE))

                TitleAndInstruction(coinName.value)

                Spacer(modifier = Modifier.height(Spacing.XBASE))

                QRCodeCard(
                    address = address.value,
                    network = uiState.value.selectedNetwork,
                    qrCodeData = uiState.value.qrCodeData,
                    composeUIWrapper = composeUIWrapper
                )

                Spacer(modifier = Modifier.height(Spacing.MEDIUM))

                if (uiState.value.amount == null)
                    AddAmountButton(
                        onClick = {
                            onAddAmountClick(
                                uiState.value.amount.orEmpty(),
                                uiState.value.nativeCoin
                            )
                        }
                    )
                else
                    AmountSection(
                        coinName = coinName.value,
                        amount = uiState.value.amount.orEmpty(),
                        onEditClick = {
                            onEditAmountClick(
                                uiState.value.amount.orEmpty(),
                                uiState.value.nativeCoin
                            )
                        }
                    )


                Spacer(modifier = Modifier.weight(1f))

                WarningMessage(
                    networkName = uiState.value.selectedNetwork.name,
                    coinName = coinName.value
                )

                Spacer(modifier = Modifier.height(Spacing.BASE))

                val copiedToastMessage =
                    MR.strings.all_message_copied_address.desc().localized()
                val savedImageToastMessage =
                    MR.strings.all_save_qr_success.desc().localized()
                val savedImageFailToastMessage =
                    MR.strings.all_save_qr_fail.desc().localized()

                BottomActionButtons(
                    onSaveClick = {
                        scope.launch {
                            val bitmap = graphicsLayer.toImageBitmap()
                            val saveResult = galleryHelper.saveImageToGallery(bitmap)
                            toastFactory.show(
                                if (saveResult) savedImageToastMessage
                                else savedImageFailToastMessage
                            )
                        }
                    },
                    onCopyClick = {
                        clipboardFactory.copyText(
                            "Mangala copy",
                            uiState.value.qrCodeData
                        )
                        toastFactory.show(copiedToastMessage)
                    },
                    onShareClick = {
                        shareFactory.shareText(
                            "Mangala share via",
                            uiState.value.qrCodeData
                        )
                    }
                )
            }
        }
    }

    @Composable
    private fun TopBar(
        onBackClick: () -> Unit,
        onEditClick: () -> Unit,
    ) {
        MangalaWalletTopBarCenteredTitle(
            title = "",
            onBackClicked = onBackClick,
            trailingButton = {
//            IconButton(onClick = onEditClick) {
//                Icon(
//                    imageVector = vectorResource(Res.drawable.ic_edit),
//                    contentDescription = "Edit",
//                    tint = MaterialTheme.mangalaColors.iconPrimary,
//                )
//            }
            }
        )
    }

    @Composable
    private fun TitleAndInstruction(coinName: String) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = MR.strings.title_receiveToken.format(coinName).localized(),
                style = MangalaTypography.Size17SemiBold(),
                color = MaterialTheme.mangalaColors.textPrimary
            )

            Spacer(modifier = Modifier.height(Spacing.XTINY))

            Text(
                text = MR.strings.subtitle_receiveToken.desc().localized(),
                style = MangalaTypography.Size14Medium(),
                color = MaterialTheme.mangalaColors.textSecondary,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    private fun QRCodeCard(
        address: String,
        network: BlockchainNetworkData,
        qrCodeData: String,
        composeUIWrapper: ComposeUIWrapper
    ) {
        Card(
            shape = RoundedCornerShape(CornerRadius.Small),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = Elevation.Medium
            ),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(
                    vertical = Dimensions.Padding.default,
                    horizontal = Dimensions.Padding.large
                )
            ) {
                Text(
                    text = address.ifBlank { "placeholder" },
                    style = MangalaTypography.Size17SemiBold(),
                    color = ColorsNew.vaultBrandColor,
                    modifier = Modifier.mangalaWalletPlaceholder(visible = address.isBlank())
                )

                Spacer(modifier = Modifier.height(Spacing.TINY))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(180.dp)
                        .mangalaWalletPlaceholder(visible = qrCodeData.isBlank())
                ) {
                    // QR code image
                    composeUIWrapper.QRCodeImage(qrCodeData, modifier = Modifier.size(180.dp))

                    LocalImage(
                        modifier = Modifier.size(50.dp)
                            .background(ColorsNew.white, shape = CircleShape),
                        imageResource = network.localImage,
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.TINY))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
                    modifier = Modifier.width(180.dp)
                ) {
                    Text(
                        text = "Mangala",
                        style = MangalaTypography.Size12SemiBold(),
                        color = Color(0xFF4A6FE3),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(
                                color = Color(0x1A4A6FE3),
                                shape = RoundedCornerShape(CornerRadius.Medium)
                            )
                            .weight(1f)
                            .padding(
                                horizontal = Dimensions.Padding.half,
                                vertical = Dimensions.Padding.quarter
                            )
                    )

                    Text(
                        text = network.name,
                        style = MangalaTypography.Size12SemiBold(),
                        color = Color(0xFF00B4D8),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(
                                color = Color(0x1A00B4D8),
                                shape = RoundedCornerShape(CornerRadius.Medium)
                            )
                            .weight(1f)
                            .padding(
                                horizontal = Dimensions.Padding.half,
                                vertical = Dimensions.Padding.quarter
                            )
                    )
                }
            }
        }
    }

    @Composable
    private fun AddAmountButton(onClick: () -> Unit) {
        TextButton(
            onClick = onClick,
            modifier = Modifier.padding(Dimensions.Padding.quarter),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = MangalaWalletPack.IcAdd,
                    contentDescription = "Add",
                    tint = MaterialTheme.mangalaColors.textLink
                )

                Text(
                    text = MR.strings.button_receiveToken_addAmount.desc().localized(),
                    style = MangalaTypography.Size14Medium(),
                    color = MaterialTheme.mangalaColors.textLink,
                )
            }
        }
    }

    @Composable
    private fun AmountSection(
        coinName: String,
        amount: String,
        shouldShowEditIcon: Boolean = true,
        onEditClick: () -> Unit,
    ) {
        TextButton(onClick = onEditClick) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(coinName)
                        append(" ")
                        withStyle(
                            SpanStyle(color = MaterialTheme.mangalaColors.textLink)
                        ) {
                            append(amount)
                        }
                    },
                    style = MangalaTypography.Size17SemiBold(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                )

                Spacer(modifier = Modifier.width(Spacing.TINY))

                if (shouldShowEditIcon)
                    Icon(
                        imageVector = MangalaWalletPack.IcEdit,
                        contentDescription = "Edit amount",
                        tint = MaterialTheme.mangalaColors.textLink,
                        modifier = Modifier.size(Dimensions.IconSizeNextToText)
                    )
            }
        }
    }

    @Composable
    fun WarningMessage(networkName: String, coinName: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.mangalaColors.bgInnerCard,
                    shape = RoundedCornerShape(CornerRadius.Small)
                )
                .border(
                    border = BorderStroke(1.dp, MaterialTheme.mangalaColors.border),
                    shape = RoundedCornerShape(CornerRadius.Small)
                )
                .padding(
                    horizontal = Dimensions.Padding.default,
                    vertical = Dimensions.Padding.half
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = MangalaWalletPack.IcWarning,
                contentDescription = "Warning",
                tint = Color.Unspecified,
            )

            Spacer(modifier = Modifier.width(Spacing.XSMALL))

            Column {
                val titleAnnotatedString = buildAnnotatedString {
                    val tokenPart =
                        MR.strings.spannedPart_receiveToken_warningBanner_title_tokenPart
                            .format(coinName)
                            .localized()

                    val networkPart =
                        MR.strings.spannedPart_receiveToken_warningBanner_title_networkPart
                            .format(networkName)
                            .localized()

                    val fullText = MR.strings.title_receiveToken_warningBanner
                        .format(tokenPart, networkPart)
                        .localized()

                    append(fullText)

                    val tokenIndex = fullText.indexOf(tokenPart)
                    if (tokenIndex >= 0) {
                        addStyle(
                            style = SpanStyle(color = MaterialTheme.mangalaColors.textPrimary),
                            start = tokenIndex,
                            end = tokenIndex + tokenPart.length
                        )
                    }

                    val networkIndex = fullText.indexOf(networkPart)
                    if (networkIndex >= 0) {
                        addStyle(
                            style = SpanStyle(color = MaterialTheme.mangalaColors.textPrimary),
                            start = networkIndex,
                            end = networkIndex + networkPart.length
                        )
                    }
                }

                Text(
                    text = titleAnnotatedString,
                    style = MangalaTypography.Size12Regular(),
                    color = MaterialTheme.mangalaColors.textSecondary,
                )

                Spacer(Modifier.height(Spacing.MICRO))

                val subtitleAnnotatedString = buildAnnotatedString {
                    val riskPart = MR.strings.spannedPart_receiveToken_warningBanner_subtitle.desc()
                        .localized()

                    val fullText =
                        MR.strings.subtitle_receiveToken_warningBanner.format(riskPart).localized()

                    append(fullText)

                    val riskIndex = fullText.indexOf(riskPart)
                    if (riskIndex >= 0) {
                        addStyle(
                            style = SpanStyle(color = MaterialTheme.mangalaColors.textPrimary),
                            start = riskIndex,
                            end = riskIndex + riskPart.length
                        )
                    }
                }

                Text(
                    text = subtitleAnnotatedString,
                    style = MangalaTypography.Size10Regular(),
                    color = MaterialTheme.mangalaColors.textSecondary,
                )
            }
        }
    }

    @Composable
    fun BottomActionButtons(
        onSaveClick: () -> Unit,
        onCopyClick: () -> Unit,
        onShareClick: () -> Unit,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = Dimensions.Padding.default),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                text = MR.strings.all_save.desc().localized(),
                icon = MangalaWalletPack.IcSave,
                onClick = onSaveClick
            )

            ActionButton(
                text = MR.strings.all_copy.desc().localized(),
                icon = MangalaWalletPack.IcCopy,
                onClick = onCopyClick,
                buttonSize = 44.dp,
                iconSize = 20.dp,
            )

            ActionButton(
                text = MR.strings.all_share.desc().localized(),
                icon = MangalaWalletPack.IcShare,
                onClick = onShareClick
            )
        }
    }

    @Composable
    fun ActionButton(
        text: String,
        icon: ImageVector,
        onClick: () -> Unit,
        buttonSize: Dp = Dimensions.RoundedActionIconButtonSize,
        iconSize: Dp = Dimensions.IconSize,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = Dimensions.Padding.half)
        ) {
            Card(
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.mangalaColors.bgInnerCard
                ),
                modifier = Modifier.size(buttonSize),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = Elevation.Medium
                ),
            ) {
                IconButton(onClick = onClick) {
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        tint = MaterialTheme.mangalaColors.iconPrimary,
                        modifier = Modifier.size(iconSize)
                    )
                }
            }

            Spacer(modifier = Modifier.height(Spacing.TINY))

            Text(
                text = text,
                style = MangalaTypography.Size12Regular(),
                color = MaterialTheme.mangalaColors.textPrimary,
            )
        }
    }
}