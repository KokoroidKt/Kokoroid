// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.permission

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.JsonUnquotedLiteral
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.longOrNull

/**
 * 权限附加信息，应当也仅应当用于限定更细粒度的权限控制
 * 只允许存储 json 原生数据类型：数字、字符串、逻辑值、null
 * 不允许嵌套，不允许使用数组
 * **不应该**使用 PermissionExtraData 存储业务数据
 * 如果需要持久化用户数据，需要自行连接数据库
 *
 * 例如：对 user.profile.edit 节点，需要附加信息 dmOnly: Boolean（只能在私聊运行），
 * 则可以用 PermissionExtraData 存储 {"dmOnly": true}，并用自定义权限检查函数获取该字段
 */
class PermissionExtraData {
    private val data: MutableMap<String, JsonElement> = mutableMapOf()

    private fun setElement(
        key: String,
        value: JsonElement,
    ) {
        require(value !is JsonObject) { "PermissionExtraData does not allow nested objects" }
        require(value !is kotlinx.serialization.json.JsonArray) { "PermissionExtraData does not allow arrays" }
        data[key] = value
    }

    private inline fun <reified T> getAny(key: String): PermissionExtraDataResult<T> {
        if (!data.containsKey(key)) {
            return PermissionExtraDataResult.KeyNotFound()
        }

        val element = data[key] ?: return PermissionExtraDataResult.IsNull()
        if (element is JsonNull) {
            return PermissionExtraDataResult.IsNull()
        }

        val value: T =
            when (T::class) {
                String::class -> {
                    val primitive = element as? JsonPrimitive ?: return PermissionExtraDataResult.WrongType()
                    if (!primitive.isString) return PermissionExtraDataResult.WrongType()
                    primitive.content as T
                }

                Boolean::class -> {
                    val primitive = element as? JsonPrimitive ?: return PermissionExtraDataResult.WrongType()
                    if (primitive.isString) return PermissionExtraDataResult.WrongType()
                    primitive.booleanOrNull as? T ?: return PermissionExtraDataResult.WrongType()
                }

                Byte::class -> {
                    val primitive = element as? JsonPrimitive ?: return PermissionExtraDataResult.WrongType()
                    if (primitive.isString) return PermissionExtraDataResult.WrongType()
                    primitive.longOrNull?.toByte() as? T ?: return PermissionExtraDataResult.WrongType()
                }

                Short::class -> {
                    val primitive = element as? JsonPrimitive ?: return PermissionExtraDataResult.WrongType()
                    if (primitive.isString) return PermissionExtraDataResult.WrongType()
                    primitive.longOrNull?.toShort() as? T ?: return PermissionExtraDataResult.WrongType()
                }

                Int::class -> {
                    val primitive = element as? JsonPrimitive ?: return PermissionExtraDataResult.WrongType()
                    if (primitive.isString) return PermissionExtraDataResult.WrongType()
                    primitive.longOrNull?.toInt() as? T ?: return PermissionExtraDataResult.WrongType()
                }

                Long::class -> {
                    val primitive = element as? JsonPrimitive ?: return PermissionExtraDataResult.WrongType()
                    if (primitive.isString) return PermissionExtraDataResult.WrongType()
                    primitive.longOrNull as? T ?: return PermissionExtraDataResult.WrongType()
                }

                Float::class -> {
                    val primitive = element as? JsonPrimitive ?: return PermissionExtraDataResult.WrongType()
                    if (primitive.isString) return PermissionExtraDataResult.WrongType()
                    primitive.floatOrNull as? T ?: return PermissionExtraDataResult.WrongType()
                }

                Double::class -> {
                    val primitive = element as? JsonPrimitive ?: return PermissionExtraDataResult.WrongType()
                    if (primitive.isString) return PermissionExtraDataResult.WrongType()
                    primitive.doubleOrNull as? T ?: return PermissionExtraDataResult.WrongType()
                }

                else -> {
                    return PermissionExtraDataResult.WrongType()
                }
            }

        return PermissionExtraDataResult.Success(value)
    }

    fun set(
        key: String,
        value: String,
    ) {
        setElement(key, JsonPrimitive(value))
    }

    fun set(
        key: String,
        value: Boolean,
    ) {
        setElement(key, JsonPrimitive(value))
    }

    fun set(
        key: String,
        value: Byte,
    ) {
        setElement(key, JsonPrimitive(value))
    }

    fun set(
        key: String,
        value: Short,
    ) {
        setElement(key, JsonPrimitive(value))
    }

    fun set(
        key: String,
        value: Int,
    ) {
        setElement(key, JsonPrimitive(value))
    }

    fun set(
        key: String,
        value: Long,
    ) {
        setElement(key, JsonPrimitive(value))
    }

    fun set(
        key: String,
        value: Float,
    ) {
        setElement(key, JsonPrimitive(value))
    }

    fun set(
        key: String,
        value: Double,
    ) {
        setElement(key, JsonPrimitive(value))
    }

    fun set(
        key: String,
        value: UByte,
    ) {
        setElement(key, JsonPrimitive(value.toLong()))
    }

    fun set(
        key: String,
        value: UShort,
    ) {
        setElement(key, JsonPrimitive(value.toLong()))
    }

    fun set(
        key: String,
        value: UInt,
    ) {
        setElement(key, JsonPrimitive(value.toLong()))
    }

    fun set(
        key: String,
        value: ULong,
    ) {
        setElement(key, JsonPrimitive(value.toString()))
    }

    fun setNull(key: String) {
        setElement(key, JsonNull)
    }

    fun getString(key: String) = getAny<String>(key)

    fun getBoolean(key: String) = getAny<Boolean>(key)

    fun getByte(key: String) = getAny<Byte>(key)

    fun getShort(key: String) = getAny<Short>(key)

    fun getInt(key: String) = getAny<Int>(key)

    fun getLong(key: String) = getAny<Long>(key)

    fun getFloat(key: String) = getAny<Float>(key)

    fun getDouble(key: String) = getAny<Double>(key)

    fun toJsonElement(): JsonElement = JsonObject(data)

    fun toJsonString(): String =
        kotlinx.serialization.json.Json
            .encodeToString(JsonElement.serializer(), toJsonElement())

    companion object {
        fun fromJsonElement(json: JsonElement): PermissionExtraData {
            require(json is JsonObject) { "PermissionExtraData only accepts JsonObject as root" }
            val result = PermissionExtraData()
            json.forEach { (key, value) ->
                require(value !is kotlinx.serialization.json.JsonArray) { "PermissionExtraData does not allow arrays" }
                require(value !is JsonObject) { "PermissionExtraData does not allow nested objects" }
                result.setElement(key, value)
            }
            return result
        }

        fun fromJsonString(json: String): PermissionExtraData {
            val jsonElement =
                kotlinx.serialization.json.Json
                    .parseToJsonElement(json)
            return fromJsonElement(jsonElement)
        }

        fun empty() = PermissionExtraData()
    }
}
