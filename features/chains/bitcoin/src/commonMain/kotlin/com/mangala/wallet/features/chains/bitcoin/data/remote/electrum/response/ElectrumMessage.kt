package com.mangala.wallet.features.chains.bitcoin.data.remote.electrum.response

sealed class ElectrumMessage {
    data class Response(val response: ElectrumResponse) : ElectrumMessage()
    data class Notification(val notification: ElectrumNotification) : ElectrumMessage()
}