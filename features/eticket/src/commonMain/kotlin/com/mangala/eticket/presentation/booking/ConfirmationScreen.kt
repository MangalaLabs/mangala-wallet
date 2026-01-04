package com.mangala.eticket.presentation.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.parameter.parametersOf

class ConfirmationScreen(
    private val ticketSelections: List<TicketSelection>,
    private val totalAmount: Double,
    private val isSendToAnotherWallet: Boolean,
    private val walletAnotherAddress: String,
) : BaseScreen<ConfirmationScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ETICKET_CONFIRMATION
    override val screenClassName: String = ConfirmationScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): ConfirmationScreenModel =
        getScreenModel<ConfirmationScreenModel>(
            parameters = {
                parametersOf(
                    ticketSelections,
                    totalAmount,
                    isSendToAnotherWallet,
                    walletAnotherAddress
                )
            }
        )


    @Composable
    override fun ScreenContent(screenModel: ConfirmationScreenModel) {
        val uiState = screenModel.uiState.collectAsState().value
        val navigator = LocalNavigator.currentOrThrow

        when (uiState) {
            is ConfirmationUiState.Loading -> {}
            is ConfirmationUiState.Success -> SuccessView(
                uiState.data, navigator, screenModel::onPaymentCompleted
            )

            is ConfirmationUiState.Error -> {}
            is BookingUiState.Error -> TODO()
        }

    }

    @Composable
    fun SuccessView(
        data: ConfirmationUiModel,
        navigator: Navigator,
        onPaymentCompleted: () -> Unit,
    ) {
        Scaffold(
            topBar = { ConfirmationTopBar(navigator) },
            bottomBar = { BottomBar(data.totalAmount, onPaymentCompleted) },
            modifier = Modifier.background(MaterialTheme.colors.background).windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            ConfirmationContent(data.selectedTickets, data.walletAnotherAddress)
        }
    }

    @Composable
    fun ConfirmationTopBar(navigator: Navigator) {
        TopAppBar(
            title = {
                Text(text = "Payment")
            },
            navigationIcon = {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
    }


    @Composable
    fun ConfirmationContent(
        selectedTickets: List<TicketSelection>,
        walletAddress: String,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.background)
                .padding(16.dp),
        ) {
            TicketSummary(selectedTickets)

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            WalletAddressReceive(walletAddress)

            PaymentMethodOption()

        }

    }

    @Composable
    fun BottomBar(total: Double, onPayClick: () -> Unit) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .navigationBarsPadding()
        ) {
            Button(
                onClick = onPayClick,
                enabled = total > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (total > 0) MaterialTheme.colors.primary else Color.Gray
                )
            ) {
                Text(
                    text = "Pay - $total",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }

    @Composable
    fun TicketSummary(tickets: List<TicketSelection>) {
        LazyColumn {
            items(tickets) { ticket ->
                TicketConfirmationCard(ticket)
            }
        }
    }

    @Composable
    fun TicketConfirmationCard(ticket: TicketSelection) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${ticket.quantity} x ${ticket.name}")
                Text(text = "${ticket.price * ticket.quantity}")
            }
        }
    }

    @Composable
    fun WalletAddressReceive(
        walletAddress: String,
    ) {
        Column {
            Text(
                text = "Wallet Address to receive NFT:",
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = walletAddress,
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }


    @Composable
    fun PaymentMethodOption() {
    }
}
