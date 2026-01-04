package com.mangala.wallet.domain.reset.usecases

interface ClearETicketDataUseCase {
    suspend operator fun invoke(): Result<Unit>
}