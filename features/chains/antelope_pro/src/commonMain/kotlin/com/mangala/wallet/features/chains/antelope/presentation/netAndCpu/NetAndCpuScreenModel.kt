package com.mangala.wallet.features.chains.antelope.presentation.netAndCpu

import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.rentViaRex.GetRexRateUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.delegate.GetDelegateRateUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.powerup.GetPowerUpRateUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NetAndCpuScreenModel(
    private val isCpu: Boolean,
    private val getPowerUpRateUseCase: GetPowerUpRateUseCase,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getDelegateRateUseCase: GetDelegateRateUseCase,
    private val getRexRateUseCase: GetRexRateUseCase
) : BaseScreenModel() {

    private lateinit var blockchainType: BlockchainType

    private val _uiState: MutableStateFlow<NetAndCpuScreenUiState> =
        MutableStateFlow(NetAndCpuScreenUiState.Loading)
    val uiState: StateFlow<NetAndCpuScreenUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            blockchainType = getSelectedNetworkUseCase().blockchainType

            val powerUpRateAsync = async {
                if (isCpu) {
                    getPowerUpRateUseCase.getCpuPricePerMs(
                        blockchainType,
                        forceRefresh = false,
                        BigDecimal.ONE
                    )
                } else {
                    getPowerUpRateUseCase.getNetPricePerKb(
                        blockchainType,
                        forceRefresh = false,
                        BigDecimal.ONE
                    )
                }
            }
            val stakeRateAsync = async {
                if (isCpu) {
                    getDelegateRateUseCase.getCpuPricePerMs(blockchainType)
                } else {
                    getDelegateRateUseCase.getNetPricePerKb(blockchainType)
                }
            }
            val rexRateAsync = async {
                if (isCpu) {
                    getRexRateUseCase.getCpuPricePerMs(
                        blockchainType,
                        forceRefresh = false,
                        BigDecimal.ONE
                    )
                } else {
                    getRexRateUseCase.getNetPricePerKb(
                        blockchainType,
                        forceRefresh = false,
                        BigDecimal.ONE
                    )
                }
            }

            val powerUpResult = powerUpRateAsync.await()
            val stakeRateResult = stakeRateAsync.await()
            val rexRateResult = rexRateAsync.await()

            val powerUpRate = powerUpResult.getOrNull()
            val stakeRate = stakeRateResult.getOrNull()
            val rexRate = rexRateResult.getOrNull()

            if (powerUpRate == null || stakeRate == null || rexRate == null) {
                // TODO: Handle failure
                return@launch
            }

            _uiState.value = NetAndCpuScreenUiState.Loaded(
                powerUpRate = powerUpRate.rate.roundToDigitPositionAfterDecimalPoint(
                    powerUpRate.sampleUsage.nativeCoinPrecision.toLong(),
                    RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
                ),
                rexRate = rexRate.roundToDigitPositionAfterDecimalPoint(
                    powerUpRate.sampleUsage.nativeCoinPrecision.toLong(),
                    RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
                ),
                stakingRate = stakeRate.roundToDigitPositionAfterDecimalPoint(
                    powerUpRate.sampleUsage.nativeCoinPrecision.toLong(),
                    RoundingMode.ROUND_HALF_AWAY_FROM_ZERO
                ),
                nativeCoinPrecision = powerUpRate.minPowerUpFee.precision
            )
        }
    }
}