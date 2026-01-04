package com.mangala.wallet.features.conversationui.domain.flow

import com.mangala.wallet.features.conversationui.presentation.TokenInfo
import com.mangala.wallet.features.conversationui.presentation.WalletContext
import com.mangala.wallet.features.conversationui.presentation.TransactionFlowData
import com.mangala.wallet.features.addressbook.data.model.ContactModel

/**
 * Engine for variable substitution in flow templates
 * Handles complex expressions like {context.availableTokens.find(t => t.symbol === 'EOS')}
 */
class VariableSubstitution {
    
    /**
     * Substitute variables in a template string
     */
    fun substitute(template: String, variables: Map<String, Any>): String {
        // Handle complex expressions like {context.availableTokens.find(t => t.symbol === 'EOS')}
        val regex = Regex("\\{([^}]+)\\}")
        
        return regex.replace(template) { matchResult ->
            val expression = matchResult.groupValues[1]
            try {
                evaluateExpression(expression, variables)
            } catch (e: Exception) {
                // If evaluation fails, return the original expression wrapped in braces
                "{$expression}"
            }
        }
    }
    
    /**
     * Evaluate a variable expression
     */
    private fun evaluateExpression(expression: String, variables: Map<String, Any>): String {
        val trimmedExpression = expression.trim()
        
        // Handle simple property access: context.currentNetwork
        if (trimmedExpression.contains('.') && !trimmedExpression.contains('(')) {
            return evaluatePropertyAccess(trimmedExpression, variables)
        }
        
        // Handle array operations: availableTokens.find(...)
        if (trimmedExpression.contains(".find(")) {
            return evaluateArrayFind(trimmedExpression, variables)
        }
        
        // Handle array operations: availableTokens.filter(...)
        if (trimmedExpression.contains(".filter(")) {
            return evaluateArrayFilter(trimmedExpression, variables)
        }
        
        // Handle calculations: sendAmount + calculatedFees.networkFee
        if (trimmedExpression.contains('+') || trimmedExpression.contains('-') || 
            trimmedExpression.contains('*') || trimmedExpression.contains('/')) {
            return evaluateCalculation(trimmedExpression, variables)
        }
        
        // Handle simple variable access
        return evaluateSimpleVariable(trimmedExpression, variables)
    }
    
    /**
     * Evaluate simple property access like context.currentNetwork.name
     */
    private fun evaluatePropertyAccess(expression: String, variables: Map<String, Any>): String {
        val parts = expression.split('.')
        var current: Any? = variables[parts[0]]
        
        for (i in 1 until parts.size) {
            current = when (current) {
                is WalletContext -> getWalletContextProperty(current, parts[i])
                is TokenInfo -> getTokenInfoProperty(current, parts[i])
                is ContactModel -> getContactProperty(current, parts[i])
                is TransactionFlowData -> getTransactionFlowDataProperty(current, parts[i])
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    (current as? Map<String, Any?>)?.get(parts[i])
                }
                else -> null
            }
            
            if (current == null) break
        }
        
        return current?.toString() ?: ""
    }
    
    /**
     * Evaluate array find operations like availableTokens.find(t => t.symbol === 'EOS')
     */
    private fun evaluateArrayFind(expression: String, variables: Map<String, Any>): String {
        // Parse expression like "context.availableTokens.find(t => t.symbol === 'EOS')"
        val findPattern = Regex("([^.]+)(?:\\.([^.]+))?\\.find\\(([^)]+)\\)")
        val match = findPattern.find(expression) ?: return ""
        
        val (rootVar, property, condition) = match.destructured
        
        // Get the array to search
        val array = if (property.isNotEmpty()) {
            val root = variables[rootVar]
            getProperty(root, property) as? List<*>
        } else {
            variables[rootVar] as? List<*>
        } ?: return ""
        
        // Parse condition like "t => t.symbol === 'EOS'"
        val conditionPattern = Regex("(\\w+)\\s*=>\\s*(.+)")
        val conditionMatch = conditionPattern.find(condition) ?: return ""
        val (paramName, conditionExpr) = conditionMatch.destructured
        
        // Find matching item
        val found = array.find { item ->
            evaluateCondition(conditionExpr, mapOf(paramName to item))
        }
        
        return found?.toString() ?: ""
    }
    
    /**
     * Evaluate array filter operations like contacts.filter(c => c.addresses.length > 0)
     */
    private fun evaluateArrayFilter(expression: String, variables: Map<String, Any>): String {
        // Similar to find but returns a list
        val filterPattern = Regex("([^.]+)(?:\\.([^.]+))?\\.filter\\(([^)]+)\\)")
        val match = filterPattern.find(expression) ?: return ""
        
        val (rootVar, property, condition) = match.destructured
        
        // Get the array to filter
        val array = if (property.isNotEmpty()) {
            val root = variables[rootVar]
            getProperty(root, property) as? List<*>
        } else {
            variables[rootVar] as? List<*>
        } ?: return ""
        
        // Parse condition
        val conditionPattern = Regex("(\\w+)\\s*=>\\s*(.+)")
        val conditionMatch = conditionPattern.find(condition) ?: return ""
        val (paramName, conditionExpr) = conditionMatch.destructured
        
        // Filter items
        val filtered = array.filter { item ->
            evaluateCondition(conditionExpr, mapOf(paramName to item))
        }
        
        return filtered.size.toString() // Return count for now
    }
    
    /**
     * Evaluate calculations like sendAmount + calculatedFees.networkFee
     */
    private fun evaluateCalculation(expression: String, variables: Map<String, Any>): String {
        // Simple calculation parser - handle basic arithmetic
        val operators = listOf("+", "-", "*", "/")
        
        for (op in operators) {
            if (expression.contains(op)) {
                val parts = expression.split(op).map { it.trim() }
                if (parts.size == 2) {
                    val left = evaluateExpression(parts[0], variables).toDoubleOrNull() ?: 0.0
                    val right = evaluateExpression(parts[1], variables).toDoubleOrNull() ?: 0.0
                    
                    val result = when (op) {
                        "+" -> left + right
                        "-" -> left - right
                        "*" -> left * right
                        "/" -> if (right != 0.0) left / right else 0.0
                        else -> 0.0
                    }
                    
                    return result.toString()
                }
            }
        }
        
        return "0"
    }
    
    /**
     * Evaluate simple variable access
     */
    private fun evaluateSimpleVariable(expression: String, variables: Map<String, Any>): String {
        return variables[expression]?.toString() ?: ""
    }
    
    /**
     * Evaluate conditions for array operations
     */
    private fun evaluateCondition(condition: String, variables: Map<String, Any?>): Boolean {
        // Handle equality: t.symbol === 'EOS'
        if (condition.contains("===")) {
            val parts = condition.split("===").map { it.trim() }
            if (parts.size == 2) {
                val left = evaluateExpressionWithNullables(parts[0], variables)
                val right = parts[1].removeSurrounding("'", "'").removeSurrounding("\"", "\"")
                return left == right
            }
        }
        
        // Handle inequality: t.balance > 0
        if (condition.contains(">")) {
            val parts = condition.split(">").map { it.trim() }
            if (parts.size == 2) {
                val left = evaluateExpressionWithNullables(parts[0], variables).toDoubleOrNull() ?: 0.0
                val right = parts[1].toDoubleOrNull() ?: 0.0
                return left > right
            }
        }
        
        // Handle property existence: t.addresses.length > 0
        if (condition.contains(".length")) {
            val propertyPath = condition.substringBefore(".length")
            val value = evaluateExpressionWithNullables(propertyPath, variables)
            // Assume it's checking if a list/array has items
            return value.isNotEmpty()
        }
        
        return false
    }
    
    /**
     * Evaluate expression with nullable values in variable map
     */
    private fun evaluateExpressionWithNullables(expression: String, variables: Map<String, Any?>): String {
        val trimmedExpression = expression.trim()
        
        // Handle simple property access: t.symbol
        if (trimmedExpression.contains('.') && !trimmedExpression.contains('(')) {
            val parts = trimmedExpression.split('.')
            var current: Any? = variables[parts[0]]
            
            for (i in 1 until parts.size) {
                current = when (current) {
                    is WalletContext -> getWalletContextProperty(current, parts[i])
                    is TokenInfo -> getTokenInfoProperty(current, parts[i])
                    is ContactModel -> getContactProperty(current, parts[i])
                    is TransactionFlowData -> getTransactionFlowDataProperty(current, parts[i])
                    is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    (current as? Map<String, Any?>)?.get(parts[i])
                }
                    else -> null
                }
                
                if (current == null) break
            }
            
            return current?.toString() ?: ""
        }
        
        // Handle simple variable access
        return variables[trimmedExpression]?.toString() ?: ""
    }
    
    /**
     * Get property from WalletContext
     */
    private fun getWalletContextProperty(context: WalletContext, property: String): Any? {
        return when (property) {
            "currentNetwork" -> context.currentNetwork
            "availableTokens" -> context.availableTokens
            "walletsOnCurrentNetwork" -> context.walletsOnCurrentNetwork
            "contacts" -> context.contacts
            "recentTransactions" -> context.recentTransactions
            else -> null
        }
    }
    
    /**
     * Get property from TokenInfo
     */
    private fun getTokenInfoProperty(token: TokenInfo, property: String): Any? {
        return when (property) {
            "symbol" -> token.symbol
            "contractAddress" -> token.contractAddress
            "decimals" -> token.decimals
            "balance" -> token.balance
            "balanceUSD" -> token.balanceUSD
            else -> null
        }
    }
    
    /**
     * Get property from ContactModel
     */
    private fun getContactProperty(contact: ContactModel, property: String): Any? {
        return when (property) {
            "id" -> contact.contactId
            "name" -> contact.contactName
            "nickname" -> contact.walletAlias
            "addresses" -> listOf(contact.walletAddress) // Convert single address to list
            "walletAddress" -> contact.walletAddress
            "walletAddressId" -> contact.walletAddressId
            "walletAlias" -> contact.walletAlias
            "blockchainName" -> contact.blockchainName
            "blockchainSymbol" -> contact.blockchainSymbol
            "isFavorite" -> contact.isFavorite
            else -> null
        }
    }
    
    /**
     * Get property from TransactionFlowData
     */
    private fun getTransactionFlowDataProperty(data: TransactionFlowData, property: String): Any? {
        return when (property) {
            "selectedToken" -> data.selectedToken
            "sendAmount" -> data.sendAmount
            "recipient" -> data.recipient
            "recipientAddress" -> data.recipientAddress
            "selectedContact" -> data.selectedContact
            "memo" -> data.memo
            "calculatedFees" -> data.calculatedFees
            "context" -> data.context
            else -> null
        }
    }
    
    /**
     * Generic property getter
     */
    private fun getProperty(obj: Any?, property: String): Any? {
        return when (obj) {
            is WalletContext -> getWalletContextProperty(obj, property)
            is TokenInfo -> getTokenInfoProperty(obj, property)
            is ContactModel -> getContactProperty(obj, property)
            is TransactionFlowData -> getTransactionFlowDataProperty(obj, property)
            is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                (obj as? Map<String, Any>)?.get(property)
            }
            else -> null
        }
    }
}