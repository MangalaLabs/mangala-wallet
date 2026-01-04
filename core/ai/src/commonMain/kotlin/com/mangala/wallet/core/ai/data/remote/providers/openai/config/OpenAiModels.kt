package com.mangala.wallet.core.ai.data.remote.providers.openai.config

enum class OpenAiModels(val modelName: String, val supportsImages: Boolean = false) {
    GPT_4O("gpt-4o", supportsImages = true),
    GPT_4O_MINI("gpt-4o-mini", supportsImages = true),
    GPT_4_TURBO("gpt-4-turbo", supportsImages = true),
    GPT_4("gpt-4"),
    GPT_3_5_TURBO("gpt-3.5-turbo")
}