// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroid.transport.raw

import kotlinx.serialization.json.JsonElement

sealed class Data {
    data class Json(
        val json: JsonElement,
    ) : Data()
}
