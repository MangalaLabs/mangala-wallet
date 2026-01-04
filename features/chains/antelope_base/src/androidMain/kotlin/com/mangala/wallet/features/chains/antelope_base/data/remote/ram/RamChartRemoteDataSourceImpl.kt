package com.mangala.wallet.features.chains.antelope_base.data.remote.ram

import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.getHttpsCallableFromUrl
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.FirebasePerformance
import com.google.firebase.perf.metrics.Trace
import com.mangala.antelope.base.api.model.eoseyes.RamOhlcResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError
import kotlinx.coroutines.tasks.await
import java.net.URL

class RamChartRemoteDataSourceImpl : RamChartRemoteDataSource {
    private val functions = Firebase.functions

    override suspend fun getRamOhlcData(
        blockchainType: BlockchainType,
        unit: String,
        start: Long,
        end: Long
    ): ApiResponse<RamOhlcResponse, CustomError> {
        val  trace: Trace = FirebasePerformance.getInstance().newTrace("get_ram_ohlc_data")

        try {
            trace.start()
            trace.putAttribute("blockchainType", blockchainType.name)
            trace.putAttribute("unit", unit)

            return when (blockchainType) {

                BlockchainType.Eos -> {

                    val data = hashMapOf(
                        "unit" to unit,
                        "start" to start,
                        "end" to end
                    )
                    try {
                        val result = functions
                            .getHttpsCallableFromUrl(URL(RAM_CHART_CLOUD_FUNCTION_URL)) {
                                limitedUseAppCheckTokens = true
                            }
                            .call(data)
                            .await()

                        val rawResult = result.data as Map<*, *>

                        return ApiResponse.Success(
                            RamOhlcResponse(
                                code = rawResult["code"] as? Int,
                                data = (rawResult["data"] as? List<Map<*, *>>)?.map {
                                    RamOhlcResponse.Data(
                                        close = it["close"] as? Double,
                                        date = it["date"] as? Long,
                                        high = it["high"] as? Double,
                                        low = it["low"] as? Double,
                                        open = it["open"] as? Double,
                                        usd = it["usd"] as? Double,
                                        volume = it["volume"] as? Long
                                    )
                                },
                                message = rawResult["message"] as? String
                            )
                        )
                    } catch (e: Exception) {
                        println("Exception: ${e.message}")
                        if (e is FirebaseFunctionsException) {
                            val code = e.code

                            ApiResponse.Error.CustomError(
                                code.ordinal,
                                CustomError(e.localizedMessage)
                            )
                        } else {
                            ApiResponse.Error.UnknownError(e.message ?: "Error")
                        }
                    }
                }

                BlockchainType.EosJungleTestnet -> {
                    ApiResponse.Error.UnknownError("Unsupported blockchain type for RAM OHLC data fetching")
                }
                else -> {
                    throw IllegalArgumentException("Unsupported blockchain type")
                }

            }
        } finally {
            trace.stop()
        }
    }

    companion object {
        private const val RAM_CHART_CLOUD_FUNCTION_URL =
            "https://asia-southeast1-mangala-wallet-cb7c1.cloudfunctions.net/ramPrice"
    }
}