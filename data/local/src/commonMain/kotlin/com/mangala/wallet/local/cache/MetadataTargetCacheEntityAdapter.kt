package com.mangala.wallet.local.cache

import app.cash.sqldelight.ColumnAdapter

internal val metadataTargetCacheEntityAdapter = object : ColumnAdapter<MetadataTargetCacheEntity, String> {
    override fun decode(databaseValue: String): MetadataTargetCacheEntity {
        return MetadataTargetCacheEntity.valueOf(databaseValue)
    }

    override fun encode(value: MetadataTargetCacheEntity): String {
        return value.name
    }
}