package com.mangala.wallet.features.chains.antelope_base.domain.model.account

import dev.icerock.moko.parcelize.Parcelable
import dev.icerock.moko.parcelize.Parcelize

@Parcelize
data class AntelopeAccountByAuthorizer(
    val accountName: String,
    val permissionName: String,
    val authorizingKey: String,
    val weight: Int,
    val threshold: Int,
    val blockchainUid: String
): Parcelable