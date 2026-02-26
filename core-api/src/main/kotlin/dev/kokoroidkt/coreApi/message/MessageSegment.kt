/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.coreApi.message

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject

abstract class MessageSegment {
    open val rawData: JsonElement = buildJsonObject { }
    abstract val isTextConvertible: Boolean

    open fun toJson(): String = Json.encodeToString(rawData)

    override fun toString(): String = "MessageSegment[${toJson()}]"
}
