package com.mangala.wallet.features.chains.antelope_base.data.local

import app.cash.sqldelight.ColumnAdapter
import com.mangala.wallet.features.chains.antelope_base.domain.model.cache.AntelopeRemoteKeyTargetEntity
import org.koin.core.module.Module

internal const val ANTELOPE_DATABASE_NAME = "antelopedatabase.db"

internal val listOfStringsAdapter = object : ColumnAdapter<List<String>, String> {
    override fun decode(databaseValue: String) =
        if (databaseValue.isEmpty()) {
            listOf()
        } else {
            databaseValue.split(";")
        }

    override fun encode(value: List<String>) = value.joinToString(separator = ";")
}

internal val listOfLongsAdapter = object : ColumnAdapter<List<Long>, String> {
    override fun decode(databaseValue: String) =
        if (databaseValue.isEmpty()) {
            listOf()
        } else {
            databaseValue.split(",").map { it.toLong() }
        }

    override fun encode(value: List<Long>) = value.joinToString(separator = ",")
}

internal val enumAntelopeRemoteKeyTargetEntity = EnumColumnAdapter(
    valueOf = AntelopeRemoteKeyTargetEntity::valueOf,
    default = AntelopeRemoteKeyTargetEntity.UNKNOWN
)

internal class EnumColumnAdapter<T : Enum<T>>(
    private val valueOf: (String) -> T, // A function to map String to Enum
    private val default: T             // Default value for invalid strings
) : ColumnAdapter<T, String> {
    override fun decode(databaseValue: String): T {
        return try {
            valueOf(databaseValue)
        } catch (e: Exception) {
            default
        }
    }

    override fun encode(value: T): String {
        return value.name
    }
}

expect fun antelopeDatabaseModule(): Module