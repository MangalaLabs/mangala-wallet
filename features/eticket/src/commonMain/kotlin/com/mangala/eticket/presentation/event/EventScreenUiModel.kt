package com.mangala.eticket.presentation.event

import androidx.compose.ui.graphics.Color
import com.mangala.eticket.data.model.event.EventDetailResponse
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.utils.convertUtcMillisToLocal
import kotlinx.datetime.LocalDateTime

sealed class EventScreenUiState {
    object Loading : EventScreenUiState()
    data class Success(val data: EventScreenUiModel) : EventScreenUiState()
    data class Error(val message: WrappedStringResource) : EventScreenUiState()
}

data class EventScreenUiModel(
    val eventDetail: EventDetailResponse?,
    val currencySymbol: String,
) {
    private val startTime: LocalDateTime? = eventDetail?.startTime?.let { convertTimeToLocal(it) }
    private val endTime: LocalDateTime? = eventDetail?.endTime?.let { convertTimeToLocal(it) }
    val timeSales: String = "$startTime - $endTime"
    val firstTicketPrice: String =
        "$currencySymbol ${eventDetail?.ticketTypes?.firstOrNull()?.price ?: 0.0}"
    val evenStatus: String = when (eventDetail?.status) {
        0 -> "CREATED"
        1 -> "APPROVED"
        2 -> "PUBLISHED"
        -1 -> "REJECTED"
        3 -> "CANCELLED"
        else -> "UNKNOWN"
    }
    val eventStatusColor: Color = getStatusColor(eventDetail?.status ?: 0)

    val ticketTypes = mapToTicketTypeUiModels()

    data class TicketTypeUiModel(
        val id: String,
        val name: String,
        val amount: Int,
        val price: Double,
        val priceWithCurrency: String,
    )
}

private fun convertTimeToLocal(timestamp: Long): LocalDateTime {
    return convertUtcMillisToLocal(timestamp.times(1000))
}

private fun getStatusColor(status: Int): Color {
    return when (status) {
        0 -> Color.Gray
        1 -> Color.Green
        2 -> Color.Blue
        -1 -> Color.Red
        3 -> Color.Yellow
        else -> Color.Black
    }
}

fun EventScreenUiModel.mapToTicketTypeUiModels(): List<EventScreenUiModel.TicketTypeUiModel> {
    return eventDetail?.ticketTypes?.map {
        EventScreenUiModel.TicketTypeUiModel(
            it.id ?: "",
            it.name ?: "",
            it.amount ?: 0,
            it.price ?: 0.0,
            "$currencySymbol ${it.price ?: 0.0}"
        )
    } ?: emptyList()
}
