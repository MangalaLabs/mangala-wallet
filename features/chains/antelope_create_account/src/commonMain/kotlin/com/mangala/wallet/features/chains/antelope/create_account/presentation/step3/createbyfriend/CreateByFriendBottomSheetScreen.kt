package com.mangala.wallet.features.chains.antelope.create_account.presentation.step3.createbyfriend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Frame
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Share
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.qrcode.ComposeUIWrapper
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextSubTitle
import com.mangala.wallet.ui.component.HorizontalSpacer
import com.mangala.wallet.ui.component.MangalaButton
import com.mangala.wallet.ui.component.MangalaTopBarTitleInMiddle
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.ShareFactory
import com.mangala.wallet.utils.ToastFactory
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class CreateByFriendBottomSheetScreen(
    private val accountName: String,
    private val eosOwnerPrivateKey: String? = null,
    private val eosActivePrivateKey: String? = null,
    @Transient private val onAccountCreated: () -> Unit
) : BaseScreen<CreateByFriendBottomSheetScreenModel>(), KoinComponent {

    override val screenName: String =
        MangalaAnalytics.Screens.ANTELOPE_CREATE_ACCOUNT_BY_FRIEND_BOTTOM_SHEET
    override val screenClassName: String =
        CreateByFriendBottomSheetScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Transient
    private val shareFactory = get<ShareFactory>()

    @Transient
    private val toastFactory = get<ToastFactory>()


    @Composable
    override fun createScreenModel(): CreateByFriendBottomSheetScreenModel {
        return getScreenModel<CreateByFriendBottomSheetScreenModel> {
            parametersOf(
                accountName,
                eosOwnerPrivateKey,
                eosActivePrivateKey
            )
        }
    }

    @Composable
    override fun ScreenContent(screenModel: CreateByFriendBottomSheetScreenModel) {
        val bottomNavigator = LocalBottomSheetNavigator.current

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        MaxWidthColumn(
            Modifier
                .fillMaxSize(Dimensions.fullScreenBottomSheetFraction)
                .background(Colors.appleBg)
                .windowInsetsPadding(WindowInsets.navigationBars),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            MaxWidthColumn {
                MangalaTopBarTitleInMiddle(
                    titleTopBar = MR.strings.title_create_by_friend.desc().localized(),
                    onBackClicked = { bottomNavigator.hide() }
                )
                MaxWidthColumn(
                    Modifier.padding(Dimensions.Padding.default).verticalScroll(
                        rememberScrollState()
                    )
                ) {
                    VerticalSpacer(Spacing.BASE)
                    TextSubTitle(
                        text = MR.strings.title_create_by_friend_content.desc().localized(),
                        fontWeight = FontWeight.Medium,
                        color = Colors.darkDarkGray
                    )
                    VerticalSpacer(Spacing.TINY)
                    TextDescription2(
                        text = MR.strings.label_create_by_friend_description.desc().localized(),
                        color = Colors.caption
                    )
                    VerticalSpacer(Spacing.BIG)
                    if (uiState is CreateByFriendBottomSheetUiState.Ready) {
                        MaxWidthColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                            val composeUIWrapper = remember { ComposeUIWrapper() }
                            Box(
                                Modifier
                                    .size(220.dp)
                                    .clip(RoundedCornerShape(CornerRadius.Medium))
                                    .background(Colors.white)
                                    .padding(Dimensions.Padding.default)
                            ) {
                                composeUIWrapper.QRCodeImage(
                                    uiState.encodedRequest,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            VerticalSpacer(Spacing.SMALL)
                            Row {

                                ActionButton(
                                    MangalaWalletPack.Share,
                                    MR.strings.button_create_by_friend_share_qr.desc().localized()
                                ) { // TODO: Extract string resource
                                    val tempFileUri =
                                        composeUIWrapper.saveTempBitmap(uiState.encodedRequest)
                                    tempFileUri?.let { shareFactory.shareImage(it) }
                                }
                                HorizontalSpacer(Spacing.BASE)
                                val saveSuccessText =
                                    MR.strings.all_save_qr_success.desc().localized()
                                ActionButton(
                                    MangalaWalletPack.Frame,
                                    MR.strings.button_create_by_friend_save_qr.desc().localized()
                                ) {
                                    val uri =
                                        composeUIWrapper.saveBitmapToFile(uiState.encodedRequest)
                                    if (uri != null) {
                                        toastFactory.show(saveSuccessText)
                                    }
                                }
                            }
                        }
                    } else if (uiState is CreateByFriendBottomSheetUiState.AccountCreated) {
                        onAccountCreated()
                        bottomNavigator.hide()
                    }
                }
            }
            MangalaButton(
                label = MR.strings.button_create_by_friend_save_account.desc().localized(),
                onClick = screenModel::onClickSaveAccount,
                enabled = uiState is CreateByFriendBottomSheetUiState.Ready && uiState.isCheckingAccountCreated.not(),
                style = MangalaTypography.Size17Medium(),
                modifier = Modifier.padding(horizontal = Dimensions.Padding.default).fillMaxWidth(),
                disabledBackgroundColor = Colors.white,
                disabledContentColor = Colors.mistGray
            )
        }
    }

    @Composable
    fun ActionButton(icon: ImageVector, text: String, onClick: () -> Unit) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onClick() }) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Colors.white)
                    .padding(10.dp)
            ) {
                Icon(
                    modifier = Modifier.fillMaxSize(),
                    imageVector = icon,
                    contentDescription = null,
                    tint = Colors.darkGray,
                )
            }
            VerticalSpacer(Spacing.TINY)
            TextDescription2(
                text,
                color = Colors.darkDarkGray
            )
        }
    }
}