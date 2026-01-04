package com.mangala.eticket

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.imageloader.RemoteImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.ui.graphics.Color

class EticketUI {
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    onClick: (msg: String) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface, shape = RoundedCornerShape(8.dp))
            .clickable {
                onClick("")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "",
            modifier = Modifier.padding(start = 16.dp)
        )
        TextNormal("Search or type URL", modifier = Modifier
            .weight(1f)
            .padding(16.dp))
    }
}

@Composable
fun CategoryList(categories: List<Category>) {
    LazyColumn {
        items(10) { category ->
            CategoryItem(categories[category])
        }
    }
}

@Composable
fun CategoryItem(category: Category) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        RemoteImage(
            modifier = Modifier.size(Dimensions.IconButtonSize),
            url = category.imageUrl,
        )
        Text(text = category.name)
    }
}

@Composable
fun EventItem(event: Event) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
    ) {
        RemoteImage(
            modifier = Modifier.size(Dimensions.IconButtonSize),
            url = event.imageUrl,
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.BottomStart)
        ) {
            Text("Naega-myeon, Ganghwa-gun, South Korea")
            Text("2,712 kilometers away")
            Text("5 nights • Nov 5 – 10")
            Text("$740 total before taxes")
        }

        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Favorite icon",
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp)
        )
    }
}

@Composable
fun EventList(events: List<Event>) {
    BoxWithConstraints {
        val columns = if (maxWidth < 720.dp) 1 else 3

        LazyColumn {
            items(events.chunked(columns)) { rowRentals ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (rental in events) {
                        EventItem(event = rental)
                    }
                }
            }
        }
    }
}