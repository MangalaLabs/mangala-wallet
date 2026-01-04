# AI Provider Mapping Documentation

## Overview

This document details how different AI providers (Gemini, OpenAI, and Ollama) handle message mapping, function calling, and response processing in the Mangala Wallet application. Each provider has unique requirements for request/response formats and capabilities.

## System Prompt Handling

### Current Implementation
The system prompt is currently embedded in the client application (`SendMessageUseCase.kt`) and defines the AI assistant's behavior. The prompt includes:

1. **Role Definition**: Cryptocurrency wallet chatbot for contact management
2. **Workflow Instructions**: CREATE, EDIT, DELETE contact flows
3. **UI Tag Instructions**: When to include `[SELECT_NETWORK]` and `[ENTER_ADDRESS:networkname]` tags

### Provider-Specific System Message Handling

| Provider | System Message Support | Implementation Details |
|----------|----------------------|------------------------|
| **Gemini** | ⚠️ Partial | Maps "system" role to "user" role (line 93 in `GeminiRemoteDataSource.kt`) |
| **OpenAI** | ❌ No | System messages are filtered out during message mapping |
| **Ollama** | ✅ Full | Native support for system role messages |

This difference in system message handling requires special consideration when implementing the backend proxy to ensure consistent behavior across providers.

## Provider Comparison Matrix

| Feature | Gemini | OpenAI | Ollama |
|---------|---------|---------|---------|
| **Multimodal Support** | ✅ Full (text, images, inline data) | ✅ Full (text, images) | ❌ Text only |
| **Function Calling** | ✅ Native support | ✅ Native support | ❌ Not implemented |
| **Streaming** | ✅ Supported (commented) | ❌ Not implemented | ✅ Supported |
| **System Messages** | ⚠️ Mapped to user role | ❌ Not mapped | ✅ Native support |
| **Image Generation** | ✅ Via special model | ❌ Not supported | ❌ Not supported |
| **API Key Source** | BuildKonfig | BuildKonfig | Not required (local) |
| **UI Tag Support** | ✅ Via text parsing | ✅ Via text parsing | ✅ Via text parsing |

## Provider-Specific Implementations

### 1. Gemini Provider

**File**: `GeminiRemoteDataSource.kt`

#### Key Features
- **Model Selection**: 
  - Text: `GEMINI_2_0_FLASH`
  - Multimodal: `GEMINI_2_0_FLASH_PREVIEW_IMAGE_GENERATION`
- **System Message Handling**: Maps "system" role to "user" role (line 93)
- **Function Calling**: Full support with reasoning/explanatory text
- **Image Support**: Both file URIs and inline base64 data

#### Message Mapping
```kotlin
// System messages remapped to user
val role = if (message.role == "system") "user" else message.role

// Multipart content support
when (content) {
    is RemoteMessage.Content.Text -> Part(text = content.text)
    is RemoteMessage.Content.FileData -> Part(fileData = FileData(mimeType, fileUri))
    is RemoteMessage.Content.InlineData -> Part(inlineData = InlineData(mimeType, data))
}
```

#### Function Call Handling
- Supports mixed responses (text + function calls)
- Extracts explanatory text as "reasoning" for function calls
- Function results mapped to `functionResponse` with proper JSON structure
- Error handling for function execution failures

#### Response Processing
```kotlin
// Priority order:
1. Function calls (with reasoning text)
2. Text-only responses
3. Multimodal responses (images + text)
4. Empty response fallback
```

### 2. OpenAI Provider

**File**: `OpenAiRemoteDataSource.kt`

#### Key Features
- **Model Selection**:
  - Text: `GPT_4O_MINI`
  - Multimodal: `GPT_4O`
- **System Message Handling**: Not supported (filtered out)
- **Function Calling**: Native support with `callId` tracking
- **Image Support**: URLs and base64 inline data

#### Message Mapping
```kotlin
// Content types differ from Gemini
ContentItem types:
- "input_text" for user text
- "output_text" for assistant text
- "image_url" for file references
- "input_image" for inline base64 images

// Function calls have special message type
OpenAiChatCompletionRequest.Message(
    type = "function_call",
    arguments = jsonString,
    toolCallId = callId,
    name = functionName
)
```

#### Function Call Handling
- Uses `callId` for tracking function calls and results
- Function arguments serialized as JSON string
- Function results mapped with `toolCallId` correlation
- Different message types for function calls vs regular messages

#### Response Processing
```kotlin
// Checks output types in order:
1. Function call outputs (type = "function_call")
2. Message outputs (type = "message")
3. Defaults to empty text response
```

### 3. Ollama Provider

**File**: `OllamaRemoteDataSource.kt`

#### Key Features
- **Model**: `gemma3-tools:12b` (supports 27b variant)
- **System Message Handling**: Native support
- **Function Calling**: Not implemented (TODO)
- **Image Support**: Text-only, attachments mentioned in text

#### Message Mapping
```kotlin
// Simplified text-only approach
when (message) {
    is RemoteMessage.UserMessage -> {
        // Converts all content to text representation
        // File attachments mentioned as "[Attached files: ...]"
    }
    is RemoteMessage.FunctionCallMessage -> {
        // Simplified to "Function call: {name}"
    }
    is RemoteMessage.FunctionResultMessage -> {
        // Simplified to "Function result: {result}"
    }
}
```

#### Limitations
- No multimodal support - images/files converted to text descriptions
- Function calling not implemented (`prepareFunctionDefinitions` returns TODO)
- Streaming-only responses
- No structured function handling

## Common Patterns Across Providers

### 1. Interface Implementation
All providers implement `AiRemoteDataSource`:
```kotlin
interface AiRemoteDataSource {
    suspend fun processMessage(
        userId: String,
        conversationContext: List<RemoteMessage>,
        isMultiModalEnabled: Boolean
    ): Flow<AIResponse>
    
    fun prepareFunctionDefinitions(functions: List<FunctionDefinition>): Any
}
```

### 2. Message Type Hierarchy
```kotlin
sealed class RemoteMessage {
    UserMessage(contents: List<Content>)
    AssistantMessage(content: String)
    SystemMessage(content: String)
    FunctionCallMessage(name, parameters, callId)
    FunctionResultMessage(name, result, callId)
}
```

### 3. Response Types
```kotlin
sealed class AIResponse {
    TextResponse(text: String)
    ImageResponse(data: ByteArray, mimeType: String)
    FunctionCallResponse(request: FunctionCallRequest, reasoning: String?)
    MultiModalResponse(responses: List<AIResponse>)
}
```

## Provider Selection Logic

The `AIServiceFactory` selects providers based on configuration:
```kotlin
when (activeService) {
    AIServiceType.GEMINI -> GeminiRemoteDataSource(geminiApi, functionRegistry)
    AIServiceType.OPENAI -> OpenAiRemoteDataSource(openAiApi, functionRegistry)
    AIServiceType.OLLAMA -> OllamaRemoteDataSource(ollamaApi, functionRegistry)
    // ... other providers
}
```

## UI Tag System

### Current Implementation
The application uses a tag-based system where the LLM includes special tags at the end of its text responses to trigger UI components:

```kotlin
// UiTag.kt
sealed class UiTag(val tag: String) {
    data object SelectNetwork : UiTag("[SELECT_NETWORK]")
    data class EnterAddress(val networkName: String) : UiTag("[ENTER_ADDRESS:$networkName]")
}
```

### Tag Processing Flow
1. **LLM Response**: Includes tags like `[SELECT_NETWORK]` or `[ENTER_ADDRESS:Ethereum]` at the end of text
2. **Tag Parsing**: `TagParser` extracts tags using regex: `\[([A-Z_]+(?::[^\]]*)?)\]\s*$`
3. **UI Update**: Client updates input mode based on parsed tag
4. **Component Display**: Special UI components (network picker, address input) are shown

### Example Usage in System Prompt
```
"When you are asking the user to select the blockchain network, and that is the only thing 
you are asking for in that message, include the tag [SELECT_NETWORK] at the end of your response."
```

All providers support this tag system as it's handled at the text parsing level, independent of the AI provider.

## Migration Considerations for Backend Proxy

### 1. Unified Message Format
The backend proxy should:
- Accept a unified message format
- Handle provider-specific transformations internally
- Abstract away provider differences from the client

### 2. Function Calling Standardization
- Normalize function call formats across providers
- Handle providers without native function support (Ollama)
- Implement fallback strategies

### 3. Multimodal Handling
- Standardize image/file handling
- Support provider-specific limitations
- Handle format conversions (base64, URLs, file uploads)

### 4. Response Normalization
- Unified response format regardless of provider
- Consistent error handling
- Streaming support abstraction

### 5. Provider-Specific Features
- Gemini: Image generation capability
- OpenAI: Advanced function calling with IDs
- Ollama: Local model support

### 6. UI Tag Enhancement
The backend proxy should enhance the current tag system:
- Move from text-based tags to structured UI component responses
- Maintain backward compatibility with existing tag system
- Support more complex UI components beyond network/address selection
- Enable dynamic UI component configuration from backend

## Best Practices

1. **Error Handling**: Each provider should gracefully handle unsupported features
2. **Type Safety**: Use sealed classes for message and response types
3. **Null Safety**: Handle missing data in provider responses
4. **Performance**: Consider streaming for long responses
5. **Security**: API keys should be moved to backend (current limitation)

## Future Improvements

1. **Streaming Support**: Implement for all providers
2. **Function Calling**: Complete Ollama implementation
3. **Error Recovery**: Better fallback mechanisms
4. **Provider Switching**: Dynamic provider selection based on task
5. **Cost Optimization**: Route requests based on complexity/cost