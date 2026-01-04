package com.mangala.wallet.websocket.chat

import com.mangala.wallet.websocket.chat.device.BatteryState
import com.mangala.wallet.websocket.chat.device.ThermalState
import com.mangala.wallet.websocket.chat.device.MemoryPressure
import com.mangala.wallet.websocket.chat.network.NetworkType
import com.mangala.wallet.websocket.chat.network.NetworkQuality
import com.mangala.wallet.websocket.chat.network.NetworkState
import com.mangala.wallet.websocket.chat.websocket.models.ChatFrame
import com.mangala.wallet.websocket.chat.websocket.models.EncryptedPayload
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Basic tests to verify WebSocket chat functionality
 */
class BasicWebSocketTest {
    
    @Test
    fun `test ChatFrame creation`() {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val payload = EncryptedPayload(
            data = "Hello World".encodeToByteArray()
        )
        
        val message = ChatFrame.Message(
            id = "test-id",
            timestamp = timestamp,
            payload = payload,
            recipientAddress = "0x123",
            senderAddress = "0x456"
        )
        
        assertEquals("test-id", message.id)
        assertEquals(timestamp, message.timestamp)
        assertEquals("0x123", message.recipientAddress)
        assertEquals("0x456", message.senderAddress)
        assertNotNull(message.payload)
    }
    
    @Test
    fun `test NetworkState creation`() {
        val networkState = NetworkState(
            isConnected = true,
            type = NetworkType.WIFI,
            quality = NetworkQuality.EXCELLENT
        )
        
        assertTrue(networkState.isConnected)
        assertEquals(NetworkType.WIFI, networkState.type)
        assertEquals(NetworkQuality.EXCELLENT, networkState.quality)
    }
    
    @Test
    fun `test BatteryState enum values`() {
        val chargingState = BatteryState.CHARGING
        val dischargingState = BatteryState.DISCHARGING
        val fullState = BatteryState.FULL
        val unknownState = BatteryState.UNKNOWN
        
        assertEquals(BatteryState.CHARGING, chargingState)
        assertEquals(BatteryState.DISCHARGING, dischargingState)
        assertEquals(BatteryState.FULL, fullState)
        assertEquals(BatteryState.UNKNOWN, unknownState)
    }
    
    @Test
    fun `test ThermalState enum values`() {
        val nominalState = ThermalState.NOMINAL
        val fairState = ThermalState.FAIR
        val seriousState = ThermalState.SERIOUS
        val criticalState = ThermalState.CRITICAL
        
        assertEquals(ThermalState.NOMINAL, nominalState)
        assertEquals(ThermalState.FAIR, fairState)
        assertEquals(ThermalState.SERIOUS, seriousState)
        assertEquals(ThermalState.CRITICAL, criticalState)
    }
    
    @Test
    fun `test MemoryPressure enum values`() {
        val normalPressure = MemoryPressure.NORMAL
        val warningPressure = MemoryPressure.WARNING
        val criticalPressure = MemoryPressure.CRITICAL
        val terminatedPressure = MemoryPressure.TERMINATED
        
        assertEquals(MemoryPressure.NORMAL, normalPressure)
        assertEquals(MemoryPressure.WARNING, warningPressure)
        assertEquals(MemoryPressure.CRITICAL, criticalPressure)
        assertEquals(MemoryPressure.TERMINATED, terminatedPressure)
    }
    
    @Test
    fun `test heartbeat frame creation`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val heartbeat = ChatFrame.Heartbeat(
            id = "heartbeat-1",
            timestamp = timestamp
        )
        
        assertEquals("heartbeat-1", heartbeat.id)
        assertEquals(timestamp, heartbeat.timestamp)
    }
    
    @Test
    fun `test acknowledgment frame creation`() = runTest {
        val timestamp = Clock.System.now().toEpochMilliseconds()
        val ack = ChatFrame.Acknowledgment(
            id = "ack-1",
            timestamp = timestamp,
            messageId = "msg-1",
            status = com.mangala.wallet.websocket.chat.websocket.models.DeliveryStatus.DELIVERED
        )
        
        assertEquals("ack-1", ack.id)
        assertEquals(timestamp, ack.timestamp)
        assertEquals("msg-1", ack.messageId)
    }
}