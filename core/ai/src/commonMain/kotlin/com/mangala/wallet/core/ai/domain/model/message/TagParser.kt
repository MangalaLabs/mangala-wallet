package com.mangala.wallet.core.ai.domain.model.message

object TagParser {

    // Updated regex to support both simple tags and parameterized tags, with optional whitespace/newlines at the end
    private val TAG_REGEX = """\[([A-Z_]+(?::[^\]]*)?)\]\s*$""".toRegex()

    data class ParseResult(
        val cleanedText: String,
        val uiTag: UiTag?
    )

    fun parseMessage(text: String): ParseResult {
        // Trim the text first to handle trailing newlines and whitespace
        val trimmedText = text.trim()
        val matchResult = TAG_REGEX.find(trimmedText)

        return if (matchResult != null) {
            val fullTag = "[${matchResult.groupValues[1]}]"
            val cleanedText = trimmedText.substring(0, matchResult.range.first).trim()
            val uiTag = UiTag.fromString(fullTag)

            ParseResult(cleanedText, uiTag)
        } else {
            ParseResult(text, null)
        }
    }
}