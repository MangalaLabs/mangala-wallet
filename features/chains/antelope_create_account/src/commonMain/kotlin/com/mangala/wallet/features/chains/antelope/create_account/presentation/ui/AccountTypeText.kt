package com.mangala.wallet.features.chains.antelope.create_account.presentation.ui

import androidx.compose.runtime.Composable
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.mokoresources.MR
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun getAccountTypeText(accountType: AccountNameType): String {
    val accountTypeText = when (accountType) {
        AccountNameType.Standard -> MR.strings.all_antelope_standard_account_name.desc()
            .localized()

        AccountNameType.Premium -> MR.strings.all_antelope_premium_account_name.desc()
            .localized()

        else -> ""
    }
    return accountTypeText
}