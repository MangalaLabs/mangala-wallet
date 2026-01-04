package com.mangala.wallet.remote.provider.quicknode

import com.mangala.wallet.model.provider.quicknode.QuickNodeTokenBalanceRequest
import com.mangala.wallet.model.provider.quicknode.QuickNodeTokenBalanceResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.POST

interface QuickNodeApi {
    @POST("/")
    suspend fun getWalletTokenBalance(@Body request: QuickNodeTokenBalanceRequest): QuickNodeTokenBalanceResponse
}