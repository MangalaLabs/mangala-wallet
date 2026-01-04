package com.mangala.features.browser.entities

import com.mangala.features.browser.entities.DAppCategory

data class DApp(
    val image: String,
    val title: String,
    val description: String,
    val link: String,
    val category: DAppCategory
)