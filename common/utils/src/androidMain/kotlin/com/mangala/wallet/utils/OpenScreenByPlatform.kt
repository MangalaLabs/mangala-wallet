package com.mangala.wallet.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import java.io.Serializable

actual class OpenScreenByPlatform(private val applicationContext: Context): Serializable {

//    @Transient
//    private var fragmentManager: FragmentManager? = null
//
//    fun bind(lifecycle: Lifecycle, fragmentManager: FragmentManager) {
//        this.fragmentManager = fragmentManager
//
//        val observer = object : LifecycleObserver {
//
//            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
//            fun onDestroyed(source: LifecycleOwner) {
//                this@OpenScreenByPlatform.fragmentManager = null
//                source.lifecycle.removeObserver(this)
//            }
//        }
//
//        lifecycle.addObserver(observer)
//    }

    actual fun openNewScreen(){

    }

    actual fun openBrowser(
        chainId: Long,
        address: String,
        rpcUrl: String,
        accountId: String
    ) {
        val intent = Intent(applicationContext, Class.forName("com.mangala.app.launch.LaunchBridgeActivity"))
        intent.putExtra("EXTRA_CHAIN_ID", chainId)
        intent.putExtra("ADDRESS", address)
        intent.putExtra("EXTRA_RPC_SERVER_URL", rpcUrl)
//        val gson = Gson()
//        val json = gson.toJson(chainNetworks)
//        intent.putExtra(BrowserActivityNavigationUtils.EXTRA_CHAIN_NETWORK, json)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(intent)
    }

}