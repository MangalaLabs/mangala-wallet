package com.mangala.wallet.core.ai.domain.model.process.preprocess.preprocessors

import com.mangala.wallet.core.ai.domain.model.process.preprocess.BasePreprocessor

/**
 * Preprocessor that replaces slang terms with standard language using a provided dictionary.
 */
class SlangPreprocessor(private val slangDictionary: Map<String, String>) : BasePreprocessor() {
    override fun processImpl(input: String): String {
        var result = input
        slangDictionary.forEach { (slang, standard) ->
            // Use word boundary regex to replace whole words only
            val regex = "\\b$slang\\b".toRegex(RegexOption.IGNORE_CASE)
            result = result.replace(regex, standard)
        }
        return result
    }
}