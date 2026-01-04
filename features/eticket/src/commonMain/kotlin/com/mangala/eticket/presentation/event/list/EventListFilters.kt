package com.mangala.eticket.presentation.event.list

data class EventListFilters (
    val categoriesIdFilter: MutableList<String>?,
    val titleFilter: String?
)