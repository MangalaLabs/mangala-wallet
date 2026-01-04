package com.mangala.eticket.domain.repository

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.PageResponse
import com.mangala.eticket.data.model.category.CategoryResponse
import com.mangala.eticket.di.ApiResponse
import com.mangala.eticket.network.CustomError

interface CategoriesRepository {

    suspend fun getCategories(pageNumber: Int, pageSize: Int): ApiResponse<ETicketResponse<PageResponse<CategoryResponse>>, CustomError>
    suspend fun getCategory(id: String): CategoryResponse

}