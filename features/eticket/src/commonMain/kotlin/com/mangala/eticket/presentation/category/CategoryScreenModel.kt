package com.mangala.eticket.presentation.category

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.eticket.data.model.category.CategoryResponse
import com.mangala.eticket.di.ApiResponse
import com.mangala.eticket.domain.usecases.category.GetCategoriesUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class CategoryScreenModel(
    private val getCategoriesUseCase: GetCategoriesUseCase
): BaseScreenModel() {

    private var _categories = MutableStateFlow<List<CategoryResponse>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _unknownError = MutableStateFlow(false)
    val unknownError = _unknownError.asStateFlow()

    init {
        screenModelScope.launch {
            getCategoriesUseCase.invoke(0, 20).let {
                if (it is ApiResponse.Success) {
                    it.body.data?.let { data ->
                        _categories.value = data.content
                    }  ?: ""
                } else {
                    _unknownError.value = true
                }
            }
        }
    }
}