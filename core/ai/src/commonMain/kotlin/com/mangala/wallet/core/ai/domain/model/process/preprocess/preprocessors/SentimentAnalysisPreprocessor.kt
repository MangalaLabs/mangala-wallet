package com.mangala.wallet.core.ai.domain.model.process.preprocess.preprocessors

import com.mangala.wallet.core.ai.domain.model.process.preprocess.BasePreprocessor

/**
 * Preprocessor that tags the input with a basic sentiment analysis.
 * This can be used to detect user frustration or urgency.
 */
class SentimentAnalysisPreprocessor : BasePreprocessor() {
    private val negativeTerms = listOf("can't", "not working", "error", "failed", "problem", "issue", "bug", "wrong", "doesn't work", "broken")
    private val urgentTerms = listOf("asap", "urgent", "immediately", "emergency", "critical", "now", "right away", "fast")
    
    override fun processImpl(input: String): String {
        val lowercaseInput = input.lowercase()
        
        val sentimentTags = mutableListOf<String>()
        
        // Check for negative sentiment
        if (negativeTerms.any { lowercaseInput.contains(it) }) {
            sentimentTags.add("[NEGATIVE]")
        }
        
        // Check for urgency
        if (urgentTerms.any { lowercaseInput.contains(it) }) {
            sentimentTags.add("[URGENT]")
        }
        
        return if (sentimentTags.isEmpty()) {
            input
        } else {
            "${sentimentTags.joinToString(" ")} $input"
        }
    }
}