# Mangala Wallet AI & Conversation UI Documentation

## 1. Overview

The Mangala Wallet integrates advanced AI capabilities with a sophisticated conversation UI to provide an intelligent, chat-based interface for cryptocurrency wallet management. This documentation covers the technical architecture and implementation of both the `:core:ai` module and the `:features:conversationui` module.

### Key Features
- Multi-provider AI integration (OpenAI, Anthropic, Gemini, Ollama, Local, Mangala)
- Function calling system for wallet operations
- Chat-based contact management
- Network-aware address validation
- Persistent chat history
- Multimodal message support

### Architecture Overview
The system follows a clean architecture approach with clear separation between the AI core functionality and the conversation UI presentation layer. 

**Architecture Diagrams:**
- [AI Architecture Overview](ai-architecture.mermaid) - High-level system architecture
- [Class Diagram](conversationui-core-ai-class-diagram.mermaid) - Detailed class relationships between ConversationUI and Core:AI
- [Module Dependencies](module-dependency-diagram.mermaid) - Module structure and dependencies

## 2. Core:AI Module

### 2.1 Module Structure

```
core/ai/
├── src/
│   ├── commonMain/kotlin/com/mangala/wallet/core/ai/
│   │   ├── AiModule.kt                    # Dependency injection
│   │   ├── AiRepository.kt                # Main repository interface
│   │   ├── domain/
│   │   │   ├── model/                     # Domain models
│   │   │   ├── preprocessing/             # Text preprocessing
│   │   │   └── providers/                 # AI provider interfaces
│   │   ├── data/
│   │   │   ├── providers/                 # Provider implementations
│   │   │   └── repository/                # Repository implementation
│   │   └── functions/                     # Function calling system
│   ├── androidMain/                       # Android-specific code
│   ├── iosMain/                          # iOS-specific code
│   └── jvmMain/                          # Desktop-specific code
```

### 2.2 AI Providers

The module supports multiple AI providers through a unified interface:

#### Provider Interface
```kotlin
interface AiProvider {
    suspend fun generateResponse(
        messages: List<AiMessage>,
        stream: Boolean = false,
        functions: List<FunctionDefinition>? = null
    ): Flow<AiResponse>
}
```

#### Supported Providers
1. **OpenAI** - GPT models with function calling
2. **Anthropic** - Claude models  
3. **Gemini** - Google's AI models
4. **Ollama** - Local model execution
5. **Local** - On-device models
6. **Mangala** - Custom provider for wallet-specific operations

### 2.3 Function Calling System

The function calling system enables AI models to execute wallet operations safely. See [function-calling.mermaid](function-calling.mermaid) for the flow diagram.

#### Key Components

**FunctionDefinition**
```kotlin
data class FunctionDefinition(
    val name: String,
    val description: String,
    val parameters: JsonObject,
    val requiresConfirmation: Boolean = false
)
```

**FunctionHandler**
```kotlin
interface FunctionHandler {
    val definition: FunctionDefinition
    suspend fun execute(parameters: JsonObject): FunctionResult
}
```

**ConfirmationRenderer**
```kotlin
interface ConfirmationRenderer {
    @Composable
    fun render(
        functionCall: FunctionCall,
        onConfirm: () -> Unit,
        onCancel: () -> Unit
    )
}
```

#### Built-in Functions
- `GetContactsFunction` - Retrieve wallet contacts
- `CreateContactFunction` - Add new contact
- `UpdateContactFunction` - Modify existing contact
- `DeleteContactFunction` - Remove contact
- `ValidateAddressFunction` - Validate blockchain addresses

### 2.4 Message Processing Pipeline

1. **Input Processing**
   - Text normalization
   - Sentiment analysis
   - Crypto terminology processing
   - Slang processing

2. **AI Processing**
   - Provider selection
   - Message formatting
   - Function detection
   - Response generation

3. **Output Processing**
   - Response parsing
   - Function call extraction
   - UI tag processing
   - Error handling

### 2.5 Configuration System

```kotlin
interface FunctionConfigSource {
    suspend fun getFunctionConfigs(): List<FunctionConfig>
    fun startAutoRefresh(interval: Duration)
    fun stopAutoRefresh()
}
```

Supports both local (resource-based) and remote configuration sources with automatic refresh capabilities.

## 3. Features:ConversationUI Module

### 3.1 Module Structure

```
features/conversationui/
├── src/
│   ├── commonMain/kotlin/com/mangala/wallet/features/conversationui/
│   │   ├── ConversationUiModule.kt        # Dependency injection
│   │   ├── domain/
│   │   │   ├── model/                     # Domain models
│   │   │   ├── repository/                # Repository interfaces
│   │   │   ├── usecase/                   # Business logic
│   │   │   └── validation/                # Address validators
│   │   ├── data/
│   │   │   └── repository/                # Repository implementations
│   │   └── presentation/
│   │       ├── ConversationUiScreen.kt    # Main screen
│   │       ├── ConversationUiScreenModel.kt # ViewModel
│   │       └── components/                # UI components
│   ├── androidMain/                       # Android-specific UI
│   ├── iosMain/                          # iOS-specific UI
│   └── commonMain/sqldelight/            # Database schema
```

### 3.2 Architecture Pattern

The module follows MVVM architecture with:
- **Screen** - Composable UI definition
- **ScreenModel** - State management and business logic
- **Repository** - Data persistence
- **UseCase** - Business operations

See [conversationui-flow.mermaid](conversationui-flow.mermaid) for the interaction flow.

### 3.3 Core Components

#### ConversationUiScreenModel
Manages the conversation state and orchestrates interactions between UI and AI services.

```kotlin
class ConversationUiScreenModel : BaseScreenModel<ConversationUiScreenModel.State>() {
    data class State(
        val messages: List<ChatMessage> = emptyList(),
        val isLoading: Boolean = false,
        val selectedNetwork: Network? = null,
        val showNetworkPicker: Boolean = false,
        val showAddressInput: Boolean = false,
        val addressInputNetwork: String? = null
    )
}
```

#### Chat Components

1. **ChatBubble** - Renders individual messages
2. **ChatInputArea** - Message composition with attachments
3. **NetworkPicker** - Blockchain network selection
4. **AddressInputField** - Validated address entry
5. **TypingIndicator** - Shows AI processing state
6. **FunctionConfirmationDialog** - User approval for functions

### 3.4 UI Tag System

The conversation UI supports special tags that trigger specific UI components:

- `[SELECT_NETWORK]` - Opens network selection dialog
- `[ENTER_ADDRESS:networkname]` - Shows address input for specified network

Example usage:
```
Assistant: Please select a network for your contact:
[SELECT_NETWORK]

Once selected, enter the address:
[ENTER_ADDRESS:ethereum]
```

### 3.5 Address Validation

The module includes a comprehensive address validation system:

```kotlin
interface AddressValidator {
    fun validate(address: String): ValidationResult
    val supportedNetworks: List<String>
}
```

Validators are registered in a registry pattern allowing easy extension:

```kotlin
class AddressValidatorRegistry {
    fun register(validator: AddressValidator)
    fun getValidator(network: String): AddressValidator?
}
```

### 3.6 Chat Persistence

Chat history is persisted using SQLDelight:

```sql
CREATE TABLE ChatSession (
    id TEXT PRIMARY KEY,
    userId TEXT NOT NULL,
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL
);

CREATE TABLE ChatMessage (
    id TEXT PRIMARY KEY,
    sessionId TEXT NOT NULL,
    role TEXT NOT NULL,
    content TEXT NOT NULL,
    timestamp INTEGER NOT NULL,
    metadata TEXT,
    FOREIGN KEY (sessionId) REFERENCES ChatSession(id)
);
```

### 3.7 Contact Management Flow

The conversation UI is optimized for contact management with guided flows:

1. **Create Contact Flow**
   - AI prompts for contact name
   - Network selection via UI picker
   - Address input with validation
   - Confirmation and creation

2. **Edit Contact Flow**
   - Contact selection
   - Field modification
   - Validation and update

3. **Delete Contact Flow**
   - Contact identification
   - Confirmation dialog
   - Deletion execution

## 4. Integration Guidelines

### 4.1 Module Dependencies

```kotlin
// In your feature module's build.gradle.kts
dependencies {
    implementation(projects.core.ai)
    implementation(projects.features.conversationui)
}
```

### 4.2 Koin Configuration

```kotlin
// In your app module
startKoin {
    modules(
        aiModule,
        conversationUiModule,
        // your other modules
    )
}
```

### 4.3 Navigation Setup

```kotlin
// Using Voyager navigation
Navigator(ConversationUiScreen())
```

### 4.4 API Key Configuration

Configure API keys in BuildKonfig:

```kotlin
buildkonfig {
    packageName = "com.mangala.wallet"
    
    defaultConfigs {
        buildConfigField(STRING, "OPENAI_API_KEY", getEnvOrProperty("OPENAI_API_KEY", ""))
        buildConfigField(STRING, "ANTHROPIC_API_KEY", getEnvOrProperty("ANTHROPIC_API_KEY", ""))
        // Other API keys
    }
}
```

## 5. Extending the System

### 5.1 Adding New AI Providers

1. Implement the `AiProvider` interface
2. Add provider to `AiProviderFactory`
3. Configure in `AiModule`

### 5.2 Creating Custom Functions

1. Implement `FunctionHandler`
2. Create `ConfirmationRenderer` if needed
3. Register in `FunctionRegistry`

### 5.3 Adding Address Validators

1. Implement `AddressValidator`
2. Register in `AddressValidatorRegistry`
3. Update UI to support new network

## 6. Best Practices

### 6.1 Security
- Always require confirmation for sensitive operations
- Validate all user inputs
- Sanitize AI responses before display
- Don't expose API keys in code

### 6.2 Performance
- Use streaming for long responses
- Implement proper error boundaries
- Cache AI responses when appropriate
- Lazy load heavy components

### 6.3 User Experience
- Provide clear loading states
- Show helpful error messages
- Guide users through complex flows
- Maintain conversation context

## 7. Troubleshooting

### Common Issues

1. **API Key Errors**
   - Verify keys in BuildKonfig
   - Check environment variables
   - Ensure proper key format

2. **Function Call Failures**
   - Check function parameter validation
   - Verify handler implementation
   - Review confirmation flow

3. **UI Tag Not Working**
   - Ensure tag format is correct
   - Check ScreenModel tag handling
   - Verify component registration

## 8. References

### Architecture Diagrams
- [AI Architecture Overview](ai-architecture.mermaid) - High-level system architecture
- [Class Diagram](conversationui-core-ai-class-diagram.mermaid) - Detailed class relationships
- [Module Dependencies](module-dependency-diagram.mermaid) - Module structure and dependencies
- [Conversation UI Flow](conversationui-flow.mermaid) - User interaction flow
- [Function Calling System](function-calling.mermaid) - Function execution flow
- [Function Registry Architecture](function-registry-architecture.mermaid) - Registry pattern details
- [Function Registry Class Diagram](function-registry-class-diagram.mermaid) - Registry classes
- [Function Registry Sequence](function-registry-sequence.mermaid) - Execution sequence
- [Function Registry State](function-registry-state.mermaid) - State management

### Documentation
- [Function Plugins Guide](function-plugins.md) - Creating and registering functions
- [Mangala Wallet Main Documentation](../../../README.md) - Project overview