// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroid.transport.raw

import kotlinx.serialization.json.JsonElement

/**
 * 承载的数据
 *
 * @constructor Create empty Data
 */
sealed class Data {
    /**
     * Json数据
     *
     * @property json
     * @constructor Create empty Json
     */
    data class Json(
        val json: JsonElement,
    ) : Data()

    /**
     * 二进制数据
     *
     * @property bytes
     * @constructor Create empty Binary
     */
    data class Binary(
        val bytes: ByteArray,
    ) : Data() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Binary

            return bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int = bytes.contentHashCode()
    }
}
