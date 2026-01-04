package com.mangala.wallet.common.test.utils

import android.os.Debug

actual fun startCpuTrace(traceFileName: String) {
    Debug.startMethodTracing(traceFileName)
}

actual fun stopCpuTrace() {
    Debug.stopMethodTracing()
}