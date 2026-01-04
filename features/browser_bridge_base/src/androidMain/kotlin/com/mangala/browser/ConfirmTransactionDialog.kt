package com.mangala.browser

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentManager
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mangala.browser_bridge_api.ActionTransactionCallback
import com.mangala.browser_bridge_base.ConfirmTransactionViewModel
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.MangalaAppTheme
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.utils.navigation.LocalBackPressedHandler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ConfirmTransactionDialog : BottomSheetDialogFragment(), KoinComponent {

    private val viewModel: ConfirmTransactionViewModel by inject()

    private val url get() = arguments?.getString(EXTRA_URL) ?: ""
    private val accountId get() = arguments?.getString(EXTRA_ACCOUNT_ID) ?: ""
    private val coinDecimals get() = arguments?.getLong(EXTRA_COIN_DECIMAL) ?: 0
    private val chainId get() = arguments?.getLong(EXTRA_CHAIN_ID) ?: 0
    private val callbackId get() = arguments?.getLong(EXTRA_CALLBACK_ID) ?: 0
    private val value get() = arguments?.getString(EXTRA_VALUE) ?: ""
    private val recipient get() = arguments?.getString(EXTRA_RECIPIENT) ?: ""
    private val payload get() = arguments?.getString(EXTRA_PAYLOAD) ?: ""
    private val nonce get() = arguments?.getLong(EXTRA_NONCE) ?: 0
    private val isLegacyTransaction
        get() = arguments?.getBoolean(EXTRA_IS_LEGACY_TRANSACTION) ?: false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return ComposeView(requireContext()).apply {
            setContent {
                MangalaAppTheme {
                    var onBackPressed: MutableState<((currentScreen: Screen) -> Boolean)?> = remember { mutableStateOf( { true } )}
                    val confirmTransactionScreen = ScreenRegistry.get(SharedScreen.BrowserConfirmTransactionScreen(
                        url = url,
                        accountId = accountId,
                        coinDecimals = coinDecimals,
                        chainId = chainId,
                        callbackId = callbackId,
                        value = value,
                        recipient = recipient,
                        payload = payload,
                        nonce = nonce,
                        isLegacyTransaction = isLegacyTransaction,
                        onSignMessageFail = {
                            Handler(Looper.getMainLooper()).postDelayed({
                                dismiss()
                            }, 200)
                        },
                        onSignMessageSuccessful = { callbackId, signHex ->

                            actionTransactionCallback?.onSignMessageSuccessful(callbackId, signHex)

                            Handler(Looper.getMainLooper()).postDelayed({
                                dismiss()
                            }, 200)
                        },
                        onConfirm = { confirmClicked(it) },
                        onDecline = { cancelClicked() }
                    ))
                    Navigator(
                        confirmTransactionScreen,
                        onBackPressed = onBackPressed.value
                    ) { navigator ->
                        CompositionLocalProvider(LocalGlobalNavigator provides navigator, LocalBackPressedHandler provides onBackPressed) {
                            CurrentScreen()
                        }
                    }
                }
            }
        }
    }

    private lateinit var behavior: BottomSheetBehavior<*>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val dialog = dialog as BottomSheetDialog
                val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                behavior = BottomSheetBehavior.from(bottomSheet)
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })

        subscribeEventSignTransaction()
    }

    private fun confirmClicked(isOpen: Boolean) {
        behavior.state = if(isOpen){
            BottomSheetBehavior.STATE_EXPANDED
        }else{
            BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        actionTransactionCallback?.dismissed(callbackId, false)
    }

    private fun cancelClicked() {
        dismiss()
//        actionTransactionCallback?.dismissed(callbackId, false)
    }

    private fun subscribeEventSignTransaction() {
//        val response = viewModel.signTransactionResult.asStateFlow()
//        if (response.value != null) {
//            if (response.value?.result.isNullOrEmpty()) {
//                actionTransactionCallback?.transactionError(callbackId, null)
//            } else {
//                actionTransactionCallback?.transactionSuccess(
//                    callbackId,
//                    value,
//                    recipient,
//                    payload,
//                    nonce,
//                    isLegacyTransaction,
//                    response.value?.result ?: ""
//                )
//            }
//        }
    }

    private var actionTransactionCallback: ActionTransactionCallback? = null
    fun setActionTransactionCallback(actionTransactionCallback: ActionTransactionCallback?) {
        this.actionTransactionCallback = actionTransactionCallback
    }

    companion object {

        private const val EXTRA_URL = "EXTRA_URL"
        private const val EXTRA_ACCOUNT_ID = "EXTRA_ACCOUNT_ID"
        private const val EXTRA_COIN_DECIMAL = "EXTRA_COIN_DECIMAL"
        private const val EXTRA_CHAIN_ID = "EXTRA_CHAIN_ID"
        private const val EXTRA_CALLBACK_ID = "EXTRA_CALLBACK_ID"
        private const val EXTRA_VALUE = "EXTRA_VALUE"
        private const val EXTRA_RECIPIENT = "EXTRA_RECIPIENT"
        private const val EXTRA_PAYLOAD = "EXTRA_PAYLOAD"
        private const val EXTRA_NONCE = "EXTRA_NONCE"
        private const val EXTRA_IS_LEGACY_TRANSACTION = "EXTRA_IS_LEGACY_TRANSACTION"
        private const val EXTRA_GAS_LIMIT = "EXTRA_GAS_LIMIT"

        private fun newInstance(
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
            gasLimit: Long
        ) =
            ConfirmTransactionDialog().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_URL, url)
                    putString(EXTRA_ACCOUNT_ID, accountId)
                    putLong(EXTRA_COIN_DECIMAL, coinDecimals)
                    putLong(EXTRA_CHAIN_ID, chainId)
                    putLong(EXTRA_CALLBACK_ID, callbackId)
                    putString(EXTRA_VALUE, value)
                    putString(EXTRA_RECIPIENT, recipient)
                    putString(EXTRA_PAYLOAD, payload)
                    putLong(EXTRA_NONCE, nonce)
                    putBoolean(EXTRA_IS_LEGACY_TRANSACTION, isLegacyTransaction)
                    putLong(EXTRA_GAS_LIMIT, gasLimit)
                }
            }

        fun showDialogFragment(
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
            gasLimit: Long
        ): ConfirmTransactionDialog {
            val transaction = fragmentManager?.beginTransaction()
            val previous =
                fragmentManager?.findFragmentByTag("ConfirmTransactionDialog")
            if (previous != null) {
                transaction?.remove(previous)
            }
            transaction?.addToBackStack(null)
            val dialogFragment = newInstance(
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
            transaction?.let {
                dialogFragment.show(
                    transaction,
                    "ConfirmTransactionBottomDialog"
                )
            }
            return dialogFragment
        }
    }
}