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
import com.mangala.browser_bridge_base.switchchain.SwitchChainScreen
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.MangalaAppTheme
import com.mangala.wallet.ui.utils.navigation.BackHandler
import com.mangala.wallet.ui.utils.navigation.LocalBackPressedHandler
import org.koin.core.component.KoinComponent

class SwitchChainDialog: BottomSheetDialogFragment(), KoinComponent {

    private val currentChainId get() = arguments?.getLong(EXTRA_CURRENT_CHAIN_ID) ?: 0L
    private val newChainId get() = arguments?.getLong(EXTRA_NEW_CHAIN_ID) ?: 0L
    private val callbackId get() = arguments?.getLong(EXTRA_CALLBACK_ID) ?: 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MangalaAppTheme {
                    Navigator(SwitchChainScreen(
                        currentChainId = currentChainId,
                        newChainId = newChainId,
                        onConfirm = {
                            actionTransactionCallback?.onSwitchChainSuccessful(callbackId, newChainId)
                            Handler(Looper.getMainLooper()).postDelayed({
                                dismiss()
                            }, 200)
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

    companion object {

        private const val EXTRA_CURRENT_CHAIN_ID = "EXTRA_CURRENT_CHAIN_ID"
        private const val EXTRA_NEW_CHAIN_ID = "EXTRA_NEW_CHAIN_ID"
        private const val EXTRA_CALLBACK_ID = "EXTRA_CALLBACK_ID"


        private fun newInstance(
            currentChainId: Long,
            newChainId: Long,
            callbackId: Long
        ) = SwitchChainDialog().apply {
                arguments = Bundle().apply {
                    putLong(EXTRA_CURRENT_CHAIN_ID, currentChainId)
                    putLong(EXTRA_NEW_CHAIN_ID, newChainId)
                    putLong(EXTRA_CALLBACK_ID, callbackId)
                }
            }

        fun showDialogFragment(
            fragmentManager: FragmentManager?,
            currentChainId: Long,
            newChainId: Long,
            callbackId: Long,
        ): SwitchChainDialog {
            val transaction = fragmentManager?.beginTransaction()
            val previous =
                fragmentManager?.findFragmentByTag("SwitchChainDialog")
            if (previous != null) {
                transaction?.remove(previous)
            }
            transaction?.addToBackStack(null)
            val dialogFragment = newInstance(
                currentChainId,
                newChainId,
                callbackId
            )
            transaction?.let {
                dialogFragment.show(
                    transaction!!,
                    "SwitchChainDialog"
                )
            }
            return dialogFragment
        }
    }
}