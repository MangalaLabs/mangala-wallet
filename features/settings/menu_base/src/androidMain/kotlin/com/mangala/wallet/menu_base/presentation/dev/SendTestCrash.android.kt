package com.mangala.wallet.menu_base.presentation.dev

import co.touchlab.crashkios.crashlytics.CrashlyticsKotlin

actual fun sendTestCrash() {
    CrashlyticsKotlin.sendHandledException(Exception("CrashKiOS Test Exception"))
}