package com.mangala.eticket.domain.usecases.category

import com.mangala.eticket.data.model.ETicketResponse
import com.mangala.eticket.data.model.PageResponse
import com.mangala.eticket.data.model.category.CategoryResponse
import com.mangala.eticket.di.ApiResponse
import com.mangala.eticket.domain.repository.CategoriesRepository
import com.mangala.eticket.network.CustomError

class GetCategoriesUseCase (private val repository: CategoriesRepository) {

    suspend operator fun invoke(
        pageNumber: Int, pageSize: Int
    ): ApiResponse<ETicketResponse<PageResponse<CategoryResponse>>, CustomError> {
        return repository.getCategories(pageNumber, pageSize)
    }
}