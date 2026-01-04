package com.mangala.wallet.features.chains.antelope.presentation.importaccount.step2

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AntelopeAccountByAuthorizer
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.InfoCircle
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextSubTitle
import com.mangala.wallet.ui.component.GradientBackground
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.modifier.roundedCornerItemShape
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

@Parcelize
class Step2ImportAccountSelectAccountScreen(
    private val privateKey: String,
    private val accountsByAuthorizers: ArrayList<AntelopeAccountByAuthorizer> // must use ArrayList here, because List<> is not serializable
) : BaseScreen<Step2ImportAccountSelectAccountScreenModel>(), Parcelable {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_IMPORT_ACCOUNT_SELECT_ACCOUNT
    override val screenClassName: String = Step2ImportAccountSelectAccountScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): Step2ImportAccountSelectAccountScreenModel {
        return getScreenModel(parameters = { parametersOf(privateKey, accountsByAuthorizers) })
    }

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun ScreenContent(screenModel: Step2ImportAccountSelectAccountScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value
        val stateReturn by screenModel.stateReturn.collectAsStateMultiplatform()

        LaunchedEffect(uiState) {
            when (uiState) {
                is Step2ImportAccountSelectAccountUiState.Imported -> {
                    screenModel.createAccount(uiState.accountName)
                }

                is Step2ImportAccountSelectAccountUiState.AccountCreated -> {
                    screenModel.returnCreatedAccount(uiState.accountName)
                    if (stateReturn) {
                        if (!uiState.isPinSetup) {
                            val createNewPin =
                                ScreenRegistry.get(
                                    SharedScreen.SetupPinScreen(
                                        blockchainUid = uiState.blockchainType.uid,
                                        antelopeAccountName = uiState.accountName,
                                        pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_PIN.name
                                    )
                                )
                            navigator.push(createNewPin)
                        } else {
                            val homeScreen = ScreenRegistry.get(SharedScreen.HomeScreen())
                            navigator.replaceAll(homeScreen)
                        }
                    }
                }

                else -> {}
            }
        }

        ImportScreen(
            uiState = uiState,
            onClickAccount = {
                screenModel.onSelectAccount(it)
            },
            onBackPressed = {
                navigator.popUntilRoot()
            }
        )
    }

    @Composable
    fun ImportScreen(
        uiState: Step2ImportAccountSelectAccountUiState,
        onClickAccount: (String) -> Unit,
        onBackPressed: () -> Unit
    ) {
        GradientBackground {
            MaxSizeColumn(verticalArrangement = Arrangement.SpaceBetween) {
                MangalaWalletTopBar(
                    modifier = Modifier.background(Color.Transparent),
                    text = "",
                    onBackClicked = onBackPressed
                )
                MaxWidthColumn(
                    modifier = Modifier.weight(1f)
                        .padding(horizontal = Dimensions.Padding.default)
                ) {
                    MaxSizeColumn {
                        Spacer(Modifier.height(Spacing.LARGE))
                        (uiState as? Step2ImportAccountSelectAccountUiState.NotImported)?.let {
                            TextSubTitle(
                                text = MR.strings.title_select_account_import_account.desc()
                                    .localized(),
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(14.dp))
                            TextNormal(
                                text = uiState.blockchainType?.name.orEmpty(),
                                color = Colors.darkDarkGray
                            )
                            Spacer(Modifier.height(Spacing.SMALL))
                            AccountList(it.accounts, onClick = onClickAccount)
                        }
                    }
                }

            }
        }
    }

    @Composable
    fun AccountList(
        accounts: List<String>,
        onClick: (String) -> Unit
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            itemsIndexed(accounts) { index, account ->
                AccountItem(
                    shape = roundedCornerItemShape(
                        accounts, index
                    ),
                    item = account,
                    onClick = {
                        onClick(account)
                    }
                )
            }
        }
    }

    @Composable
    private fun AccountItem(
        item: String,
        shape: Shape,
        onClick: () -> Unit
    ) {
        MaxWidthRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(Color.White)
                .clickable(onClick = onClick)
                .padding(
                    horizontal = Dimensions.Padding.default,
                    vertical = Dimensions.Padding.medium
                ),
        ) {
            TextDescription2(
                text = item,
                color = MaterialTheme.colors.onPrimary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(Spacing.TINY))
            Icon(
                MangalaWalletPack.InfoCircle,
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}