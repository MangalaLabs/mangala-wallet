package com.mangala.eticket.presentation.favourite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.cash.paging.LoadStateLoading
import app.cash.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.eticket.data.model.favourite.UserEventFavouriteResponse
import com.mangala.wallet.ui.MangalaTextButton
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.imageloader.RemoteImage
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow

class UserEventFavouriteScreen: BaseScreen<UserEventFavouriteScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ETICKET_USER_EVENT_FAVORITE
    override val screenClassName: String = UserEventFavouriteScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): UserEventFavouriteScreenModel = getScreenModel<UserEventFavouriteScreenModel>()

    @Composable
    override fun ScreenContent(screenModel: UserEventFavouriteScreenModel) {
        MaxSizeBox(Modifier.background(MaterialTheme.colors.background).windowInsetsPadding(WindowInsets.safeDrawing)) {
            EventList(screenModel.listData)
        }
    }


    @Composable
    fun EventList(events: Flow<PagingData<UserEventFavouriteResponse>>) {
        val localNavigator = LocalNavigator.currentOrThrow
        val items: LazyPagingItems<UserEventFavouriteResponse> = events.collectAsLazyPagingItems()

        MangalaTextButton(
            text = "Back",
            onClick = {
                localNavigator.pop()
            },
            color = Color.Black
        )

        LazyColumn {
            if (items.loadState.refresh == LoadStateLoading) {
                item {
                    Text("Loading")
                }
                Napier.d(
                    message = "items.loadState.refresh in lazycolumn  = ${items.loadState.refresh}",
                    tag = "refresh"
                )
            }

            items(
                count = items.itemCount,
                key = { it }
            ) { index ->
                val currentItem = items[index]
                currentItem?.let {
                    EventItem(it)
                }
            }

            if (items.loadState.append == LoadStateLoading) {
                item {
                    Text("Loading")
                }
            }
        }
    }

    @Composable
    fun EventItem(
        event: UserEventFavouriteResponse,
        modifier: Modifier = Modifier
    ) {
        Card(modifier.padding(8.dp)) {
            Row(modifier.fillMaxWidth().padding(16.dp)) {
                event.thumbUrl?.let { thumbUrl ->
                    RemoteImage(
                        modifier = Modifier.padding(end = 8.dp).size(200.dp, 200.dp),
                        url = thumbUrl,
                        contentScale = ContentScale.Crop
                    )
                }
                Column {
                    Text(text = event.title, style = MaterialTheme.typography.h6)
                    Text(text = "Venue: ${event.venue}", style = MaterialTheme.typography.body2)
                    Text(text = "Start: ${event.startTime}", style = MaterialTheme.typography.body2)
                    Text(text = "End: ${event.endTime}", style = MaterialTheme.typography.body2)
                    Text(text = "End: ${event.status}", style = MaterialTheme.typography.body2)
                }
            }
        }
    }
}