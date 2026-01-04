package com.mangala.wallet.core.ai.data.remote.providers.gemini.config

enum class GeminiModels(
    val modelName: String,
    val supportedModalities: List<GeminiModalities>?
) {
    GEMINI_2_0_FLASH(
        modelName = "gemini-2.0-flash",
        supportedModalities = null
    ),
    GEMINI_2_0_FLASH_PREVIEW_IMAGE_GENERATION(
        modelName = "gemini-2.0-flash-preview-image-generation",
        supportedModalities = listOf(GeminiModalities.TEXT, GeminiModalities.IMAGE)
    )
}