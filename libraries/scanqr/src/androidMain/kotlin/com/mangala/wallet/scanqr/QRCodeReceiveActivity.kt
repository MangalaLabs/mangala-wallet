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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.ui.MangalaAppTheme
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class QRCodeReceiveActivity : ComponentActivity(), KoinComponent {

    private val receiveScreenProvider: ReceiveScreenProvider by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val accountId = intent.getStringExtra(ACCOUNT_ID).orEmpty()
        val initialBlockchainUid = intent.getStringExtra(INITIAL_BLOCKCHAIN_UID) // avoid initialBlockchainUid == ""
        val networkTypeString = intent.getStringExtra(NETWORK_TYPE)
        val networkType = networkTypeString?.let { NetworkType.valueOf(it) }!!

        setContent {
            MangalaAppTheme {
                QRCodeReceiveComposeView(
                    receiveScreenProvider.provideReceiveScreen(
                        accountId = accountId,
                        onBackPressed = {
                            finish()
                        },
                        networkType = networkType,
                        initialBlockchainUid = initialBlockchainUid
                    )
                )
            }
        }
    }

    companion object {
        const val ACCOUNT_ID = "account_id"
        const val INITIAL_BLOCKCHAIN_UID = "INITIAL_BLOCKCHAIN_UID"
        const val NETWORK_TYPE = "NETWORK_TYPE"
    }
}