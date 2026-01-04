/*
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mangala.wallet.scanqr

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.mangala.wallet.model.blockchain.NetworkType
import java.io.Serializable
import java.util.concurrent.Executor

actual class ScanQRCode(
    private val applicationContext: Context
) : Serializable {

    @Transient
    private var fragmentManager: FragmentManager? = null

    @Transient var scanResult = ""

    fun bind(lifecycle: Lifecycle, fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager

        val observer = object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroyed(source: LifecycleOwner) {
                this@ScanQRCode.fragmentManager = null
                source.lifecycle.removeObserver(this)
            }
        }

        lifecycle.addObserver(observer)
    }

    private fun getResolverFragment(): ResolverFragment {
        val fragmentManager: FragmentManager = fragmentManager
            ?: error("can't check scan QR code without active window")

        val currentFragment: Fragment? = fragmentManager
            .findFragmentByTag(SCAN_QRCODE_RESOLVER_FRAGMENT_TAG)

        return if (currentFragment != null) {
            currentFragment as ResolverFragment
        } else {
            ResolverFragment().apply {
                fragmentManager
                    .beginTransaction()
                    .add(this, SCAN_QRCODE_RESOLVER_FRAGMENT_TAG)
                    .commitNow()
            }
        }
    }

    class ResolverFragment : Fragment() {
        private lateinit var executor: Executor

        init {
            retainInstance = true
        }


        private var scanQRCodeListener: ScanQRCodeListener? = null

        fun setScanQRCodeListener(listener: ScanQRCodeListener) {
            scanQRCodeListener = listener
        }

        fun showScanQRCode(currentAccountId: String? = null, networkType: NetworkType? = null, initialBlockchainUid: String?) {
            val context = requireContext()

            executor = ContextCompat.getMainExecutor(context)

            run {
                val integrator = IntentIntegrator.forSupportFragment(this@ResolverFragment)
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
                integrator.setPrompt("Scan a QR Code")
                integrator.setCameraId(0)
                if (currentAccountId != null) integrator.addExtra(QRCodeReceiveActivity.ACCOUNT_ID, currentAccountId)
                if (networkType != null) integrator.addExtra(QRCodeReceiveActivity.NETWORK_TYPE, networkType.name)
                if (initialBlockchainUid != null) integrator.addExtra(QRCodeReceiveActivity.INITIAL_BLOCKCHAIN_UID, initialBlockchainUid)
                integrator.setBeepEnabled(false)
                integrator.setOrientationLocked(false)
                integrator.setBarcodeImageEnabled(true)
                integrator.captureActivity = CustomCaptureActivity::class.java
                integrator.initiateScan()
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result.contents != null) {
                // Handle the scanned QR Code data here
                val scannedData = result.contents
                scanQRCodeListener?.onScanQRCodeResult(scannedData)
                //...
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    actual fun scanQRCode(scanQRCodeListener: ScanQRCodeListener) {
        initResolverFragment(scanQRCodeListener)
    }

    actual fun scanQRCode(
        scanQRCodeListener: ScanQRCodeListener,
        currentAccountId: String,
        networkType: NetworkType,
        initialBlockchainUid: String?
    ) {
        initResolverFragment(scanQRCodeListener, currentAccountId, networkType, initialBlockchainUid)
    }

    private fun initResolverFragment(
        scanQRCodeListener: ScanQRCodeListener,
        currentAccountId: String? = null,
        networkType: NetworkType? = null,
        initialBlockchainUid: String? = null
    ) {
        val resolverFragment: ResolverFragment = getResolverFragment()
        resolverFragment.showScanQRCode(currentAccountId, networkType, initialBlockchainUid)
        resolverFragment.setScanQRCodeListener(object : ScanQRCodeListener {
            override fun onScanQRCodeResult(result: String) {
    //                _qrCodeResult.value = result
                scanQRCodeListener.onScanQRCodeResult(result)
            }
        })
    }

    companion object {
        private const val SCAN_QRCODE_RESOLVER_FRAGMENT_TAG = "SCAN_QRCODE_RESOLVER_FRAGMENT_TAG"
    }

}