package com.mangala.wallet.features.wallet.presentation.syncaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.model.qr.SyncAccountRequest
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.component.WalletMainScreenTopBar
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import org.koin.core.parameter.parametersOf

class SyncAccountScreen(
    private val syncAccountRequest: SyncAccountRequest
): BaseScreen<SyncAccountScreenModel>() {

    override val isBottomBarVisible: Boolean
        get() = false

    override val statusBarInsetColor: Color
        @Composable
        get() = MaterialTheme.colors.background

    @Composable
    override fun createScreenModel(): SyncAccountScreenModel = getScreenModel(
        parameters = { parametersOf(syncAccountRequest) }
    )

    @Composable
    override fun ScreenContent(screenModel: SyncAccountScreenModel) {
        val uiState = screenModel.uiState.collectAsStateMultiplatform()
        val navigator = LocalNavigator.currentOrThrow

        SyncAccountScreen(
            uiState.value,
            onClickSyncAccount = screenModel::onClickSyncAccount,
            navigator
        )
    }

    @Composable
    fun SyncAccountScreen(
        uiState: SyncAccountUiState,
        onClickSyncAccount: () -> Unit,
        navigator: Navigator
    ) {
        WalletMainScreenTopBar(
            onClickMenuIcon = {
                val menuScreen = ScreenRegistry.get(SharedScreen.MenuScreen)
                navigator.push(menuScreen)
            },
        )
        Box {
            MaxWidthColumn(
                Modifier
                    .fillMaxHeight() // Occupy the entire height of the available space
                    .verticalScroll(rememberScrollState())
                    .padding()
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = Spacing.SMALL, vertical = Spacing.XSMALL)
                        .weight(1f) // Occupy remaining vertical space
                ) {
                    TextNormal(
                        text = "About wallet information",
                        fontWeight = FontWeight.Medium,
                    )

                    VerticalSpacer(16.dp)
                    ItemSyncWallet(
                        stringTitle = "Account name",
                        stringInformation = syncAccountRequest.accountName
                    )
                    VerticalSpacer(12.dp)
                    ItemSyncWallet(
                        stringTitle = "Account address",
                        stringInformation = syncAccountRequest.bip44Address
                    )
                }
            }
            MaxWidthColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.SMALL, vertical = Spacing.SMALL)
                    .background(Color.White)
                    .align(Alignment.BottomCenter), // Align to bottom center
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ButtonNormal(
                    text = "Sync Account",
                    modifier = Modifier
                        .fillMaxWidth(),
                    buttonModifier = Modifier
                        .height(44.dp)
                        .fillMaxWidth(),
                    fontSize = FontType.REGULAR,
                    backgroundColor = Color(0xFFD7D7D7),
                ) {
                    onClickSyncAccount()
                    navigator.pop()
                }
            }
        }
    }


    @Composable
    private fun ItemSyncWallet(
        stringTitle: String,
        stringInformation: String
    ) {
        Column(
            Modifier
                .border(
                    width = 1.dp,
                    color = Color(0xFFD7D7D7),
                    shape = RoundedCornerShape(size = 16.dp)
                )
                .fillMaxWidth()
                .height(72.dp)
                .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 16.dp))
                .padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 12.dp)

        ) {
            Column {
                TextDescription2(
                    text = stringTitle,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF383838),
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextDescription2(
                    text = stringInformation,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF767676),
                )
            }
        }
    }
}