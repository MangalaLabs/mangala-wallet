package com.mangala.wallet.pin.presentation.lock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.TextDescription1
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class LockScreen : Screen {

    @Composable
    override fun Content() {
        LifecycleEffect(onStarted = {
            MangalaAnalytics.trackScreenView(
                MangalaAnalytics.Screens.LOCK,
                LockScreen::class.simpleName.orEmpty()
            )
        })

        Column(
            modifier = Modifier.fillMaxSize()
                .background(color = MaterialTheme.colors.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            //Set Text center screen
            val messageLock = MR.strings.os_screen_lock.desc().localized()
            TextDescription1(text = messageLock, modifier = Modifier.padding(Spacing.SMALL))
        }
    }
}