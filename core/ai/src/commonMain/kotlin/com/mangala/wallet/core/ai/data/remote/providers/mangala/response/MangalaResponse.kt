package com.mangala.wallet.core.ai.data.remote.providers.mangala.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MangalaResponseItem(
    @SerialName("recipient_id")
    val recipientId: String? = null,
    
    @SerialName("text")
    val text: String? = null,
    
    @SerialName("custom")
    val custom: CustomAction? = null
) {
    @Serializable
    data class CustomAction(
        @SerialName("action_type")
        val actionType: String,

        @SerialName("data")
        val data: ActionData? = null,

        @SerialName("ui_hints")
        val uiHints: UiHints? = null
    )

    @Serializable
    data class ActionData(
        @SerialName("contact_name")
        val contactName: String? = null,

        @SerialName("networks")
        val networks: List<NetworkInfo>? = null
    )

    @Serializable
    data class NetworkInfo(
        @SerialName("id")
        val id: String,

        @SerialName("name")
        val name: String,

        @SerialName("symbol")
        val symbol: String? = null
    )

    @Serializable
    data class UiHints(
        @SerialName("show_network_picker")
        val showNetworkPicker: Boolean? = null,

        @SerialName("allow_text_input")
        val allowTextInput: Boolean? = null
    )
}

typealias MangalaResponse = List<MangalaResponseItem>