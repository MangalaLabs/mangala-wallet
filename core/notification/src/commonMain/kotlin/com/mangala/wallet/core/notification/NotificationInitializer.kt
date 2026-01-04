package com.mangala.wallet.core.notification

import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.PayloadData
import dev.theolm.rinku.Rinku
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object NotificationInitializer : KoinComponent {
    private val applicationStartPlatformSpecific: ApplicationStartPlatformSpecific by inject()

    fun onApplicationStart() {
        applicationStartPlatformSpecific.onApplicationStartPlatformSpecific()
    }
}

fun initNotificationListener() {
    NotifierManager.addListener(object : NotifierManager.Listener {
        override fun onNewToken(token: String) {
        }

        override fun onPushNotification(title: String?, body: String?) {
            super.onPushNotification(title, body)
        }

        override fun onPayloadData(data: PayloadData) {

        }

        override fun onNotificationClicked(data: PayloadData) {
            when (data[NOTIFICATION_PAYLOAD_KEY_TYPE]) {
                in NotificationType.entries.map { it.name } -> {
                    val proposalName = data["proposalName"]
                    val submitter = data["proposer"]
                    val chainId = data["chainId"]

                    val deepLink = buildString {
                        append("$APP_NOTIFICATION_SCHEME://$APP_NOTIFICATION_HOST/$PATH_MULTISIG")
                        append("?proposalName=${proposalName ?: ""}")
                        append("&submitter=${submitter ?: ""}")
                        if (chainId != null) {
                            append("&chainId=$chainId")
                        }
                    }

                    Rinku.handleDeepLink(deepLink)
                }
            }
        }
    })
}

enum class NotificationType {
    NEW_PROPOSAL,
    APPPOVED_PROPOSAL,
    CANCEL_PROPOSAL,
    EXECUTED_ACTOR_PROPOSAL,
    EXECUTED_REQUESTER_PROPOSAL,
    REVOKE_REQUESTER_PROPOSAL,
    INVALID_ACTOR_ACCOUNT
}

const val NOTIFICATION_PAYLOAD_KEY_TYPE = "type"

const val APP_NOTIFICATION_SCHEME = "app"
const val APP_NOTIFICATION_HOST = "mangala"

const val PATH_MULTISIG = "multisig"