package com.mangala.wallet.core.ai.data.remote.providers.openai.mapping

import com.mangala.wallet.core.ai.data.remote.providers.openai.request.OpenAiChatCompletionRequest
import com.mangala.wallet.core.ai.domain.model.function.ParameterType
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionDefinition
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray

fun List<FunctionDefinition>.toOpenAiTools(): List<OpenAiChatCompletionRequest.Tool> {
    return this.map { function ->
        OpenAiChatCompletionRequest.Tool(
            type = "function",
            name = function.name,
            description = function.description,
            parameters = function.toOpenAiParameters()
        )
    }
}

fun FunctionDefinition.toOpenAiParameters(): JsonObject {
    return buildJsonObject {
        put("type", "object")
        put("properties", buildJsonObject {
            parameters.forEach { (name, schemaProperty) ->
                put(name, buildJsonObject {
                    put("type", schemaProperty.type.toOpenAiType())
                    put("description", schemaProperty.description)
                    
                    schemaProperty.enumValues?.let { enumValues ->
                        put("enum", buildJsonArray {
                            enumValues.forEach { add(it) }
                        })
                    }
                    
//                    schemaProperty.items?.let { itemSchema ->
//                        put("items", buildJsonObject {
//                            put("type", itemSchema.type.toOpenAiType())
//
//                            itemSchema.properties?.let { itemProperties ->
//                                put("properties", buildJsonObject {
//                                    itemProperties.forEach { (key, value) ->
//                                        put(key, buildJsonObject {
//                                            put("type", value.type.toOpenAiType())
//                                            put("description", value.description)
//                                        })
//                                    }
//                                })
//                            }
//                        })
//                    }
                })
            }
        })
        put("required", buildJsonArray {
            requiredParameters.forEach { add(it) }
        })
    }
}

fun ParameterType.toOpenAiType(): String {
    return when (this) {
        ParameterType.STRING -> "string"
        ParameterType.NUMBER -> "number"
        ParameterType.BOOLEAN -> "boolean"
        ParameterType.OBJECT -> "object"
        ParameterType.ARRAY -> "array"
        ParameterType.NULL -> "null"
    }
}