package com.mangala.eticket.domain.usecases.reset

import com.mangala.eticket.data.local.cache.RemotePagingKeyLocalDataSource
import com.mangala.eticket.data.local.event.EventListLocalDataSource
import com.mangala.eticket.data.local.user.login.AuthenticationLocalDataSource
import com.mangala.wallet.domain.reset.usecases.ClearETicketDataUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ClearETicketDataUseCaseImpl(
    private val authenticationLocalDataSource: AuthenticationLocalDataSource,
    private val remotePagingKeyLocalDataSource: RemotePagingKeyLocalDataSource,
    private val eventListLocalDataSource: EventListLocalDataSource
) : ClearETicketDataUseCase {

    override suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            authenticationLocalDataSource.clearAll()
            remotePagingKeyLocalDataSource.clearAll()
            eventListLocalDataSource.clearAll()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}