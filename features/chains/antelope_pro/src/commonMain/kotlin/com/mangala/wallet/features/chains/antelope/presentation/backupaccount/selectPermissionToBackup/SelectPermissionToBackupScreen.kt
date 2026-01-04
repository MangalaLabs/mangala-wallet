package com.mangala.wallet.features.chains.antelope.presentation.backupaccount.selectPermissionToBackup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.AntelopeAccountVisualize
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowRight
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.modifier.roundedCornerItemShape
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class SelectPermissionToBackupScreen(private val accountName: String) :
    BaseScreen<SelectPermissionToBackupScreenModel>() {

    override val screenName: String =
        MangalaAnalytics.Screens.ANTELOPE_BACKUP_ACCOUNT_SELECT_PERMISSION
    override val screenClassName: String =
        SelectPermissionToBackupScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): SelectPermissionToBackupScreenModel =
        getScreenModel<SelectPermissionToBackupScreenModel>(
            parameters = {
                parametersOf(accountName)
            }
        )

    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun ScreenContent(screenModel: SelectPermissionToBackupScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        OnboardingGradientBackground {
            MaxSizeColumn(
                modifier = Modifier
                    .safeDrawingPadding(),
            ) {
                MangalaWalletTopBarCenteredTitle(
                    title = MR.strings.all_antelope_export_private_key.desc().localized(),
                    onBackClicked = navigator::pop
                )

                SelectPermissionToBackupScreen(
                    screenModel = screenModel,
                    onClickPermission = { permission ->
                        val pinScreen = ScreenRegistry.get(
                            SharedScreen.UnlockPinScreen(
                                unlockPinCase = SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                                antelopeAccountName = null,
                                onUnlockSuccess = {
                                    val screen = ScreenRegistry.get(
                                        SharedScreen.BackupAntelopePrivateKeyScreen(
                                            accountName = accountName,
                                            permissionName = permission
                                        )
                                    )
                                    navigator.replace(screen)
                                }
                            )
                        )

                        navigator.push(pinScreen)
                    }
                )
            }
        }
    }

    @Composable
    private fun SelectPermissionToBackupScreen(
        screenModel: SelectPermissionToBackupScreenModel,
        onClickPermission: (permission: String) -> Unit
    ) {
        val accountPermissions = screenModel.accountPermissions.collectAsStateMultiplatform()
        MaxSizeColumn(
            modifier = Modifier
                .padding(Dimensions.Padding.default),
        ) {
            Spacer(modifier = Modifier.height(Dimensions.Height.medium))

            TextDescription2(
                text = MR.strings.title_export_private_key_select_permission_selected_account.desc()
                    .localized(),
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(Dimensions.Height.small))

            AccountItem(
                accountName = accountName,
                shape = RoundedCornerShape(CornerRadius.Small)
            )

            Spacer(modifier = Modifier.height(Dimensions.Height.medium))

            TextDescription2(
                text = MR.strings.title_export_private_key_select_permission_choose_permission.desc()
                    .localized(),
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(Dimensions.Height.small))

            LazyColumn {
                items(
                    items = accountPermissions.value,
                    key = { it }
                ) {
                    PerMissionItem(
                        shape = roundedCornerItemShape(
                            accountPermissions.value, accountPermissions.value.indexOf(it)
                        ),
                        permission = it,
                        onClick = {
                            onClickPermission(it)
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun PerMissionItem(
        permission: String,
        shape: Shape,
        onClick: () -> Unit
    ) {
        MaxWidthRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(MaterialTheme.mangalaColors.bgInnerCard)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = Dimensions.Padding.default,
                    vertical = Dimensions.Padding.medium
                ),
        ) {
            TextDescription2(
                text = permission,
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                MangalaWalletPack.ArrowRight,
                contentDescription = null,
                tint = MaterialTheme.mangalaColors.iconSecondary,
                modifier = Modifier.size(14.dp)
            )
        }
    }

    @Composable
    fun AccountItem(
        accountName: String,
        shape: Shape
    ) {
        MaxWidthRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = MaterialTheme.mangalaColors.bgInnerCard,
                    shape = shape
                )
                .padding(Dimensions.Padding.default),
        ) {
            Icon(
                MangalaWalletPack.AntelopeAccountVisualize,
                contentDescription = null,
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(Spacing.XSMALL))
            TextNormal(
                text = accountName,
                color = MaterialTheme.mangalaColors.textPrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}