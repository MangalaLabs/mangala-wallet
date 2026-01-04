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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mangala.browser_bridge_api.ActionTransactionCallback
import com.mangala.browser_bridge_base.personal.SignPersonalMessageScreen
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.MangalaAppTheme
import com.mangala.wallet.ui.utils.navigation.BackHandler
import com.mangala.wallet.ui.utils.navigation.LocalBackPressedHandler
import org.koin.core.component.KoinComponent

class SignPersonalMessageDialog: BottomSheetDialogFragment(), KoinComponent {

    private val url get() = arguments?.getString(EXTRA_URL) ?: ""
    private val callbackId get() = arguments?.getLong(EXTRA_CALLBACK_ID) ?: 0L
    private val message get() = arguments?.getByteArray(EXTRA_MESSAGE)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MangalaAppTheme {
                    Navigator(SignPersonalMessageScreen(
                        url = url,
                        callbackId = callbackId,
                        message = message,
                        onSign = {
                            actionTransactionCallback?.onSignMessageSuccessful(callbackId, it)
                            Handler(Looper.getMainLooper()).postDelayed({
                                dismiss()
                            }, 200)
                        },
                        onConfirm = {
                            confirmClicked(it)
                        },
                        onDecline = {
                            dismiss()
                        }
                    ), onBackPressed = {
                        BackHandler.handleBackPressed(it)
                    })
                    { navigator ->
                        CompositionLocalProvider(LocalGlobalNavigator provides navigator) {
                            CurrentScreen()
                        }
                    }
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        actionTransactionCallback?.dismissed(callbackId, false)
    }

    private var actionTransactionCallback: ActionTransactionCallback? = null
    fun setActionTransactionCallback(actionTransactionCallback: ActionTransactionCallback?) {
        this.actionTransactionCallback = actionTransactionCallback
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
    }

    private fun confirmClicked(isOpen: Boolean) {
        behavior.state = if(isOpen){
            BottomSheetBehavior.STATE_EXPANDED
        }else{
            BottomSheetBehavior.STATE_COLLAPSED
        }
    }


    companion object {

        private const val EXTRA_URL = "EXTRA_URL"
        private const val EXTRA_CALLBACK_ID = "EXTRA_CALLBACK_ID"
        private const val EXTRA_MESSAGE = "EXTRA_MESSAGE"

        private fun newInstance(
            url: String,
            callbackId: Long,
            message: ByteArray?
        ) = SignPersonalMessageDialog().apply {
                arguments = Bundle().apply {
                    putString(EXTRA_URL, url)
                    putLong(EXTRA_CALLBACK_ID, callbackId)
                    putByteArray(EXTRA_MESSAGE, message)
                }
            }

        fun showDialogFragment(
            fragmentManager: FragmentManager?,
            url: String,
            callbackId: Long,
            message: ByteArray?
        ): SignPersonalMessageDialog {
            val transaction = fragmentManager?.beginTransaction()
            val previous =
                fragmentManager?.findFragmentByTag("SignPersonalMessageDialog")
            if (previous != null) {
                transaction?.remove(previous)
            }
            transaction?.addToBackStack(null)
            val dialogFragment = newInstance(
                url,
                callbackId,
                message
            )
            transaction?.let {
                dialogFragment.show(
                    transaction,
                    "SignPersonalMessageDialog"
                )
            }
            return dialogFragment
        }
    }
}