package com.mangala.wallet.features.conversationui.data.local.mapper

import com.mangala.wallet.features.conversationui.ConversationSession as DbSession
import com.mangala.wallet.features.conversationui.domain.model.ConversationSession
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer

object SessionMapper {
    private val json = Json { ignoreUnknownKeys = true }
    private val mapSerializer = MapSerializer(String.serializer(), String.serializer())

    fun DbSession.toDomainModel(): ConversationSession {
        return ConversationSession(
            id = id,
            userId = userId,
            startTime = Instant.fromEpochMilliseconds(startTime),
            lastUpdatedTime = Instant.fromEpochMilliseconds(lastUpdatedTime),
            title = title,
            metadata = try {
                json.decodeFromString(mapSerializer, metadata)
            } catch (e: Exception) {
                emptyMap()
            },
            messages = emptyList() // Messages are loaded separately
        )
    }

    fun ConversationSession.toDbModel(): DbSession {
        return DbSession(
            id = id,
            userId = userId,
            startTime = startTime.toEpochMilliseconds(),
            lastUpdatedTime = lastUpdatedTime.toEpochMilliseconds(),
            title = title,
            metadata = json.encodeToString(mapSerializer, metadata)
        )
    }
}