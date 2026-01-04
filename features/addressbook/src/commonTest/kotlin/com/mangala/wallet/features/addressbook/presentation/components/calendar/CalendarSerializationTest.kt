package com.mangala.wallet.features.addressbook.presentation.components.calendar

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CalendarSerializationTest {
    
    @Test
    fun `calendar screen serialization size should be small`() {
        val screen = CalendarBottomSheetScreen(
            screenId = "test_12345",
            existingDateId = "date_001",
            existingDateTitle = "Birthday Celebration Event",
            existingDateDay = 15,
            existingDateMonth = 8,
            existingDateYear = 2024
        )
        
        val json = Json.encodeToString(screen)
        val sizeInBytes = json.toByteArray().size
        
        println("Serialized JSON: $json")
        println("Size: $sizeInBytes bytes")
        
        // Assert size is reasonable (< 1KB for this simple object)
        assertTrue(sizeInBytes < 1024, "Screen serialization too large: $sizeInBytes bytes")
    }
    
    @Test
    fun `compare with ImportantDate object size`() {
        // This would fail because ImportantDate is not serializable with Java serialization
        // But we can check Kotlin serialization size
        
        val screenWithPrimitives = CalendarBottomSheetScreen(
            screenId = "test",
            existingDateId = "id",
            existingDateTitle = "Birthday", 
            existingDateDay = 1,
            existingDateMonth = 1,
            existingDateYear = 2024
        )
        
        val jsonPrimitives = Json.encodeToString(screenWithPrimitives)
        
        println("With primitives: ${jsonPrimitives.length} chars")
        println("Approximately: ${jsonPrimitives.toByteArray().size} bytes")
        
        // Typically < 200 bytes
        assertTrue(jsonPrimitives.toByteArray().size < 500)
    }
}