package com.mangala.eticket.presentation.event


import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.parameter.parametersOf

class EvenDetailScreen : BaseScreen<EventDetailScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ETICKET_EVENT_DETAILS
    override val screenClassName: String = EvenDetailScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): EventDetailScreenModel =
        getScreenModel<EventDetailScreenModel>(
            parameters = {
                parametersOf("a46afb75-bfff-4ba3-b036-c137021d5300")
            }
        )

    @Composable
    override fun ScreenContent(screenModel: EventDetailScreenModel) {
        when (val uiState = screenModel.uiState.collectAsStateMultiplatform().value) {
            is EventScreenUiState.Loading -> {
                // Display loading UI
            }

            is EventScreenUiState.Success -> {
                uiState.data.eventDetail?.let {
                    EventDetail(
                        it,
                        uiState.data.firstTicketPrice,
                        uiState.data.timeSales,
                        uiState.data.evenStatus,
                        uiState.data.eventStatusColor,
                        uiState.data.ticketTypes
                    )
                }
            }

            is EventScreenUiState.Error -> {
                // Display error UI with uiState.errorMessage
            }
        }
    }
}

