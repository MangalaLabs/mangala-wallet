package com.mangala.wallet.model.account.domain.eos

enum class AccountNameType {
    Standard, Premium, Friend, None;

    companion object {
        fun getAccountNameType(accountName: String): AccountNameType {
            if (accountName.isBlank() || accountName.length > 12) return None

            if (!accountName.matches(Regex("^[a-z1-5.]*\$"))) return None

            val isPremiumAccount =
                accountName.contains(".") && !accountName.endsWith(".") && accountName.length <= 12
            if (isPremiumAccount) return Premium

            return Standard
        }
    }
}