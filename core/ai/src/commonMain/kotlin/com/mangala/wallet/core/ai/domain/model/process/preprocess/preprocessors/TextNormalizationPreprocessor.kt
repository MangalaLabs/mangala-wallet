package com.mangala.wallet.core.ai.domain.model.process.preprocess.preprocessors

import com.mangala.wallet.core.ai.domain.model.process.preprocess.BasePreprocessor

class TextNormalizationPreprocessor(private val toLowercase: Boolean) : BasePreprocessor() {
    override fun processImpl(input: String): String {
        val trimmed = input.trim()
        return if (toLowercase) trimmed.lowercase() else trimmed
    }
}