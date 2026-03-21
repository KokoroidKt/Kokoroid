// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.coreApi.user

@Suppress("UNCHECKED_CAST")
class UserStorage {
    val map: MutableMap<Any, MutableMap<String, Any?>> = mutableMapOf()

    internal fun set(
        extensionId: String,
        key: String,
        value: Any?,
    ) {
        map.getOrPut(extensionId) { mutableMapOf() }[key] = value
    }

    @JvmName("getValue")
    internal fun getValue(
        extensionId: String,
        key: String,
    ): Any? = map[extensionId]?.get(key)

    internal fun <T> getValue(
        extensionId: String,
        key: String,
    ): T? = map[extensionId]?.get(key) as? T

    internal fun getBoolean(
        extensionId: String,
        key: String,
    ): Boolean? = map[extensionId]?.get(key) as? Boolean

    internal fun getInt(
        extensionId: String,
        key: String,
    ): Int? = map[extensionId]?.get(key) as? Int

    internal fun getLong(
        extensionId: String,
        key: String,
    ): Long? = map[extensionId]?.get(key) as? Long

    internal fun getFloat(
        extensionId: String,
        key: String,
    ): Float? = map[extensionId]?.get(key) as? Float

    internal fun getDouble(
        extensionId: String,
        key: String,
    ): Double? = map[extensionId]?.get(key) as? Double

    internal fun getString(
        extensionId: String,
        key: String,
    ): String? = map[extensionId]?.get(key) as? String

    internal fun getChar(
        extensionId: String,
        key: String,
    ): Char? = map[extensionId]?.get(key) as? Char

    internal fun getByte(
        extensionId: String,
        key: String,
    ): Byte? = map[extensionId]?.get(key) as? Byte

    internal fun getShort(
        extensionId: String,
        key: String,
    ): Short? = map[extensionId]?.get(key) as? Short

    internal fun getBoolean(
        extensionId: String,
        key: String,
        defaultValue: Boolean,
    ): Boolean = (map[extensionId]?.get(key) as? Boolean) ?: defaultValue

    internal fun getInt(
        extensionId: String,
        key: String,
        defaultValue: Int,
    ): Int = (map[extensionId]?.get(key) as? Int) ?: defaultValue

    internal fun getLong(
        extensionId: String,
        key: String,
        defaultValue: Long,
    ): Long = (map[extensionId]?.get(key) as? Long) ?: defaultValue

    internal fun getFloat(
        extensionId: String,
        key: String,
        defaultValue: Float,
    ): Float = (map[extensionId]?.get(key) as? Float) ?: defaultValue

    internal fun getDouble(
        extensionId: String,
        key: String,
        defaultValue: Double,
    ): Double = (map[extensionId]?.get(key) as? Double) ?: defaultValue

    internal fun getString(
        extensionId: String,
        key: String,
        defaultValue: String,
    ): String = (map[extensionId]?.get(key) as? String) ?: defaultValue

    internal fun getChar(
        extensionId: String,
        key: String,
        defaultValue: Char,
    ): Char = (map[extensionId]?.get(key) as? Char) ?: defaultValue

    internal fun getByte(
        extensionId: String,
        key: String,
        defaultValue: Byte,
    ): Byte = (map[extensionId]?.get(key) as? Byte) ?: defaultValue

    internal fun getShort(
        extensionId: String,
        key: String,
        defaultValue: Short,
    ): Short = (map[extensionId]?.get(key) as? Short) ?: defaultValue

    internal fun getBooleanOrNull(
        extensionId: String,
        key: String,
    ): Boolean? = map[extensionId]?.get(key) as? Boolean

    internal fun getIntOrNull(
        extensionId: String,
        key: String,
    ): Int? = map[extensionId]?.get(key) as? Int

    internal fun getLongOrNull(
        extensionId: String,
        key: String,
    ): Long? = map[extensionId]?.get(key) as? Long

    internal fun getFloatOrNull(
        extensionId: String,
        key: String,
    ): Float? = map[extensionId]?.get(key) as? Float

    internal fun getDoubleOrNull(
        extensionId: String,
        key: String,
    ): Double? = map[extensionId]?.get(key) as? Double

    internal fun getStringOrNull(
        extensionId: String,
        key: String,
    ): String? = map[extensionId]?.get(key) as? String

    internal fun getCharOrNull(
        extensionId: String,
        key: String,
    ): Char? = map[extensionId]?.get(key) as? Char

    internal fun getByteOrNull(
        extensionId: String,
        key: String,
    ): Byte? = map[extensionId]?.get(key) as? Byte

    internal fun getShortOrNull(
        extensionId: String,
        key: String,
    ): Short? = map[extensionId]?.get(key) as? Short
}
