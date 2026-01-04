package com.mangala.wallet.features.send_base.selectrecipienttype

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SendExistingContact
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SendNewContact
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.HorizontalSpacer
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class SelectRecipientTypeScreen(
    private val accountId: String,
    private val networkType: String
) : BaseScreen<SelectRecipientTypeScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.SEND_SELECT_RECIPIENT_TYPE
    override val screenClassName: String = SelectRecipientTypeScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): SelectRecipientTypeScreenModel {
        return getScreenModel(
            parameters = {
                parametersOf(
                    accountId
                )
            }
        )
    }

    @Composable
    override fun ScreenContent(screenModel: SelectRecipientTypeScreenModel) {
        val navigator = LocalNavigator.current
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val parentNavigator = navigator?.parent ?: return // parent, non-bottom sheet navigator

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value
        SelectRecipientTypeScreen(
            uiState,
            onClickSendExistingContact = {
                val screen = ScreenRegistry.get(
                    SharedScreen.SendContactListScreen(
                        accountId = accountId
                    )
                )
                parentNavigator.push(screen)
                bottomSheetNavigator.hide()
            },
            onClickSendNewContact = {
                val screen = ScreenRegistry.get(
                    SharedScreen.Step2SelectNetwork(
                        accountId = accountId,
                        networkType = networkType,
                        address = null
                    )
                )
                parentNavigator.push(screen)
                bottomSheetNavigator.hide()
            },
        )
    }

    @Composable
    fun SelectRecipientTypeScreen(
        uiState: SelectRecipientTypeScreenUiState,
        onClickSendExistingContact: () -> Unit,
        onClickSendNewContact: () -> Unit,
    ) {
        val isLoading = uiState is SelectRecipientTypeScreenUiState.Loading

        OnboardingGradientBackground(modifier = Modifier.fillMaxWidth()) {
            MaxWidthColumn(
                Modifier.padding(
                    top = Dimensions.Padding.default,
                    start = Dimensions.Padding.default,
                    end = Dimensions.Padding.default
                ).navigationBarsPadding()
            ) {
                TextNormal(
                    text = MR.strings.title_select_recipient_type.desc().localized(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                )
                VerticalSpacer(Spacing.XXXBASE)
                Item(
                    MangalaWalletPack.SendExistingContact,
                    MR.strings.label_select_recipient_existing_contact.desc().localized(),
                    isLoading,
                    onClickSendExistingContact
                )
                VerticalSpacer(Spacing.SMALL)
                Item(
                    MangalaWalletPack.SendNewContact,
                    MR.strings.label_select_recipient_new_contact.desc().localized(),
                    isLoading,
                    onClickSendNewContact
                )
                VerticalSpacer(172.dp)
            }
        }
    }

    @Composable
    private fun Item(
        icon: ImageVector,
        text: String,
        isLoading: Boolean,
        onClick: () -> Unit
    ) {
        MaxWidthRow(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = MaterialTheme.mangalaColors.border,
                    shape = RoundedCornerShape(size = CornerRadius.Small)
                )
                .clip(RoundedCornerShape(size = CornerRadius.Small))
                .clickable {
                    onClick()
                }
                .padding(
                    horizontal = Dimensions.Padding.default,
                    vertical = Dimensions.Padding.small
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .mangalaWalletPlaceholder(isLoading)
                    .clickable(onClick = onClick),
                tint = MaterialTheme.mangalaColors.iconSecondary
            )
            HorizontalSpacer(Spacing.TINY)
            TextNormal(
                modifier = Modifier.fillMaxWidth().mangalaWalletPlaceholder(isLoading),
                text = text,
                color = MaterialTheme.mangalaColors.textPrimary
            )
        }
    }
}