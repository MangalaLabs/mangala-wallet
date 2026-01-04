package com.mangala.wallet.features.home.presentation

sealed class EosAccountCreatedNotificationUIState {
    data class Success(val uiModel: EosAccountNoticeModel) : EosAccountCreatedNotificationUIState()
    data class Failed(val message: String, val eosAccountNoticeBody: EosAccountNoticeBody): EosAccountCreatedNotificationUIState()
    data object Default: EosAccountCreatedNotificationUIState()
}