## Plugin System

The AI module uses a comprehensive plugin system for extensibility. This system allows developers to add new functions, handlers, and UI renderers without modifying the core code.

## Plugin Architecture

The plugin system consists of three main components:

1. **Function Plugins**: Define what functions are available

   Function plugins register function definitions with the system:

    ```kotlin
    class WalletFunctions : FunctionPlugin {
        companion object {
            const val MODULE_ID = "wallet"
        }
    
        override fun registerTo(registry: FunctionRegistry) {
            registry.registerFunction(
                FunctionDefinition(
                    name = "send_transaction",
                    description = "Send cryptocurrency to another address",
                    parameters = mapOf(
                        "amount" to FunctionParameter(
                            type = ParameterType.NUMBER,
                            description = "Amount to send",
                            required = true
                        ),
                        "recipient" to FunctionParameter(
                            type = ParameterType.STRING,
                            description = "Recipient wallet address",
                            required = true
                        )
                    ),
                    requiredParameters = listOf("amount", "recipient"),
                    moduleId = MODULE_ID,
                    requiresConfirmation = true
                )
            )
        }
    }
    ```

2. **Handler Plugins**: Implement the execution logic for functions

   Handler plugins provide the execution logic for functions:

    ```kotlin
    object WalletFunctionHandlers : FunctionHandlerPlugin {
        override fun getFunctionHandlers(): List<FunctionHandler> {
            return listOf(
                SendTransactionFunctionHandler(),
                CheckBalanceFunctionHandler(),
                GetTransactionHistoryFunctionHandler()
            )
        }
        
        override fun registerTo(registry: FunctionHandlerRegistry) {
            getFunctionHandlers().forEach { handler ->
                registry.registerHandler(handler)
            }
        }
    }
    
    class SendTransactionFunctionHandler : FunctionHandler {
        override val functionName = "send_transaction"
        
        override suspend fun execute(parameters: Map<String, Any?>): FunctionResult {
            val amount = parameters["amount"] as? Double
            val recipient = parameters["recipient"] as? String
            
            return try {
                // Execute transaction logic
                val txHash = performTransaction(amount!!, recipient!!)
                FunctionResult.Success(
                    data = mapOf("transactionHash" to txHash),
                    message = "Transaction sent successfully"
                )
            } catch (e: Exception) {
                FunctionResult.Error("Transaction failed: ${e.message}")
            }
        }
    }
    
    ```

3. **Renderer Plugins**: Provide UI components for confirmation dialogs

   Renderer plugins provide UI components for function confirmations:

    ```kotlin
    class WalletFunctionConfirmationRendererPlugin : ConfirmationRendererPlugin {
        override fun getRenderers(): List<ConfirmationRenderer> {
            return listOf(
                SendTransactionConfirmationRenderer(),
                DeleteWalletConfirmationRenderer()
            )
        }
    }
    
    class SendTransactionConfirmationRenderer : ConfirmationRenderer {
        override val functionName = "send_transaction"
        
        @Composable
        override fun RenderConfirmation(
            message: FunctionCallConfirmationRequiredMessage,
            onConfirm: () -> Unit,
            onEdit: () -> Unit,
            onDeny: () -> Unit
        ) {
            val amount = message.functionCall.parameters["amount"]
            val recipient = message.functionCall.parameters["recipient"]
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Confirm Transaction", style = MaterialTheme.typography.headlineSmall)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Amount: $amount")
                    Text("To: $recipient")
                    Spacer(modifier = Modifier.height(16.dp))
                    Row {
                        Button(onClick = onConfirm) {
                            Text("Confirm")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = onEdit) {
                            Text("Edit")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = onDeny) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
    ```


## Registry System

The registry system manages all registered plugins:

## Plugin Registration

Plugins are registered through dependency injection:

```kotlin
val coreAiModule = module {
    // Register individual plugins
    single<FunctionPlugin>(named("WalletFunctions")) { 
        WalletFunctions() 
    }
    single<FunctionHandlerPlugin>(named("WalletHandlers")) { 
        WalletFunctionHandlers 
    }
    single<ConfirmationRendererPlugin>(named("WalletRenderers")) { 
        WalletFunctionConfirmationRendererPlugin() 
    }
    
    // Create registries with all plugins
    single<FunctionRegistry> {
        DefaultFunctionRegistry(getAll<FunctionPlugin>())
    }
    single<FunctionHandlerRegistry> {
        DefaultFunctionHandlerRegistry(getAll<FunctionHandlerPlugin>())
    }
    single<ConfirmationRendererRegistry> {
        DefaultConfirmationRendererRegistry(getAll<ConfirmationRendererPlugin>())
    }
}
```

## Creating a Complete Plugin Module

Here's an example of creating a complete plugin module for a new feature:

```kotlin
// 1. Define the functions
class PaymentFunctions : FunctionPlugin {
    override fun registerTo(registry: FunctionRegistry) {
        registry.registerFunction(
            FunctionDefinition(
                name = "request_payment",
                description = "Request payment from another user",
                parameters = mapOf(
                    "amount" to FunctionParameter(
                        type = ParameterType.NUMBER,
                        description = "Payment amount"
                    ),
                    "from" to FunctionParameter(
                        type = ParameterType.STRING,
                        description = "User to request from"
                    ),
                    "reason" to FunctionParameter(
                        type = ParameterType.STRING,
                        description = "Reason for payment",
                        required = false
                    )
                ),
                requiredParameters = listOf("amount", "from"),
                moduleId = "payments",
                requiresConfirmation = true
            )
        )
    }
}

// 2. Implement the handlers
class RequestPaymentHandler : FunctionHandler {
    override val functionName = "request_payment"
    
    override suspend fun execute(parameters: Map<String, Any?>): FunctionResult {
        val amount = parameters["amount"] as Double
        val from = parameters["from"] as String
        val reason = parameters["reason"] as? String
        
        return try {
            val requestId = createPaymentRequest(amount, from, reason)
            FunctionResult.Success(
                data = mapOf("requestId" to requestId),
                message = "Payment request sent"
            )
        } catch (e: Exception) {
            FunctionResult.Error(e.message ?: "Failed to create payment request")
        }
    }
}

// 3. Create the UI renderer (for functions that needs user confirmation before executing)
class RequestPaymentRenderer : ConfirmationRenderer {
    override val functionName = "request_payment"
    
    @Composable
    override fun RenderConfirmation(
        message: FunctionCallConfirmationRequiredMessage,
        onConfirm: () -> Unit,
        onEdit: () -> Unit,
        onDeny: () -> Unit
    ) {
        // Compose UI for payment request confirmation
        PaymentRequestConfirmationCard(
            amount = message.functionCall.parameters["amount"],
            from = message.functionCall.parameters["from"],
            reason = message.functionCall.parameters["reason"],
            onConfirm = onConfirm,
            onEdit = onEdit,
            onDeny = onDeny
        )
    }
}

// 4. Bundle everything in a plugin module
val paymentPluginModule = module {
    single<FunctionPlugin>(named("PaymentFunctions")) { 
        PaymentFunctions() 
    }
    single<FunctionHandlerPlugin>(named("PaymentHandlers")) { 
        object : FunctionHandlerPlugin {
            override fun getFunctionHandlers() = listOf(RequestPaymentHandler())
            override fun registerTo(registry: FunctionHandlerRegistry) {
                getFunctionHandlers().forEach { registry.registerHandler(it) }
            }
        }
    }
    single<ConfirmationRendererPlugin>(named("PaymentRenderers")) { 
        object : ConfirmationRendererPlugin {
            override fun getRenderers() = listOf(RequestPaymentRenderer())
        }
    }
}
```