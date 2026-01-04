package com.mangala.wallet.model.currency


enum class Currency(
    val code: String,
    val symbol: String,
    val flagUrl: String
) {
    USD("USD", "$", ""),
    AED("AED", "د.إ", ""),
    ARS("ARS", "$", ""),
    AUD("AUD", "$", ""),
    BDT("BDT", "৳", ""),
    BHD("BHD", ".د.ب", ""),
    BMD("BMD", "$", ""),
    BRL("BRL", "R$", ""),
    CAD("CAD", "$", ""),
    CHF("CHF", "Fr.", ""),
    CLP("CLP", "$", ""),
    CNY("CNY", "¥", ""),
    CZK("CZK", "Kč", ""),
    DKK("DKK", "kr.", ""),
    EUR("EUR", "€", ""),
    GBP("GBP", "£", ""),
    HKD("HKD", "$", ""),
    HUF("HUF", "Ft", ""),
    IDR("IDR", "Rp", ""),
    ILS("ILS", "₪", ""),
    INR("INR", "₹", ""),
    JPY("JPY", "¥", ""),
    KRW("KRW", "₩", ""),
    KWD("KWD", "د.ك", ""),
    LKR("LKR", "Rs", ""),
    MMK("MMK", "K", ""),
    MXN("MXN", "$", ""),
    MYR("MYR", "RM", ""),
    NGN("NGN", "₦", ""),
    NOK("NOK", "kr", ""),
    NZD("NZD", "$", ""),
    PHP("PHP", "₱", ""),
    PKR("PKR", "Rs", ""),
    PLN("PLN", "zł", ""),
    RUB("RUB", "₽", ""),
    SAR("SAR", "ر.س", ""),
    SEK("SEK", "kr", ""),
    SGD("SGD", "$", ""),
    THB("THB", "฿", ""),
    TRY("TRY", "₺", ""),
    TWD("TWD", "NT$", ""),
    UAH("UAH", "₴", ""),
    VEF("VEF", "Bs.", ""),
    VND("VND", "₫", ""),
    ZAR("ZAR", "R", ""),
    BTC("BTC", "₿", "");

    companion object {
        val DEFAULT_CURRENCY = USD
    }
}
