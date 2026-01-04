package com.mangala.eticket.domain.repository

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.PageResponse
import com.mangala.eticket.data.model.category.CategoryResponse
import com.mangala.eticket.data.remote.CategoriesDataSource
import com.mangala.eticket.di.ApiResponse
import com.mangala.eticket.network.CustomError

class CategoriesRepositoryImpl(private val dataSource: CategoriesDataSource
): CategoriesRepository {
    override suspend fun getCategories(pageNumber: Int, pageSize: Int): ApiResponse<ETicketResponse<PageResponse<CategoryResponse>>, CustomError> {
        return dataSource.getCategories(pageNumber, pageSize)
    }

    override suspend fun getCategory(id: String): CategoryResponse {
        return dataSource.getCategory(id)
    }
}