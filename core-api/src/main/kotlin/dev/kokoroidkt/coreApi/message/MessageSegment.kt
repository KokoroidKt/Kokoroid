// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.coreApi.message

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.buildJsonObject

abstract class MessageSegment {
    open val rawData: JsonElement = buildJsonObject { }
    val isTextConvertible: Boolean = this is TextConvertible

    open fun toJson(): String = Json.encodeToString(rawData)

    override fun toString(): String = "MessageSegment[${toJson()}]"
}
