package com.mangala.eticket

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class ETicketSharedScreen: ScreenProvider {
    object ETicketHomeScreen: ETicketSharedScreen()
    object CategoryScreen: ETicketSharedScreen()
}