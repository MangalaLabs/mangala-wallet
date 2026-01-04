package com.mangala.wallet.core.ai.domain.model.process.preprocess

/**
 * Interface for input preprocessors that can be chained together
 * using the Chain of Responsibility pattern.
 */
interface InputPreprocessor {
    /**
     * Process the input string and return the processed result
     * 
     * @param input The input string to process
     * @return The processed string
     */
    fun process(input: String): String
    
    /**
     * Set the next preprocessor in the chain
     * 
     * @param preprocessor The next preprocessor to handle the input after this one
     * @return The next preprocessor, enabling fluent method chaining
     */
    fun setNext(preprocessor: InputPreprocessor): InputPreprocessor
}