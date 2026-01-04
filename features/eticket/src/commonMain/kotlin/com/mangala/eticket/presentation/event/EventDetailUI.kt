package com.mangala.eticket.presentation.event

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.eticket.data.model.event.EventDetailResponse
import com.mangala.eticket.data.model.event.EventMediaResponse
import com.mangala.eticket.presentation.booking.BookingScreen
import com.mangala.wallet.ui.imageloader.RemoteImage

@Composable
fun EventDetail(
    event: EventDetailResponse,
    firstTicketPrice: String,
    timeSales: String,
    statusText: String?,
    statusColor: Color?,
    ticketTypes: List<EventScreenUiModel.TicketTypeUiModel>,
) {
    var selectedTicketPrice by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = event) {
        selectedTicketPrice = firstTicketPrice
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.h6,
                        color = Color.Black
                    )
                },
                backgroundColor = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            )
        },
        bottomBar = {
            selectedTicketPrice?.let {
                BookNowButton("$it ", ticketTypes) {
                }
            }
        },
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) { innerPadding ->
        EventDetailContent(
            detail = event,
            timeSales,
            statusText,
            statusColor,
            ticketTypes,
            innerPadding
        ) { price ->
            selectedTicketPrice = price
        }
    }
}

@Composable
fun EventDetailContent(
    detail: EventDetailResponse,
    timeSales: String,
    eventStatusText: String?,
    statusColor: Color?,
    ticketTypes: List<EventScreenUiModel.TicketTypeUiModel>,
    innerPadding: PaddingValues,
    onTicketSelected: (String) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        MediaCarousel(detail.medias)

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = detail.title,
            style = MaterialTheme.typography.h5,
        )
        Spacer(modifier = Modifier.height(8.dp))
        BlockchainEventDetail("Ethereum", "0x123...7890")

        Spacer(modifier = Modifier.height(8.dp))
        EventTime(timeSales)

        Spacer(modifier = Modifier.height(8.dp))
        Venue(detail.venue)
        EventStatus(eventStatusText, statusColor)

        Spacer(modifier = Modifier.height(8.dp))

        DescriptionEvent(detail.description)

        Spacer(modifier = Modifier.height(8.dp))
        PolicyEvent(detail.refundPolicy)

        Spacer(modifier = Modifier.height(8.dp))
        TicketTypeSection(ticketTypes, onTicketSelected)

        Spacer(modifier = Modifier.height(24.dp))

    }
}

@Composable
fun BlockchainEventDetail(chain: String, contractAddress: String) {
    Text(text = "Chain: $chain", style = MaterialTheme.typography.subtitle1)
    Spacer(modifier = Modifier.height(8.dp))
    Text(text = "Contract Address: $contractAddress", style = MaterialTheme.typography.subtitle1)
}

@Composable
fun EventTime(timeSale: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.DateRange,
            contentDescription = "Clock Icon"
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = timeSale,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun Venue(venue: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Filled.LocationOn,
            contentDescription = "Location icon"
        )
        Spacer(modifier = Modifier.size(4.dp))
        Text(
            text = venue,
            style = MaterialTheme.typography.body1
        )
    }
}

@Composable
fun BookNowButton(
    ticketPrice: String,
    ticketTypes: List<EventScreenUiModel.TicketTypeUiModel>,
    onClick: () -> Unit,
) {
    val navigator = LocalNavigator.currentOrThrow

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row {
            Text(
                text = "",
                style = MaterialTheme.typography.subtitle1
            )
            Text(
                text = ticketPrice,
                style = MaterialTheme.typography.subtitle1
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Button(
            onClick = {
                onClick()
                navigator.push(
                    BookingScreen(
                        ticketTypes = ticketTypes
                    )
                )
            },
            modifier = Modifier
                .wrapContentSize(),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
        ) {
            Text("Book now", color = Color.White, style = MaterialTheme.typography.subtitle1)
        }
    }
}

@Composable
fun TicketTypeSection(
    ticketTypes: List<EventScreenUiModel.TicketTypeUiModel>,
    onTicketTypeSelected: (String) -> Unit,
) {
    Text(
        text = "Ticket types",
        style = MaterialTheme.typography.h6
    )

    Spacer(modifier = Modifier.height(8.dp))
    Column() {
        ticketTypes.forEach { ticketType ->
            TicketTypeCard(ticketType, onTicketTypeSelected)
        }
    }
}

@Composable
fun TicketTypeCard(
    ticketType: EventScreenUiModel.TicketTypeUiModel,
    onTicketTypeSelected: (String) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onTicketTypeSelected(ticketType.priceWithCurrency) },
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Row(
            modifier = Modifier
                .clickable { onTicketTypeSelected(ticketType.priceWithCurrency) }
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = ticketType.name,
                style = MaterialTheme.typography.subtitle1
            )

            Text(
                text = ticketType.priceWithCurrency,
                style = MaterialTheme.typography.subtitle1
            )

        }
    }
}

@Composable
fun MediaCarousel(medias: List<EventMediaResponse>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(medias.size) { index ->
            medias[index].url?.let { url ->
                if (url.isNotEmpty()) {
                    RemoteImage(
                        modifier = Modifier.padding(end = 8.dp).size(200.dp, 200.dp),
                        url = url,
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }

}

@Composable
fun DescriptionEvent(description: String) {
    Text(
        text = "About",
        style = MaterialTheme.typography.h6
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = description,
        style = MaterialTheme.typography.body1
    )
}

@Composable
fun PolicyEvent(policy: String) {
    Text(
        text = "Policy",
        style = MaterialTheme.typography.h6
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = policy,
        style = MaterialTheme.typography.body1
    )
}

@Composable
fun EventStatus(eventStatusText: String?, statusColor: Color?) {
    Chip(
        label = { eventStatusText?.let { Text(it) } },
        color = statusColor,
        modifier = Modifier.padding(4.dp)
    )
}

@Composable
fun Chip(label: @Composable () -> Unit, color: Color?, modifier: Modifier = Modifier) {
    if (color != null) {
        Surface(
            modifier = modifier.clip(RoundedCornerShape(50)),
            color = color
        ) {
            Row(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                label()
            }
        }
    }
}