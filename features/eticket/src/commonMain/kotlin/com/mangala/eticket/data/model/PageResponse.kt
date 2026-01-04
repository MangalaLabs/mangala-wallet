package com.mangala.eticket.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PageResponse<T> (
    val totalPages: Int,
    val size: Int,
    val content: List<T>,
    val number: Int,
    val sort: SortResponse,
    val pageable: PageableResponse,
    val first: Boolean,
    val last: Boolean,
    val numberOfElements: Int,
    val empty: Boolean
)

@Serializable
class PageableResponse (
    val offset: Int,
    val sort: SortResponse,
    val pageNumber: Int,
    val pageSize: Int,
    val paged: Boolean,
    @SerialName("unpaged")
    val unPaged: Boolean
)

@Serializable
class SortResponse (
    val empty: Boolean,
    val sorted: Boolean,
    val unsorted: Boolean
)

