package com.mangala.wallet.remote.utils

import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.di.ApiResponse
import io.ktor.util.network.UnresolvedAddressException
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

inline fun <T> networkBoundResource(
    crossinline query: () -> Flow<T>,
    crossinline fetch: suspend () -> Resource<T>,
    crossinline saveFetchResult: suspend (Resource<T>) -> Unit,
    crossinline shouldFetch: suspend (T) -> Boolean = { true },
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) = flow {

    //First step, fetch data from the local cache
    val initQuery = query()
    val data = initQuery.first()

    //If shouldFetch returns true,
    val resource = if (shouldFetch(data)) {

        //Dispatch a message to the UI that you're doing some background work
        emit(Resource.Loading(data))

        try {

            //make a networking call
            val resultType = fetch()

            when {
                resultType is Resource.Success -> {
                    //save it to the database
                    saveFetchResult(resultType)
                    //Now fetch data again from the database and Dispatch it to the UI
                    query().map { Resource.Success(it) }
                }

                resultType is Resource.Error -> {
                    //Dispatch any error emitted to the UI, plus data emitted from the Database
                    initQuery.map { Resource.Error(resultType.exception, it) }
                }

                resultType is Resource.Loading -> {
                    initQuery.map { Resource.Loading(it) }
                }

                else -> {
                    initQuery.map { Resource.Error(Exception("UnknownException"), it) }
                }
            }

        } catch (e: UnresolvedAddressException) {

            initQuery.map { Resource.Success(it) }
        } catch (e: IOException) {

            initQuery.map { Resource.Success(it) }
        } catch (throwable: Throwable) {

            //Dispatch any error emitted to the UI, plus data emmited from the Database
            initQuery.map { Resource.Error(Exception(throwable.message), it) }

        }

        //If should fetch returned false
    } else {
        //Make a query to the database and Dispatch it to the UI.
        query().map { Resource.Success(it) }
    }

    //Emit the resource variable
    emitAll(resource)
}.flowOn(ioDispatcher)

inline fun <Entity, Dto, Domain> networkBoundResource(
    crossinline query: () -> Flow<Entity>,
    crossinline fetch: suspend (cachedData: Entity) -> ApiResponse<Dto, *>,
    crossinline saveFetchResult: suspend (Dto) -> Unit,
    crossinline shouldFetch: suspend (Entity) -> Boolean,
    crossinline entityToDomain: suspend (Entity) -> Domain,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): Flow<Resource<Domain>> = flow {


    val initQuery = query()
    val data = initQuery.first()

    //If shouldFetch returns true,
    val resource = if (shouldFetch(data)) {

        //Dispatch a message to the UI that you're doing some background work
        emit(Resource.Loading(entityToDomain(data)))

        try {
            when (val networkResult = fetch(data)) {
                is ApiResponse.Success -> {
                    saveFetchResult(networkResult.body)
                    query().map { Resource.Success(entityToDomain(it)) }
                }

                is ApiResponse.Error -> {
                    initQuery.map {
                        Resource.Error(
                            Exception(networkResult.toString()),
                            entityToDomain(it)
                        )
                    }
                }
            }

        } catch (e: UnresolvedAddressException) {
            println("cachedResource: UnresolvedAddressException $e")
            initQuery.map { Resource.Error(exception = e, entityToDomain(it)) }
        } catch (e: IOException) {
            println("cachedResource: IOException $e")
            initQuery.map { Resource.Error(exception = e, entityToDomain(it)) }
        } catch (throwable: Throwable) {

            println("cachedResource: Throwable $throwable")
            //Dispatch any error emitted to the UI, plus data emmited from the Database
            initQuery.map { Resource.Error(Exception(throwable.message), entityToDomain(it)) }

        }

        //If should fetch returned false
    } else {
        //Make a query to the database and Dispatch it to the UI.
        query().map { Resource.Success(entityToDomain(it)) }
    }

    //Emit the resource variable
    emitAll(resource)
}.flowOn(ioDispatcher)