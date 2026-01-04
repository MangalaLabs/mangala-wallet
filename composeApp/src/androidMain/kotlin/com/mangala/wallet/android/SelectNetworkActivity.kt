package com.mangala.wallet.android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.mangala.wallet.ui.NetworkActivityComposeView

class SelectNetworkActivity: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            NetworkActivityComposeView(){
                Log.d("219", "SelectNetworkActivity: $it")
                val intent = Intent()
                intent.putExtra(EXTRA_CHAIN_ID, it)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    companion object {
        const val EXTRA_CHAIN_ID = "EXTRA_CHAIN_ID"
    }
}