package com.mangala.wallet.remote.category_dapps

import com.mangala.wallet.model.category_dapp.remote.Category
import com.mangala.wallet.model.category_dapp.remote.DAppRemote
import com.mangala.wallet.model.category_dapp.remote.BaseResponse
import com.mangala.wallet.model.category_dapp.remote.CategoryDApp
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface CategoryDAppApi {
        @GET("api/v1/categories")
        suspend fun getCategories(): BaseResponse<List<Category>>

        @GET("api/v1/dapp-categories")
        suspend fun getDappsByCategories(@Query("category_uuids") categoryUuids: List<String>): BaseResponse<List<CategoryDApp>>

        @GET("api/v1/dapps")
        suspend fun getDappsByCategory(@Query("category_uuid") categoryUuid: String): BaseResponse<List<DAppRemote>>

        @GET("songtoan0302/json/master/dapp.json")
        suspend fun getDappsJson(): String
}