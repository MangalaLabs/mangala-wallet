package com.mangala.wallet.walletconnect

import android.os.Bundle
import android.text.TextUtils
import android.text.format.DateUtils
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.mangala.wallet.utils.DebugLog

class WalletConnectActivity : AppCompatActivity() {

    private var chainId: Long = 0L
    private var address: String = ""
    private var tabId: String = ""
    private var importPassData: String = ""
    private var url: String = ""
    private var accountId: String = ""
    private var fromDappBrowser = false
    private var fromPhoneBrowser = false
    private var qrCode: String = ""

//    private var session: WCSession? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
        setContent {
            WalletConnectViews(
                chainId = chainId,
                addressConnect = address,
                prevTabId = tabId,
                importPassData = importPassData,
                url = url,
                accountId = accountId,
                onConnectClick = {

                },
                onBackClick = {


                })
        }
    }

    private fun getData() {
        chainId = intent.getLongExtra("EXTRA_CHAIN_ID", 0L)
        address = intent.getStringExtra("EXTRA_ADDRESS_CONNECT") ?: ""
        tabId = intent.getStringExtra("EXTRA_TAB_ID") ?: ""
        importPassData = intent.getStringExtra("EXTRA_IMPORT_PASS_DATA") ?: ""
        url = intent.getStringExtra("EXTRA_URL") ?: ""
        accountId = intent.getStringExtra("EXTRA_ACCOUNT_ID") ?: ""
        val sessionId = intent.getStringExtra("session")
        if (!TextUtils.isEmpty(importPassData)) {
            parseSessionCode(importPassData!!)
        } else if (!TextUtils.isEmpty(sessionId)) {
//            session = viewModel.getSession(sessionId)
        }
    }

    private fun parseSessionCode(wcCode: String) {
        var wcCode = wcCode
        if (wcCode != null && wcCode.startsWith(WC_LOCAL_PREFIX)) {
            wcCode =
                wcCode.replace(WC_LOCAL_PREFIX, "")
            fromDappBrowser = true
        } else if (wcCode != null && wcCode.startsWith(WC_INTENT)) {
            wcCode = wcCode.replace(WC_INTENT, "")
            fromPhoneBrowser =
                true //don't use this yet, but could use it for switching between apps
        }
        this.qrCode = wcCode
//        session = com.trustwallet.walletconnect.models.session.WCSession.Companion.from(qrCode)
    }


    companion object {
        const val WC_LOCAL_PREFIX = "wclocal:"
        const val WC_INTENT = "wcintent:"
        private const val TAG = "WCClient"
        private const val DEFAULT_IDON = "https://example.walletconnect.org/favicon.ico"
        private const val CONNECT_TIMEOUT = 10 * DateUtils.SECOND_IN_MILLIS // 10 Seconds timeout

    }


}