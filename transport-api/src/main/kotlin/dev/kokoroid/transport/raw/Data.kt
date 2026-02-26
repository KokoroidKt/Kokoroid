/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroid.transport.raw

import kotlinx.serialization.json.JsonElement

sealed class Data {
    data class Json(
        val json: JsonElement,
    ) : Data()
}
