package com.mangala.wallet.ui.utils

import androidx.compose.runtime.MutableState

fun MutableState<Boolean>.toggle() {
    value = !value
}