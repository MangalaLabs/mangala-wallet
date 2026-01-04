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

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.Reader
import com.google.zxing.Result
import com.google.zxing.client.android.Intents
import com.google.zxing.common.HybridBinarizer
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.mangala.wallet.libraries.scanqr.R
import com.mangala.wallet.mokoresources.MR
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class CustomCaptureActivity : Activity() {

    private var capture: CaptureManager? = null
    private var barcodeScannerView: DecoratedBarcodeView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        barcodeScannerView = initializeContent()
        capture = CaptureManager(this, barcodeScannerView)
        capture?.initializeFromIntent(intent.apply {
            putExtra(Intents.Scan.SCAN_TYPE, Intents.Scan.MIXED_SCAN)
        }, savedInstanceState)
        capture?.decode()

        val accountId = intent.getStringExtra(QRCodeReceiveActivity.ACCOUNT_ID)
        if (accountId == null) {
            findViewById<TextView>(R.id.text_custom_capture_message).isVisible = false
            findViewById<ImageButton>(R.id.imagebutton_custom_capture_receive).isEnabled = false
        }

        findViewById<ImageButton>(R.id.imagebutton_custom_capture_album).setOnClickListener {
            onClickLoadFromAlbum()
        }
        findViewById<ImageButton>(R.id.imagebutton_custom_capture_receive).setOnClickListener {
            onClickReceive()
        }
    }

    /**
     * Override to use a different layout.
     *
     * @return the DecoratedBarcodeView
     */
    private fun initializeContent(): DecoratedBarcodeView? {
        setContentView(R.layout.activity_custom_capture)
        return findViewById<View>(R.id.zxing_barcode_scanner) as DecoratedBarcodeView
    }

    override fun onResume() {
        super.onResume()
        capture?.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture?.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        capture?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return barcodeScannerView?.onKeyDown(keyCode, event) ?: false || super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            val uri = data?.data
            if (uri != null) {
                try {
                    scanQRImage(uri)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun onClickReceive() {
        val accountId = intent.getStringExtra(QRCodeReceiveActivity.ACCOUNT_ID)
        val initialBlockchainUid = intent.getStringExtra(QRCodeReceiveActivity.INITIAL_BLOCKCHAIN_UID)
        val networkType = intent.getStringExtra(QRCodeReceiveActivity.NETWORK_TYPE)

        val intent = Intent(this, QRCodeReceiveActivity::class.java).apply {
            putExtra(QRCodeReceiveActivity.ACCOUNT_ID, accountId)
            putExtra(QRCodeReceiveActivity.INITIAL_BLOCKCHAIN_UID, initialBlockchainUid)
            putExtra(QRCodeReceiveActivity.NETWORK_TYPE, networkType)
        }
        startActivity(intent)
    }

    private fun onClickLoadFromAlbum() {
        pickImageFromGallery()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        setTransparentStatusBarBackground()

        super.onWindowFocusChanged(hasFocus)
    }

    private fun setTransparentStatusBarBackground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun scanQRImage(imageUri: Uri) {
        var inputStream: InputStream? = null
        try {
            inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val intArray = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

            val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            val reader: Reader = MultiFormatReader()

            try {
                val result = reader.decode(binaryBitmap)
                val intent = transformQrResultToIntent(result)
                setResult(RESULT_OK, intent)
                finish()
            } catch (e: NotFoundException) {
                Toast.makeText(
                    this,
                    MR.strings.message_scan_qr_code_not_found.getString(this),
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            inputStream?.close()
        }
    }

    private fun transformQrResultToIntent(result: Result): Intent {
        return Intent().apply {
            putExtra(Intents.Scan.RESULT, result.text)
            putExtra(Intents.Scan.RESULT_FORMAT, result.barcodeFormat.toString())
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1337
    }
}