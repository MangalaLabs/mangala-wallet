package com.mangala.features.browser

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import java.io.Serializable
import java.util.concurrent.Executor

actual class OpenBrowser() : Serializable {

    @Transient
    private var fragmentManager: FragmentManager? = null

    fun bind(lifecycle: Lifecycle, fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager

        val observer = object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroyed(source: LifecycleOwner) {
                this@OpenBrowser.fragmentManager = null
                source.lifecycle.removeObserver(this)
            }
        }

        lifecycle.addObserver(observer)
    }

    private fun getResolverFragment(): ResolverFragment {
        val fragmentManager: FragmentManager = fragmentManager
            ?: error("can't check scan QR code without active window")

        val currentFragment: Fragment? = fragmentManager
            .findFragmentByTag(OPEN_BROWSER_RESOLVER_FRAGMENT_TAG)

        return if (currentFragment != null) {
            currentFragment as ResolverFragment
        } else {
            ResolverFragment().apply {
                fragmentManager
                    .beginTransaction()
                    .add(this, OPEN_BROWSER_RESOLVER_FRAGMENT_TAG)
                    .commitNow()
            }
        }
    }

    class ResolverFragment : Fragment() {
        private lateinit var executor: Executor

        init {
            retainInstance = true
        }

        fun openBrowser(
            chainId: Long,
            address: String,
            rpcUrl: String,
            accountId: String
        ) {
            val context = requireContext()
            val intent = Intent(
                context,
                Class.forName("com.mangala.app.launch.LaunchBridgeActivity")
            )
            intent.putExtra("EXTRA_CHAIN_ID", chainId)
            intent.putExtra("ADDRESS", address)
            intent.putExtra("EXTRA_RPC_SERVER_URL", rpcUrl)
            intent.putExtra("EXTRA_ACCOUNT_ID", accountId)
//        val gson = Gson()
//        val json = gson.toJson(chainNetworks)
//        intent.putExtra(BrowserActivityNavigationUtils.EXTRA_CHAIN_NETWORK, json)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }


    actual fun openNewScreen() {

    }

    actual fun openBrowser(
        chainId: Long,
        address: String,
        rpcUrl: String,
        accountId: String
    ) {
        val resolverFragment: ResolverFragment = getResolverFragment()
        resolverFragment.openBrowser(chainId, address, rpcUrl, accountId)

    }

    actual fun putData(
        chainId: Long,
        address: String,
        rpcUrl: String,
        accountId: String
    ) {
    }

    companion object {
        private const val OPEN_BROWSER_RESOLVER_FRAGMENT_TAG = "OPEN_BROWSER_RESOLVER_FRAGMENT_TAG"
    }
}