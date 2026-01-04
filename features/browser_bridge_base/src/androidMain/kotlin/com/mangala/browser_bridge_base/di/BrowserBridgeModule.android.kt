package com.mangala.browser_bridge_base.di

import androidx.fragment.app.FragmentManager
import com.mangala.browser.ConfirmTransactionDialog
import com.mangala.browser.SignPersonalMessageDialog
import com.mangala.browser.SwitchChainDialog
import com.mangala.browser_bridge_api.ActionTransactionCallback
import com.mangala.browser_bridge_api.ConfirmTransactionCallback
import com.mangala.browser_bridge_api.SignPersonalMessageCallback
import com.mangala.browser_bridge_api.SwitchChainCallback
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun browserBridgePlatformSpecificModule(): Module  = module {
    factory<ConfirmTransactionCallback> {
        object: ConfirmTransactionCallback {
            override fun showDialogFragment(
                fragmentManager: FragmentManager?,
                url: String,
                accountId: String,
                coinDecimals: Long,
                chainId: Long,
                callbackId: Long,
                value: String,
                recipient: String,
                payload: String,
                nonce: Long,
                isLegacyTransaction: Boolean,
                gasLimit: Long,
                actionTransactionCallback: ActionTransactionCallback
            ) {
                val dialog = ConfirmTransactionDialog.showDialogFragment(
                    fragmentManager,
                    url,
                    accountId,
                    coinDecimals,
                    chainId,
                    callbackId,
                    value,
                    recipient,
                    payload,
                    nonce,
                    isLegacyTransaction,
                    gasLimit
                )
                dialog.setActionTransactionCallback(actionTransactionCallback)
            }
        }
    }

    factory<SignPersonalMessageCallback> {
        object : SignPersonalMessageCallback {
            override fun showDialogFragment(
                fragmentManager: FragmentManager?,
                url: String,
                callbackId: Long,
                message: ByteArray?,
                actionTransactionCallback: ActionTransactionCallback
            ) {
                val dialog = SignPersonalMessageDialog.showDialogFragment(
                    fragmentManager,
                    url,
                    callbackId,
                    message
                )
                dialog.setActionTransactionCallback(actionTransactionCallback)
            }
        }
    }

    factory<SwitchChainCallback> {
        object : SwitchChainCallback {
            override fun showDialogFragment(
                fragmentManager: FragmentManager?,
                currentChainId: Long,
                newChainId: Long,
                callbackId: Long,
                actionTransactionCallback: ActionTransactionCallback
            ) {
                val dialog = SwitchChainDialog.showDialogFragment(
                    fragmentManager,
                    currentChainId,
                    newChainId,
                    callbackId
                )
                dialog.setActionTransactionCallback(actionTransactionCallback)
            }
        }
    }
}