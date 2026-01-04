package com.mangala.wallet.core.ai.data.remote

/**
 * Configuration for an AI service
 *
 * @property type The type of AI service
 * @property apiKey API key for the service (for OPENAI and ANTHROPIC)
 * @property apiUrl Base URL for the API (for OPENAI and ANTHROPIC)
 * @property modelName The model name to use (e.g., "gpt-4", "claude-3-opus")
 * @property modelPath Path to the model file (for LOCAL)
 */
data class AIServiceConfig(
    val type: AIServiceType,
    val apiKey: String = "",
    val modelName: String = "",
    val modelPath: String = ""
) {
    companion object {
        /**
         * Creates a config for OpenAI
         */
        fun openAI(apiKey: String, modelName: String, apiUrl: String = "https://api.openai.com/v1"): AIServiceConfig {
            return AIServiceConfig(
                type = AIServiceType.OPENAI,
                apiKey = apiKey,
                modelName = modelName
            )
        }

        /**
         * Creates a config for Anthropic
         */
        fun anthropic(apiKey: String, modelName: String, apiUrl: String = "https://api.anthropic.com"): AIServiceConfig {
            return AIServiceConfig(
                type = AIServiceType.ANTHROPIC,
                apiKey = apiKey,
                modelName = modelName
            )
        }

        /**
         * Creates a config for a local model
         */
        fun local(modelPath: String, modelName: String = ""): AIServiceConfig {
            return AIServiceConfig(
                type = AIServiceType.LOCAL,
                modelPath = modelPath,
                modelName = modelName
            )
        }
        
        /**
         * Creates a config for Mangala
         */
        fun mangala(): AIServiceConfig {
            return AIServiceConfig(
                type = AIServiceType.MANGALA
            )
        }
    }
}