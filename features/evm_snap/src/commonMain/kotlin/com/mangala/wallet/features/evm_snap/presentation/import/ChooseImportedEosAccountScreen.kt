package com.mangala.wallet.features.evm_snap.presentation.import

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope.presentation.importaccount.step2.Step2ImportAccountSelectAccountScreen
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextSubTitle
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.resolve
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class ChooseImportedEosAccountScreen(
    private val eosOwnerPrivateKey: String,
    private val eosActivePrivateKey: String
): BaseScreen<ChooseImportedEosAccountScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_CREATE_ACCOUNT_VIA_EVM_CHOOSE_IMPORTED
    override val screenClassName: String = ChooseImportedEosAccountScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): ChooseImportedEosAccountScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: ChooseImportedEosAccountScreenModel) {
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value
        val localNavigator = LocalNavigator.currentOrThrow
        val homeScreen = rememberScreen(SharedScreen.HomeScreen())

        MaxWidthColumn(
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight()
                .background(Colors.appleBg).windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(
                    start = Dimensions.Padding.default,
                    end = Dimensions.Padding.default,
                    bottom = Dimensions.Padding.default
                )
        ) {
            MangalaWalletTopBar(
                text = MR.strings.title_import_evm_snap_account.desc().localized(),
                onBackClicked = { localNavigator.replaceAll(homeScreen) },
                color = Colors.darkDarkGray,
                fontSize = FontType.REGULAR,
                fontWeight = FontWeight.SemiBold
            )
            VerticalSpacer(Spacing.XTINY)
            when(uiState) {
                is ChooseImportedEosAccountUIState.Loading -> {
                    ChooseImportedEosAccountScreenContent(
                        screenModel = screenModel
                    )
                }

                is ChooseImportedEosAccountUIState.Error -> {
                    TextSubTitle(
                        text = MR.strings.title_choose_imported_eos_account_with_private_key_error.desc().localized(),
                        fontWeight = FontWeight.Medium,
                        color = Colors.darkDarkGray
                    )

                    MaxWidthColumn(
                        modifier = Modifier.fillMaxWidth()
                            .padding(vertical = Dimensions.Padding.default),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextNormal(
                            text = uiState.message.resolve(),
                            fontWeight = FontWeight.Normal,
                            color = Colors.darkDarkGray,
                            fontSize = FontType.SMALL
                        )
                    }
                }

                is ChooseImportedEosAccountUIState.Success -> {
                    localNavigator.push(
                        Step2ImportAccountSelectAccountScreen(
                            privateKey = uiState.privateKey,
                            accountsByAuthorizers = ArrayList(uiState.accounts)
                        )
                    )
                }
            }
        }
    }

    @Composable
    fun ChooseImportedEosAccountScreenContent(
        screenModel: ChooseImportedEosAccountScreenModel
    ) {
        var selectedPrivateKey by remember { mutableStateOf<String?>(null) }
        TextSubTitle(
            text = MR.strings.title_choose_imported_eos_account_with_private_key.desc().localized(),
            fontWeight = FontWeight.Medium,
            color = Colors.darkGray,
            modifier = Modifier.padding(bottom = Dimensions.Padding.half)
        )
        MaxWidthColumn(
            verticalArrangement = Arrangement.Top
        ) {
            TextNormal(
                text = MR.strings.label_choose_imported_eos_account_with_private_key_description.desc().localized(),
                fontWeight = FontWeight.Normal,
                color = Colors.darkDarkGray,
                fontSize = FontType.SMALL,
                modifier = Modifier.padding(bottom = Dimensions.Padding.default)
            )
            PrivateKeyOptionCard(
                title = StringDesc.ResourceFormatted(
                    MR.strings.title_choose_permission_private_key_to_import,
                    "Owner"
                ).localized(),
                description = MR.strings.label_warning_when_choose_owner_permission_description.desc().localized(),
                privateKey = eosOwnerPrivateKey,
                selectedPrivateKey = selectedPrivateKey,
                onSelected = { selectedPrivateKey = eosOwnerPrivateKey }
            )
            VerticalSpacer(Spacing.TINY)
            PrivateKeyOptionCard(
                title = StringDesc.ResourceFormatted(
                    MR.strings.title_choose_permission_private_key_to_import,
                    "Active"
                ).localized(),
                description = MR.strings.label_warning_when_choose_active_permission_description.desc().localized(),
                privateKey = eosActivePrivateKey,
                selectedPrivateKey = selectedPrivateKey,
                onSelected = { selectedPrivateKey = eosActivePrivateKey }
            )

            Spacer(modifier = Modifier.weight(1f))

            ButtonNormal(
                text = MR.strings.all_continue.desc().localized(),
                fontSize = FontType.REGULAR,
                textColor = if (selectedPrivateKey != null) Colors.white else Colors.mistGray,
                onClick = {
                    selectedPrivateKey?.let {
                        screenModel.findEosAccountFromPrivateKey(it)
                    }
                },
                buttonModifier = Modifier.fillMaxWidth(),
                buttonMinSizeDefault = MangalaButtonSize.XMedium.height,
                backgroundColor = Colors.darkDarkGray,
                disabledBackgroundColor = Colors.lightLightGrayWhite,
                enabled = selectedPrivateKey != null,
                shape = RoundedCornerShape(CornerRadius.Tiny)
            )
        }
    }

    @Composable
    fun PrivateKeyOptionCard(
        title: String,
        description: String,
        privateKey: String,
        selectedPrivateKey: String?,
        onSelected: () -> Unit,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            MaxWidthRow(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .background(Colors.white, shape = RoundedCornerShape(CornerRadius.Small))
                    .border(
                        width = 1.dp,
                        color = Colors.paleGray,
                        shape = RoundedCornerShape(CornerRadius.Small)
                    )
                    .clickable { onSelected() }
                    .padding(
                        horizontal = Dimensions.Padding.default,
                        vertical = Dimensions.Padding.medium
                    )
            ) {
                TextDescription2(
                    text = title,
                    color = Colors.darkDarkGray,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.weight(1f))
                selectedPrivateKey?.let {
                    if (it == privateKey) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Dropdown",
                            tint = Colors.darkDarkGray,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            TextNormal(
                text = description,
                fontWeight = FontWeight.Normal,
                color = Colors.brightRed,
                fontSize = FontType.TINY
            )
        }
    }
}