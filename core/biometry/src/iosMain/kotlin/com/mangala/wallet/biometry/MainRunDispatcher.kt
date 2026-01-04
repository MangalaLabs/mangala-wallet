package com.mangala.wallet.biometry

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import platform.Foundation.NSRunLoop
import platform.Foundation.performBlock
import kotlin.coroutines.CoroutineContext

/**
 * Simple object made to ensure dispatching to the main looper on iOS
 */
internal object MainRunDispatcher : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) =
        NSRunLoop.mainRunLoop.performBlock { block.run() }
}
