package com.wallet.iap.purchases

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.serialization.json.Json
import java.io.Serializable
import java.util.concurrent.Executor

public actual class OpenIapScreen(
    private val applicationContext: Context
) : Serializable {

    @Transient
    private var fragmentManager: FragmentManager? = null

    @Transient
    public var scanResult: String = ""

    public fun bind(lifecycle: Lifecycle, fragmentManager: FragmentManager) {
        this.fragmentManager = fragmentManager

        val observer = object : LifecycleObserver {

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroyed(source: LifecycleOwner) {
                this@OpenIapScreen.fragmentManager = null
                source.lifecycle.removeObserver(this)
            }
        }

        lifecycle.addObserver(observer)
    }

    private fun getResolverFragment(): ResolverFragment {
        val fragmentManager: FragmentManager = fragmentManager
            ?: error("can't open IAP without active window")

        val currentFragment: Fragment? = fragmentManager
            .findFragmentByTag(IAP_RESOLVER_FRAGMENT_TAG)

        return if (currentFragment != null) {
            currentFragment as ResolverFragment
        } else {
            ResolverFragment().apply {
                fragmentManager
                    .beginTransaction()
                    .add(this, IAP_RESOLVER_FRAGMENT_TAG)
                    .commitNow()
            }
        }
    }

    public class ResolverFragment : Fragment() {
        private lateinit var executor: Executor

        init {
            retainInstance = true
        }


        private var paymentResultListener: PaymentResultListener? = null

        public fun setPaymentResultListener(listener: PaymentResultListener) {
            paymentResultListener = listener
        }

        public fun openIapScreen(
            accountName: String,
            blockchainUid: String
        ) {
            val context = requireContext()

            executor = ContextCompat.getMainExecutor(context)

            run {
                val intent = Intent(
                    context,
                    Class.forName("com.wallet.iap.purchases.presentation.EosAccountPaywallActivity")
                )
                intent.putExtra("EXTRA_ACCOUNT_NAME", accountName)
                intent.putExtra("EXTRA_BLOCKCHAIN_UID", blockchainUid)
//            intent.putExtra("EXTRA_RPC_SERVER_URL", rpcUrl)
//            intent.putExtra("EXTRA_ACCOUNT_ID", accountId)
//        val gson = Gson()
//        val json = gson.toJson(chainNetworks)
//        intent.putExtra(BrowserActivityNavigationUtils.EXTRA_CHAIN_NETWORK, json)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            context.startActivity(intent)
                startActivityForResult(intent, REQUEST_CODE)
            }

        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            Log.d("TestIAP", "ActivityResult")
            if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                // Handle the scanned QR Code data here
                val state = data?.getIntExtra(PaymentConstant.EXTRA_STATE, -1)
                val msg = data?.getStringExtra(PaymentConstant.EXTRA_INFO)

                Log.d("TestIAP", "ActivityResult state: $state, msg: $msg")
                val paymentInfo = Json.decodeFromString<PaymentInfo>(msg.orEmpty())
                paymentResultListener?.onPaymentResult(
                    PaymentState.fromInt(state ?: 0),
                    msg,
                    paymentInfo
                )
                //...
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }

        companion object {
            const val REQUEST_CODE = 10001
        }

    }

    public actual fun openIapScreen(
        paymentResultListener: PaymentResultListener,
        accountName: String,
        blockchainUid: String
    ) {
        initResolverFragment(paymentResultListener, accountName, blockchainUid)
    }


    private fun initResolverFragment(
        paymentResultListener: PaymentResultListener,
        accountName: String,
        blockchainUid: String
    ) {
        val resolverFragment: ResolverFragment = getResolverFragment()
        resolverFragment.openIapScreen(accountName, blockchainUid)
        resolverFragment.setPaymentResultListener(object : PaymentResultListener {

            override fun onPaymentResult(
                state: PaymentState?,
                message: String?,
                paymentInfo: PaymentInfo?
            ) {
                paymentResultListener.onPaymentResult(state, message, paymentInfo)
            }
        })
    }

    public companion object {
        private const val IAP_RESOLVER_FRAGMENT_TAG = "IAP_RESOLVER_FRAGMENT_TAG"
    }

}