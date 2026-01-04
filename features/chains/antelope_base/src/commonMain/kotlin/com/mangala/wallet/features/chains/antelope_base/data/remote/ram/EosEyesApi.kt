package com.mangala.wallet.features.chains.antelope_base.data.remote.ram

import com.mangala.antelope.base.api.model.eoseyes.RamOhlcResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface EosEyesApi {
    @GET("ram/kline")
    suspend fun getRamOhlcData(
        @Query("unit") unit: String,
        @Query("start") start: Long,
        @Query("end") end: Long
    ): RamOhlcResponse
}