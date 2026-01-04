package com.mangala.wallet.core.ai.domain.model.process.preprocess

import com.mangala.wallet.core.ai.domain.model.process.preprocess.preprocessors.CryptoTermPreprocessor
import com.mangala.wallet.core.ai.domain.model.process.preprocess.preprocessors.SentimentAnalysisPreprocessor
import com.mangala.wallet.core.ai.domain.model.process.preprocess.preprocessors.SlangPreprocessor
import com.mangala.wallet.core.ai.domain.model.process.preprocess.preprocessors.TextNormalizationPreprocessor

/**
 * Utility class for building and managing input preprocessor chains.
 */
object PreprocessorChain {
    /**
     * Creates a default preprocessing chain suitable for most user inputs.
     * The chain performs the following processing steps in order:
     * 1. Text normalization (trimming and lowercasing)
     * 2. Cryptocurrency term standardization
     * 3. Sentiment analysis for tagging urgency or negative emotions
     *
     * @return The first preprocessor in the chain
     */
    fun createDefaultChain(): InputPreprocessor {
        return TextNormalizationPreprocessor(toLowercase = false).apply {
            setNext(CryptoTermPreprocessor())
            .setNext(SentimentAnalysisPreprocessor())
        }
    }

    /**
     * Creates a custom preprocessing chain with the specified preprocessors.
     * Preprocessors are applied in the order they are provided.
     *
     * @param preprocessors The list of preprocessors to chain together
     * @return The first preprocessor in the chain, or null if the list is empty
     */
    fun createChain(preprocessors: List<InputPreprocessor>): InputPreprocessor? {
        if (preprocessors.isEmpty()) return null
        
        val first = preprocessors[0]
        var current = first
        
        for (i in 1 until preprocessors.size) {
            current = current.setNext(preprocessors[i])
        }
        
        return first
    }

    /**
     * Creates a custom preprocessing chain with the given slang dictionary.
     * The chain performs the following processing steps in order:
     * 1. Text normalization (trimming and lowercasing)
     * 2. Slang replacement using the provided dictionary
     * 3. Cryptocurrency term standardization
     *
     * @param slangDictionary A map of slang terms to their standard equivalents
     * @return The first preprocessor in the chain
     */
    fun createWithSlangPreprocessor(slangDictionary: Map<String, String>): InputPreprocessor {
        return TextNormalizationPreprocessor(toLowercase = false).apply {
            setNext(SlangPreprocessor(slangDictionary))
                .setNext(CryptoTermPreprocessor())
        }
    }
}