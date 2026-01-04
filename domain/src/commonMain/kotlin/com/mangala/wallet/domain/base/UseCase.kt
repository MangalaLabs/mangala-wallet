package com.mangala.wallet.domain.base

abstract class UseCase<out Type> where Type : Any {
    abstract suspend fun run(params: Map<String, Any?>): Type

    suspend operator fun invoke(params: Map<String, Any?>) = run(params)
}