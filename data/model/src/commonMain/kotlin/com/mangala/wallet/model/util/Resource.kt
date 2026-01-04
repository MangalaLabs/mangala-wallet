package com.mangala.wallet.model.util

sealed class Resource<out T>(val data: T?) {
    data class Success<out T>(private val dataa: T) : Resource<T>(dataa)
    data class Error<out T>(val exception: Throwable, private val dataa: T? = null) : Resource<T>(dataa)
    data class Loading<out T>(private val dataa: T?) : Resource<T>(dataa)

    fun isLoading() = this is Loading
    fun isError() = this is Error

    fun <X> map(transform: (T?) -> X): Resource<X> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(exception, data?.let { transform(it) })
            is Loading -> Loading(transform(data))
        }
    }

    suspend fun <X> mapSuspend(transform: suspend (T?) -> X): Resource<X> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> Error(exception, data?.let { transform(it) })
            is Loading -> Loading(transform(data))
        }
    }

    suspend fun <X> mapSuspendNonNull(transform: suspend (T) -> X): Resource<X> {
        return when (this) {
            is Success -> Success(transform(data!!))
            is Error -> Error(exception, data?.let { transform(it) })
            is Loading -> Loading(transform(data!!))
        }
    }
}

fun Resource<List<*>?>.isLoadingWithEmptyData(): Boolean {
    return this is Resource.Loading && data.isNullOrEmpty()
}