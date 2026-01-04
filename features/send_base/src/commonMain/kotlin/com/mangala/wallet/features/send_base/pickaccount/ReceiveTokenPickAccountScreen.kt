package com.mangala.wallet.features.send_base.pickaccount

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class ReceiveTokenPickAccountScreen(
    private val onClickAccountInfo: (accountId: String) -> Unit,
    private val networkType: NetworkType
) : BaseScreen<ReceiveTokenPickAccountScreenModel>() {

    override val screenName: String  = MangalaAnalytics.Screens.RECEIVE_TOKEN_PICK_ACCOUNT
    override val screenClassName: String = ReceiveTokenPickAccountScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ReceiveTokenPickAccountScreenModel = getScreenModel(
        parameters = {
            parametersOf(
                networkType
            )
        }
    )

    @Composable
    override fun ScreenContent(screenModel: ReceiveTokenPickAccountScreenModel) {
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        ReceiveTokenPickAccountScreen(
            uiState = uiState,
            onClickAccountInfo = {
                onClickAccountInfo(it)
            })
    }

    @Composable
    private fun ReceiveTokenPickAccountScreen(
        uiState: ReceiveTokenPickAccountScreenUiState,
        onClickAccountInfo: (accountId: String) -> Unit,
    ) {
        LazyColumn(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .fillMaxWidth()
                .heightIn(min = Dp.Unspecified, max = 650.dp)
        ) {
            when (uiState) {
                is ReceiveTokenPickAccountScreenUiState.Success -> {
                    items(uiState.uiModel.items.size) { index ->
                        val account = uiState.uiModel.items[index]
                        val onClick = { onClickAccountInfo(account.key) }
                        when (account) {
                            is AccountUiModelPickAccount.Evm -> AccountItem(
                                accountName = account.account.account.account.name,
                                address = Address(account.account.account.bip44Address).eip55,
                                formattedValue = account.account.formattedValue,
                                onClickAccountInfo = onClick
                            )

                            is AccountUiModelPickAccount.Antelope -> AccountItem(
                                accountName = account.account.account.accountName,
                                address = account.account.account.accountName,
                                formattedValue = account.account.fiatValueFormatted,
                                onClickAccountInfo = onClick
                            )
                        }
                    }
                }

                ReceiveTokenPickAccountScreenUiState.Loading -> {
                    item {
                        Text(MR.strings.all_loading.desc().localized()) // test text, to be replaced with real UI
                    }
                }

                ReceiveTokenPickAccountScreenUiState.Error -> {
                    item {
                        Text(MR.strings.message_receive_token_pick_account_screen_error_loading_accounts_list.desc().localized()) // test text, to be replaced with real UI
                    }
                }
            }
        }
    }

    @Composable
    private fun AccountItem(
        accountName: String,
        address: String,
        formattedValue: String,
        onClickAccountInfo: () -> Unit
    ) {
        // State to control the elevation
        var clicked by remember { mutableStateOf(false) }
        val elevation by animateDpAsState(targetValue = if (clicked) 8.dp else 2.dp)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = Spacing.SMALL, end = Spacing.SMALL)
        ) {
            Spacer(modifier = Modifier.height(Spacing.TINY))
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = {
                            clicked = !clicked
                            onClickAccountInfo()
                        }
                    )
                    .shadow(elevation = elevation, shape = RoundedCornerShape(8.dp))
                    .background(color = MaterialTheme.colors.primary)
                    .border(
                        width = 1.dp,
                        color = Color.LightGray,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Spacing.SMALL),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = accountName,
                                color = MaterialTheme.colors.onSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = FontType.SMALL_18,
                                fontFamily = fontFamilyResource(MR.fonts.sfpro)
                            )
                            TextTiny(address)
                        }
                    }
                    Divider(
                        color = Color.LightGray,
                        modifier = Modifier
                            .height(1.dp)
                            .padding(start = Spacing.SMALL, end = Spacing.SMALL)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(start = Spacing.SMALL, end = Spacing.SMALL),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextDescription2(formattedValue)
                    }
                }
            }
        }
    }

}