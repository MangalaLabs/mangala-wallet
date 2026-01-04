package com.mangala.eticket.data.remote

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.PageResponse
import com.mangala.eticket.data.model.category.CategoryResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface CategoriesApi {

    @GET("v1/categories")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun getCategories(
        @Query("page_number") pageNumber: Int,
        @Query("page_size") pageSize: Int,
    ): ETicketResponse<PageResponse<CategoryResponse>>

    @GET("v1/categories/{id}")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun getCategory(
        @Path("id") id: String
    ): CategoryResponse
}