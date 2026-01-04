package com.mangala.wallet.utils.exception

sealed class MangalaRemoteException(cause: Throwable?) : Exception(cause) {
    class NetworkException(cause: Throwable?) : MangalaRemoteException(cause)
    class HttpException(val code: Int, override val message: String, cause: Throwable?) :
        MangalaRemoteException(cause)

    data object SerializationError : MangalaRemoteException(null)
    class UnknownError(override val message: String, cause: Throwable?) :
        MangalaRemoteException(cause)
}
