package com.mangala.wallet.features.chains.antelope

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.ui.component.MaxSizeBox
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AntelopeActivity: AppCompatActivity(), KoinComponent {

    private val scanQRCode: ScanQRCode by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scanQRCode.bind(
            lifecycle = this.lifecycle,
            fragmentManager = this.supportFragmentManager
        )

        setContent {
            Navigator(AntelopeUiScreen()) {
                MaxSizeBox(Modifier.background(Color.White)) {
                    CurrentScreen()
                }
            }
        }
    }
}