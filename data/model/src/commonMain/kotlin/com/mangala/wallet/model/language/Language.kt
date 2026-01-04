package com.mangala.wallet.model.language

enum class Language(val code: String, val languageName: String, val flagUrl: String) {
    English("en", "English", ""),
    Spanish("es", "Spanish", ""),
    French("fr", "French", ""),
    German("de", "German", ""),
    Italian("it", "Italian", ""),
    Japanese("ja", "Japanese", ""),
    Korean("ko", "Korean", ""),
    Chinese("zh", "Chinese", ""),
    Vietnamese("vi", "Vietnamese", "");

    companion object {
        val DEFAULT_LANGUAGE = English
    }
}