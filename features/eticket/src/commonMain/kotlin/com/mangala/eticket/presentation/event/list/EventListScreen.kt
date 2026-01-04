package com.mangala.eticket.presentation.event.list

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.cash.paging.LoadStateLoading
import app.cash.paging.PagingData
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.eticket.presentation.error.GeneralError
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.imageloader.RemoteImage
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import commangalaeticketdatabase.EventListEntity
import kotlinx.coroutines.flow.Flow
import org.koin.core.parameter.parametersOf
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.ui.MangalaTextButton
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import io.github.aakira.napier.Napier

class EventListScreen(private val categoryId: String?) : BaseScreen<EventListScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ETICKET_EVENTS_LIST
    override val screenClassName: String = EventListScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): EventListScreenModel = getScreenModel<EventListScreenModel>(
        parameters = { parametersOf(categoryId) }
    )

    @Composable
    override fun ScreenContent(screenModel: EventListScreenModel) {

        MaxSizeBox(Modifier.background(MaterialTheme.colors.background).windowInsetsPadding(WindowInsets.safeDrawing)) {
            when (val uiState = screenModel.uiState.collectAsStateMultiplatform().value) {
                is EventListScreenUIState.Loading -> {
                    MaxSizeBox(
                        contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.Red)
                    }
                }

                is EventListScreenUIState.Success -> {
                    EventList(uiState.data)
                }

                is EventListScreenUIState.Error -> {
                    GeneralError()
                }
            }
        }
    }

    @Composable
    fun EventList(events: Flow<PagingData<EventListEntity>>) {
        val localNavigator = LocalNavigator.currentOrThrow
        var items: LazyPagingItems<EventListEntity> = events.collectAsLazyPagingItems()

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
                    Text("Loading") // temp message, no need for localization
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


                val previousItem = if (index > 0) items[index - 1] else null
                val nextItem = if (index < items.itemCount - 1) items[index + 1] else null

                val currentItem = items[index]
                val roundedCornersShape = when {
                    previousItem == null && nextItem == null -> RoundedCornerShape(CornerRadius.Small)

                    previousItem == null -> RoundedCornerShape(
                        topStart = CornerRadius.Small,
                        topEnd = CornerRadius.Small
                    )

                    nextItem == null -> RoundedCornerShape(
                        bottomStart = CornerRadius.Small,
                        bottomEnd = CornerRadius.Small
                    )

                    else -> RoundedCornerShape(0.dp)
                }

                currentItem?.let {
                    EventItem(it)
                }
            }

            if (items.loadState.append == LoadStateLoading) {
                item {
                    Text("Loading") // temp message, no need for localization
                }
            }
        }
    }

    @Composable
    fun EventItem(
        event: EventListEntity,
        modifier: Modifier = Modifier
    ) {
        Card(modifier.padding(8.dp)) {
            Row(modifier.fillMaxWidth().padding(16.dp)) {
                event.thumb?.let { thumbUrl ->
                    RemoteImage(
                        modifier = Modifier.padding(end = 8.dp).size(200.dp, 200.dp),
                        url = thumbUrl,
                        contentScale = ContentScale.Crop
                    )
                }
                Column {
                    event.title?.let { Text(text = it, style = MaterialTheme.typography.h6) }
                    event.venue?.let { Text(text = "Venue: $it", style = MaterialTheme.typography.body2) }
                    event.startTime?.let { Text(text = "Start: $it", style = MaterialTheme.typography.body2) }
                    event.endTime?.let { Text(text = "End: $it", style = MaterialTheme.typography.body2) }
                }
            }
        }
    }
}