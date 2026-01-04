package com.mangala.wallet.core.ai.domain.model.process.preprocess

/**
 * Base implementation of the InputPreprocessor interface that handles
 * the chaining logic for the Chain of Responsibility pattern.
 */
abstract class BasePreprocessor : InputPreprocessor {
    private var nextPreprocessor: InputPreprocessor? = null

    override fun setNext(preprocessor: InputPreprocessor): InputPreprocessor {
        nextPreprocessor = preprocessor
        return preprocessor
    }

    override fun process(input: String): String {
        val processed = processImpl(input)
        return nextPreprocessor?.process(processed) ?: processed
    }

    /**
     * Concrete implementations should override this method to perform
     * their specific processing logic.
     *
     * @param input The input string to process
     * @return The processed string
     */
    protected abstract fun processImpl(input: String): String
}