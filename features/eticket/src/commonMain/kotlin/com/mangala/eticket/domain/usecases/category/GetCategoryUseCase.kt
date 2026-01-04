package com.mangala.eticket.domain.usecases.category

import com.mangala.eticket.data.model.category.CategoryResponse
import com.mangala.eticket.domain.repository.CategoriesRepository

class GetCategoryUseCase (private val repository: CategoriesRepository) {

    suspend operator fun invoke(
        id: String
    ): CategoryResponse {
        return repository.getCategory(id)
    }
}