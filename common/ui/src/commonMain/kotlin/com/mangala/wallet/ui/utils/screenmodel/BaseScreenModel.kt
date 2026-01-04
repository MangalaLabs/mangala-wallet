package com.mangala.wallet.ui.utils.screenmodel

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

abstract class BaseScreenModel: ScreenModel {

    protected lateinit var lifecycleScope: CoroutineScope
        private set

    private var isInitialized = false

    internal fun onComposableStarted() {
        if (!isInitialized) {
            lifecycleScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
            isInitialized = true
        }

        doOnComposableStarted()
    }

    open fun doOnComposableStarted() {

    }

    fun onComposableDisposed() {
        lifecycleScope.cancel()
    }
}