package com.mangala.wallet.features.home.presentation

import kotlinx.serialization.Serializable

class EosAccountNoticeModel (
    val title: String,
    val body: EosAccountNoticeBody
)

@Serializable
class EosAccountNoticeBody (
    val accountName: String,
    val chainId: String
)