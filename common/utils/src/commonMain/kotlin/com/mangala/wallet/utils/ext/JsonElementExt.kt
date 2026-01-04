package com.mangala.wallet.utils.ext

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

val JsonElement?.jsonArrayOrNull: JsonArray?
    get() = this as? JsonArray

val JsonElement?.jsonObjectOrNull: JsonObject?
    get() = this as? JsonObject

val JsonElement.jsonPrimitiveOrNull: JsonPrimitive?
    get() = this as? JsonPrimitive

val JsonElement.stringOrNull: String?
    get() = (this as? JsonPrimitive)?.contentOrNull