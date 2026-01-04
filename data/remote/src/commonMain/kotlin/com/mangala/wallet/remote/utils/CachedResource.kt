package com.mangala.wallet.remote.utils

import com.mangala.wallet.remote.di.ApiResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

@Deprecated(
    message = "Use networkBoundResource instead",
    replaceWith = ReplaceWith(
        expression = """
            inline fun <Entity, Dto, Domain> networkBoundResource(
                crossinline query: () -> Flow<Entity>,
                crossinline fetch: suspend (cachedData: Entity) -> ApiResponse<Dto, *>,
                crossinline saveFetchResult: suspend (Dto?) -> Unit,
                crossinline shouldFetch: (Entity) -> Boolean = { true },
                crossinline entityToDomain: (Entity) -> Domain
            ): Flow<Resource<Domain?>>
        """
    )
)
suspend inline fun <Entity, Dto, Domain> cachedResource(
    crossinline query: suspend () -> Entity,
    crossinline fetch: suspend () -> ApiResponse<Dto, *>,
    crossinline saveFetchResult: suspend (Dto) -> Unit,
    crossinline shouldFetch: suspend (Entity) -> Boolean,
    crossinline entityToDomain: (Entity) -> Domain,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): Result<Domain> = withContext(ioDispatcher) {
    val data = query()

    if (shouldFetch(data)) {
        return@withContext try {
            when (val networkResult = fetch()) {
                is ApiResponse.Success -> {
                    saveFetchResult(networkResult.body)
                    Result.success(entityToDomain(query()))
                }

                is ApiResponse.Error -> {
                    Result.failure(Exception("Failed to fetch data"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    } else {
        return@withContext Result.success(entityToDomain(data))
    }
}