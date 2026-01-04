package com.mangala.wallet.features.chains.antelope_base.data.repository.createaccount

import com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount.CreateAccountRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.BlacklistedAccountRemoteConfig
import com.mangala.wallet.features.chains.antelope_base.domain.repository.createaccount.CreateAccountRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.utils.CrashlyticsUtils
import com.mangala.wallet.utils.remoteconfig.RemoteConfigKey
import com.mangala.wallet.utils.exception.MangalaRemoteException
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlinx.serialization.json.Json

class CreateAccountRepositoryImpl(private val createAccountRemoteDataSource: CreateAccountRemoteDataSource) :
    CreateAccountRepository {

    override suspend fun createAccount(
        accountName: String,
        blockchainType: BlockchainType,
        purchaseToken: String,
        activePublicKey: String,
        ownerPublicKey: String
    ): Result<String> {
        val response = createAccountRemoteDataSource.createAccount(
            accountName,
            blockchainType,
            purchaseToken,
            activePublicKey,
            ownerPublicKey
        )

        return when (response) {
            is ApiResponse.Success -> {
                Result.success("Create account success")
            }
            is ApiResponse.Error.NetworkError -> {
                Result.failure((MangalaRemoteException.NetworkException(response.exception)))
            }

            is ApiResponse.Error.CustomError -> {
                Result.failure(
                    MangalaRemoteException.HttpException(
                        response.code,
                        response.errorBody?.message.toString(),
                        null
                    )
                )
            }

            is ApiResponse.Error.HttpError -> {
                Result.failure(
                    (MangalaRemoteException.HttpException(
                        response.code,
                        response.errorBody,
                        null
                    ))
                )
            }

            is ApiResponse.Error.SerializationError -> {
                Result.failure((MangalaRemoteException.SerializationError))
            }

            is ApiResponse.Error.UnknownError -> {
                Result.failure((MangalaRemoteException.UnknownError(response.message, null)))
            }

            is ApiResponse.Error.CancellationError -> {
                Result.failure((MangalaRemoteException.UnknownError("", null)))
            }
        }
    }

    override fun isInBlackListAccountName(accountName: String): Result<Boolean> {
        try {
            val remoteConfig = Firebase.remoteConfig
            val blackListAccountNameAsString = remoteConfig.getValue(RemoteConfigKey.ANTELOPE_BLACKLISTED_ACCOUNT_NAME).asString()
            val blacklistedAccountRemoteConfig = Json.decodeFromString<BlacklistedAccountRemoteConfig>(blackListAccountNameAsString)
            return Result.success(blacklistedAccountRemoteConfig.data.contains(accountName))
        } catch (e: Exception) {
            CrashlyticsUtils.logNonFatal(e)
            return Result.failure(e)
        }
    }
}