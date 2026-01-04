package com.mangala.eticket.data.remote

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.PageResponse
import com.mangala.eticket.data.model.category.CategoryResponse
import com.mangala.eticket.di.ApiResponse
import com.mangala.eticket.di.safeApiCall
import com.mangala.eticket.network.CustomError


class CategoriesDataSource(private val api: CategoriesApi) {

    suspend fun getCategories(pageNumber: Int, pageSize: Int): ApiResponse<ETicketResponse<PageResponse<CategoryResponse>>, CustomError> {
        return safeApiCall {
            api.getCategories(pageNumber, pageSize)
        }
    }

    suspend fun getCategory(id: String): CategoryResponse {
        return api.getCategory(id)
    }
}