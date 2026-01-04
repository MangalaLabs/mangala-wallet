package com.mangala.eticket.presentation.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.eticket.presentation.event.EventScreenUiModel
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Minus
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.parameter.parametersOf


class BookingScreen(
    private val ticketTypes: List<EventScreenUiModel.TicketTypeUiModel>,
) : BaseScreen<BookingScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ETICKET_BOOKING
    override val screenClassName: String = BookingScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): BookingScreenModel = getScreenModel<BookingScreenModel>(
        parameters = { parametersOf(ticketTypes) }
    )


    @Composable
    override fun ScreenContent(screenModel: BookingScreenModel) {
        val uiState = screenModel.uiState.collectAsState().value
        val navigator = LocalNavigator.currentOrThrow

        when (uiState) {
            is BookingUiState.Loading -> {
            }

            is BookingUiState.Success -> {
                BookingScreenContent(
                    uiState.data,
                    screenModel::updateTicketQuantity,
                    navigator
                )
            }
        }
    }

    @Composable
    fun BookingScreenContent(
        data: BookingUiModel,
        updateTicketQuantity: (EventScreenUiModel.TicketTypeUiModel, Int) -> Unit,
        navigator: Navigator,
    ) {
        val sendToAnotherWallet = remember { mutableStateOf(false) }
        val walletAddress = remember { mutableStateOf("") }

        Scaffold(
            topBar = {
                BookingTopBar(navigator)
            },
            bottomBar = {
                BottomBar(
                    navigator,
                    data.ticketSelections,
                    data.isButtonEnabled,
                    data.totalMoney,
                    sendToAnotherWallet.value,
                    walletAddress.value,
                    data.totalMoneyStr
                )
            },
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background)
                    .padding(16.dp),
            ) {
                LazyColumn {
                    items(ticketTypes) { ticketType ->
                        TicketTypeCardWithQuantity(
                            ticketType,
                            onQuantityChange = { newQuantity ->
                                updateTicketQuantity(ticketType, newQuantity)
                            }
                        )
                    }
                }

                WalletOptionRow(
                    sendToAnotherWallet = sendToAnotherWallet.value,
                    onWalletAddressChange = { walletAddress.value = it },
                    onChange = { sendToAnotherWallet.value = it }
                )
            }
        }
    }

    @Composable
    fun BookingTopBar(navigator: Navigator) {
        TopAppBar(
            title = { Text("Booking") },
            navigationIcon = {
                IconButton(onClick = { navigator.pop() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )
    }

    @Composable
    fun BottomBar(
        navigator: Navigator,
        ticketSelections: List<TicketSelection>,
        isButtonEnabled: Boolean,
        total: Double,
        sendToAnotherWallet: Boolean,
        walletAddress: String,
        totalMoneyStr: String,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    if (isButtonEnabled) {
                        navigator.push(
                            ConfirmationScreen(
                                ticketSelections,
                                total,
                                sendToAnotherWallet,
                                walletAddress
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (total > 0) Color.Green else Color.Gray
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
            ) {
                Text(text = totalMoneyStr)
            }
        }
    }

    @Composable
    fun WalletOptionRow(
        sendToAnotherWallet: Boolean,
        onWalletAddressChange: (String) -> Unit,
        onChange: (Boolean) -> Unit,
    ) {

        Column {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Send to another wallet?",
                    style = MaterialTheme.typography.h6
                )

                Switch(
                    checked = sendToAnotherWallet,
                    onCheckedChange = onChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        uncheckedThumbColor = Color.Black,
                        checkedTrackColor = Color.Black,
                        uncheckedTrackColor = Color.White
                    )
                )
            }

            AnimatedVisibility(visible = sendToAnotherWallet) {
                WalletDetailsInput(onValueChange = onWalletAddressChange)
            }

        }
    }

    @Composable
    fun WalletDetailsInput(
        onValueChange: (String) -> Unit,
    ) {
        var text by remember { mutableStateOf("") }
        Column {
            CustomTextField(
                value = text,
                label = "Enter wallet address",
                onValueChange = {
                    text = it
                    onValueChange(it)
                }
            )
        }
    }


    @Composable
    fun TicketTypeCardWithQuantity(
        ticketType: EventScreenUiModel.TicketTypeUiModel,
        onQuantityChange: (Int) -> Unit,
    ) {
        var quantity by remember { mutableStateOf(0) }
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
                Column {
                    Text(
                        text = ticketType.name,
                        style = MaterialTheme.typography.subtitle1
                    )

                    Text(
                        text = ticketType.priceWithCurrency,
                        style = MaterialTheme.typography.subtitle1
                    )
                }

                QuantitySelector(quantity) { newQuantity ->
                    quantity = newQuantity
                }
            }
        }

        LaunchedEffect(quantity) {
            onQuantityChange(quantity)
        }
    }

    @Composable
    fun QuantitySelector(quantity: Int, onQuantityChange: (Int) -> Unit) {
        Row {
            IconButton(onClick = { if (quantity > 0) onQuantityChange(quantity - 1) }) {
                Icon(
                    MangalaWalletPack.Minus,
                    modifier = Modifier.width(16.dp),
                    contentDescription = "Decrease quantity"
                )
            }

            Text(text = quantity.toString(), modifier = Modifier.padding(16.dp))

            IconButton(onClick = { onQuantityChange(quantity + 1) }) {
                Icon(
                    Icons.Filled.Add,
                    modifier = Modifier.width(16.dp),
                    contentDescription = "Increase quantity"
                )
            }
        }
    }

    @Composable
    fun CustomTextField(
        value: String,
        label: String,
        keyboardType: KeyboardType = KeyboardType.Text,
        onValueChange: (String) -> Unit,
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    label,
                    style = MaterialTheme.typography.subtitle1,
                    color = Color.Gray
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray
            )
        )
    }
}

