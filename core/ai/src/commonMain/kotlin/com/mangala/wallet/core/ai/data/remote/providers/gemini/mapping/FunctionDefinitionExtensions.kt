package com.mangala.wallet.core.ai.data.remote.providers.gemini.mapping

import com.mangala.wallet.core.ai.data.remote.providers.gemini.request.GeminiGenerateContentRequest
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionDefinition
import com.mangala.wallet.core.ai.domain.model.function.FunctionParameter
import com.mangala.wallet.core.ai.domain.model.function.ParameterType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

fun FunctionDefinition.toGeminiFunctionDeclaration(): GeminiGenerateContentRequest.Tool.FunctionDeclaration {
    return GeminiGenerateContentRequest.Tool.FunctionDeclaration(
        name = this.name,
        description = this.description,
        parameters = GeminiGenerateContentRequest.Tool.FunctionDeclaration.Parameters(
            type = "object",
            properties = this.parameters.toGeminiProperties(),
            required = this.requiredParameters
        )
    )
}

fun List<FunctionDefinition>.toGeminiTool(): GeminiGenerateContentRequest.Tool {
    val functionDeclarations = this.map { it.toGeminiFunctionDeclaration() }
    return GeminiGenerateContentRequest.Tool(
        functionDeclarations = functionDeclarations
    )
}

private fun Map<String, FunctionParameter>.toGeminiProperties(): JsonObject {
    return buildJsonObject {
        this@toGeminiProperties.forEach { (name, parameter) ->
            put(name, parameter.toJsonObject())
        }
    }
}

private fun FunctionParameter.toJsonObject(): JsonObject {
    return buildJsonObject {
        put("type", JsonPrimitive(this@toJsonObject.type.toGeminiType()))
        put("description", JsonPrimitive(this@toJsonObject.description))
        
        if (!this@toJsonObject.enumValues.isNullOrEmpty()) {
            put("enum", buildJsonArray {
                this@toJsonObject.enumValues.forEach { value ->
                    add(JsonPrimitive(value))
                }
            })
        }
        
        if (this@toJsonObject.type == ParameterType.ARRAY) {
            put("items", buildJsonObject {
                put("type", JsonPrimitive("string"))
            })
        }
        
        if (this@toJsonObject.type == ParameterType.OBJECT && this@toJsonObject.properties != null) {
            put("properties", buildJsonObject {
                this@toJsonObject.properties.forEach { (propName, propValue) ->
                    put(propName, propValue.toJsonObject())
                }
            })
        }
    }
}

private fun ParameterType.toGeminiType(): String {
    return when (this) {
        ParameterType.STRING -> "string"
        ParameterType.NUMBER -> "number"
        ParameterType.BOOLEAN -> "boolean"
        ParameterType.OBJECT -> "object"
        ParameterType.ARRAY -> "array"
        ParameterType.NULL -> "null"
    }
}