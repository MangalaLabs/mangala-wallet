package com.mangala.wallet.features.conversationui.domain.flow

import com.mangala.wallet.features.conversationui.presentation.FlowStepType
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

/**
 * Data class representing an execute local flow command
 */
data class ExecuteLocalFlowCommand(
    val data: FlowDefinition
)

/**
 * Data class representing a flow definition
 */
data class FlowDefinition(
    val flowId: String,
    val flowType: String,
    val steps: List<Map<String, Any>>,
    val events: Map<String, Any>? = null
)

/**
 * Data class representing a parsed flow
 */
data class ParsedFlow(
    val id: String,
    val type: String,
    val steps: List<FlowStep>,
    val events: Map<String, Any>? = null,
    val variables: MutableMap<String, Any> = mutableMapOf()
)

/**
 * Sealed class representing different types of flow steps
 */
sealed class FlowStep(
    val type: FlowStepType,
    val id: String,
    val description: String? = null
) {
    data class GetLocalDataStep(
        override val stepId: String,
        val dataType: String,
        val target: String,
        val params: Map<String, Any>? = null
    ) : FlowStep(FlowStepType.GET_LOCAL_DATA, stepId)

    data class ConditionalLogicStep(
        override val stepId: String,
        val condition: String,
        val ifTrue: String? = null,
        val ifFalse: String? = null,
        val elseSteps: List<String>? = null
    ) : FlowStep(FlowStepType.CONDITIONAL_LOGIC, stepId)

    data class FindContactLocalStep(
        override val stepId: String,
        val searchCriteria: Map<String, Any>,
        val allowMultiple: Boolean = false
    ) : FlowStep(FlowStepType.FIND_CONTACT_LOCAL, stepId)

    data class AddressInputStep(
        override val stepId: String,
        val placeholder: String? = null,
        val validation: Map<String, Any>? = null
    ) : FlowStep(FlowStepType.ADDRESS_INPUT, stepId)

    data class ShowLocalSelectorStep(
        override val stepId: String,
        val selectorType: String,
        val title: String? = null,
        val items: List<Map<String, Any>>? = null,
        val dataSource: String? = null
    ) : FlowStep(FlowStepType.SHOW_LOCAL_SELECTOR, stepId)

    data class MemoInputStep(
        override val stepId: String,
        val placeholder: String? = null,
        val maxLength: Int? = null,
        val required: Boolean = false
    ) : FlowStep(FlowStepType.MEMO_INPUT, stepId)

    data class CalculateFeesLocalStep(
        override val stepId: String,
        val network: String,
        val transactionType: String,
        val inputs: Map<String, Any>
    ) : FlowStep(FlowStepType.CALCULATE_FEES_LOCAL, stepId)

    data class TransactionReviewStep(
        override val stepId: String,
        val reviewData: Map<String, Any>
    ) : FlowStep(FlowStepType.TRANSACTION_REVIEW, stepId)

    data class SecurityAuthenticationStep(
        override val stepId: String,
        val authType: String,
        val reason: String? = null
    ) : FlowStep(FlowStepType.SECURITY_AUTHENTICATION, stepId)

    data class ExecuteBlockchainTransactionStep(
        override val stepId: String,
        val network: String,
        val transactionData: Map<String, Any>
    ) : FlowStep(FlowStepType.EXECUTE_BLOCKCHAIN_TRANSACTION, stepId)

    abstract val stepId: String
}

/**
 * Parser for flow definitions that converts JSON structures into strongly typed flow objects
 */
class FlowDefinitionParser {
    
    /**
     * Parse an ExecuteLocalFlow command into a ParsedFlow object
     */
    fun parseExecuteLocalFlow(command: ExecuteLocalFlowCommand): ParsedFlow {
        return ParsedFlow(
            id = command.data.flowId,
            type = command.data.flowType,
            steps = command.data.steps.map { parseStep(it) },
            events = command.data.events,
            variables = mutableMapOf()
        )
    }

    /**
     * Parse an ExecuteLocalFlow command from JSON
     */
    fun parseExecuteLocalFlowFromJson(jsonObject: JsonObject): ParsedFlow {
        val data = jsonObject["data"]?.jsonObject ?: throw IllegalArgumentException("Missing data field")
        
        val flowId = data["flowId"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing flowId")
        val flowType = data["flowType"]?.jsonPrimitive?.content ?: throw IllegalArgumentException("Missing flowType")
        
        val stepsJson = data["steps"]?.jsonArray ?: throw IllegalArgumentException("Missing steps")
        val steps = stepsJson.map { stepJson ->
            val stepMap = stepJson.jsonObject.mapValues { (_, value) -> jsonElementToAny(value) }
            parseStep(stepMap)
        }

        val eventsJson = data["events"]?.jsonObject
        val events = eventsJson?.mapValues { (_, value) -> jsonElementToAny(value) }

        return ParsedFlow(
            id = flowId,
            type = flowType,
            steps = steps,
            events = events,
            variables = mutableMapOf()
        )
    }
    
    /**
     * Parse a single flow step from a map
     */
    private fun parseStep(stepData: Map<String, Any>): FlowStep {
        val type = stepData["type"] as? String ?: throw IllegalArgumentException("Missing step type")
        val stepId = stepData["id"] as? String ?: throw IllegalArgumentException("Missing step id")
        
        return when (type) {
            "GET_LOCAL_DATA" -> FlowStep.GetLocalDataStep(
                stepId = stepId,
                dataType = stepData["dataType"] as? String ?: throw IllegalArgumentException("Missing dataType"),
                target = stepData["target"] as? String ?: throw IllegalArgumentException("Missing target"),
                params = stepData["params"] as? Map<String, Any>
            )
            
            "CONDITIONAL_LOGIC" -> FlowStep.ConditionalLogicStep(
                stepId = stepId,
                condition = stepData["condition"] as? String ?: throw IllegalArgumentException("Missing condition"),
                ifTrue = stepData["ifTrue"] as? String,
                ifFalse = stepData["ifFalse"] as? String,
                elseSteps = (stepData["elseSteps"] as? List<*>)?.mapNotNull { it as? String }
            )
            
            "FIND_CONTACT_LOCAL" -> FlowStep.FindContactLocalStep(
                stepId = stepId,
                searchCriteria = stepData["searchCriteria"] as? Map<String, Any> ?: emptyMap(),
                allowMultiple = stepData["allowMultiple"] as? Boolean ?: false
            )
            
            "ADDRESS_INPUT" -> FlowStep.AddressInputStep(
                stepId = stepId,
                placeholder = stepData["placeholder"] as? String,
                validation = stepData["validation"] as? Map<String, Any>
            )
            
            "SHOW_LOCAL_SELECTOR" -> FlowStep.ShowLocalSelectorStep(
                stepId = stepId,
                selectorType = stepData["selectorType"] as? String ?: throw IllegalArgumentException("Missing selectorType"),
                title = stepData["title"] as? String,
                items = (stepData["items"] as? List<*>)?.mapNotNull { it as? Map<String, Any> },
                dataSource = stepData["dataSource"] as? String
            )
            
            "MEMO_INPUT" -> FlowStep.MemoInputStep(
                stepId = stepId,
                placeholder = stepData["placeholder"] as? String,
                maxLength = stepData["maxLength"] as? Int,
                required = stepData["required"] as? Boolean ?: false
            )
            
            "CALCULATE_FEES_LOCAL" -> FlowStep.CalculateFeesLocalStep(
                stepId = stepId,
                network = stepData["network"] as? String ?: throw IllegalArgumentException("Missing network"),
                transactionType = stepData["transactionType"] as? String ?: throw IllegalArgumentException("Missing transactionType"),
                inputs = stepData["inputs"] as? Map<String, Any> ?: emptyMap()
            )
            
            "TRANSACTION_REVIEW" -> FlowStep.TransactionReviewStep(
                stepId = stepId,
                reviewData = stepData["reviewData"] as? Map<String, Any> ?: emptyMap()
            )
            
            "SECURITY_AUTHENTICATION" -> FlowStep.SecurityAuthenticationStep(
                stepId = stepId,
                authType = stepData["authType"] as? String ?: throw IllegalArgumentException("Missing authType"),
                reason = stepData["reason"] as? String
            )
            
            "EXECUTE_BLOCKCHAIN_TRANSACTION" -> FlowStep.ExecuteBlockchainTransactionStep(
                stepId = stepId,
                network = stepData["network"] as? String ?: throw IllegalArgumentException("Missing network"),
                transactionData = stepData["transactionData"] as? Map<String, Any> ?: emptyMap()
            )
            
            else -> throw IllegalArgumentException("Unknown step type: $type")
        }
    }

    /**
     * Convert JsonElement to Any for runtime use
     */
    private fun jsonElementToAny(element: JsonElement): Any {
        return when {
            element is kotlinx.serialization.json.JsonPrimitive && element.isString -> element.content
            element is kotlinx.serialization.json.JsonPrimitive -> {
                element.content.toBooleanStrictOrNull() 
                    ?: element.content.toIntOrNull() 
                    ?: element.content.toLongOrNull()
                    ?: element.content.toDoubleOrNull()
                    ?: element.content
            }
            element is JsonObject -> element.mapValues { jsonElementToAny(it.value) }
            element is kotlinx.serialization.json.JsonArray -> element.map { jsonElementToAny(it) }
            else -> element.toString()
        }
    }
}