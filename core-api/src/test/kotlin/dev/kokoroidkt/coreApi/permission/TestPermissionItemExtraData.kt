package dev.kokoroidkt.coreApi.permission

import kotlin.test.Test

class TestPermissionItemExtraData {
    @Test
    fun `test json transport`() {
        val raw = PermissionExtraData.empty()
        raw.set("test1", 123)
        raw.set("test2", "test")
        raw.set("test3", true)
        val json = raw.toJsonString()
        println(json)
        val copy = PermissionExtraData.fromJsonString(json)
        assert(copy.getInt("test1").getOrNull() == raw.getInt("test1").getOrNull())
        assert(copy.getString("test2").getOrNull() == raw.getString("test2").getOrNull())
        assert(copy.getBoolean("test3").getOrNull() == raw.getBoolean("test3").getOrNull())
    }

    @Test
    fun `test add int and get int`() {
        val value = 114514
        val extraData = PermissionExtraData.empty()
        extraData.set("test", value)
        val result = extraData.getInt("test")
        assert(result.getOrNull() == value)
        assert(result.isSuccess)
    }

    @Test
    fun `test get int failed when key not exist`() {
        val extraData = PermissionExtraData.empty()
        val result = extraData.getInt("test")
        assert(result is PermissionExtraDataResult.KeyNotFound)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get int failed when value is not int`() {
        val extraData = PermissionExtraData.empty()
        extraData.set("test", "114514")
        val result = extraData.getInt("test")
        assert(result is PermissionExtraDataResult.WrongType)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get int failed when value is null`() {
        val extraData = PermissionExtraData.empty()
        extraData.setNull("test")
        val result = extraData.getInt("test")
        assert(result is PermissionExtraDataResult.IsNull)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    // Byte
    @Test
    fun `test add byte and get byte`() {
        val value: Byte = 114
        val extraData = PermissionExtraData.empty()
        extraData.set("test", value)
        val result = extraData.getByte("test")
        assert(result.getOrNull() == value)
        assert(result.isSuccess)
    }

    @Test
    fun `test get byte failed when key not exist`() {
        val extraData = PermissionExtraData.empty()
        val result = extraData.getByte("test")
        assert(result is PermissionExtraDataResult.KeyNotFound)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get byte failed when value is not byte`() {
        val extraData = PermissionExtraData.empty()
        extraData.set("test", "114")
        val result = extraData.getByte("test")
        assert(result is PermissionExtraDataResult.WrongType)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get byte failed when value is null`() {
        val extraData = PermissionExtraData.empty()
        extraData.setNull("test")
        val result = extraData.getByte("test")
        assert(result is PermissionExtraDataResult.IsNull)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    // Short
    @Test
    fun `test add short and get short`() {
        val value: Short = 11451
        val extraData = PermissionExtraData.empty()
        extraData.set("test", value)
        val result = extraData.getShort("test")
        assert(result.getOrNull() == value)
        assert(result.isSuccess)
    }

    @Test
    fun `test get short failed when key not exist`() {
        val extraData = PermissionExtraData.empty()
        val result = extraData.getShort("test")
        assert(result is PermissionExtraDataResult.KeyNotFound)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get short failed when value is not short`() {
        val extraData = PermissionExtraData.empty()
        extraData.set("test", "11451")
        val result = extraData.getShort("test")
        assert(result is PermissionExtraDataResult.WrongType)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get short failed when value is null`() {
        val extraData = PermissionExtraData.empty()
        extraData.setNull("test")
        val result = extraData.getShort("test")
        assert(result is PermissionExtraDataResult.IsNull)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    // Long
    @Test
    fun `test add long and get long`() {
        val value = 1145141919810L
        val extraData = PermissionExtraData.empty()
        extraData.set("test", value)
        val result = extraData.getLong("test")
        assert(result.getOrNull() == value)
        assert(result.isSuccess)
    }

    @Test
    fun `test get long failed when key not exist`() {
        val extraData = PermissionExtraData.empty()
        val result = extraData.getLong("test")
        assert(result is PermissionExtraDataResult.KeyNotFound)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get long failed when value is not long`() {
        val extraData = PermissionExtraData.empty()
        extraData.set("test", "1145141919810")
        val result = extraData.getLong("test")
        assert(result is PermissionExtraDataResult.WrongType)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get long failed when value is null`() {
        val extraData = PermissionExtraData.empty()
        extraData.setNull("test")
        val result = extraData.getLong("test")
        assert(result is PermissionExtraDataResult.IsNull)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    // Float
    @Test
    fun `test add float and get float`() {
        val value = 114.514f
        val extraData = PermissionExtraData.empty()
        extraData.set("test", value)
        val result = extraData.getFloat("test")
        assert(result.getOrNull() == value)
        assert(result.isSuccess)
    }

    @Test
    fun `test get float failed when key not exist`() {
        val extraData = PermissionExtraData.empty()
        val result = extraData.getFloat("test")
        assert(result is PermissionExtraDataResult.KeyNotFound)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get float failed when value is not float`() {
        val extraData = PermissionExtraData.empty()
        extraData.set("test", "114.514")
        val result = extraData.getFloat("test")
        assert(result is PermissionExtraDataResult.WrongType)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get float failed when value is null`() {
        val extraData = PermissionExtraData.empty()
        extraData.setNull("test")
        val result = extraData.getFloat("test")
        assert(result is PermissionExtraDataResult.IsNull)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    // Double
    @Test
    fun `test add double and get double`() {
        val value = 114.514
        val extraData = PermissionExtraData.empty()
        extraData.set("test", value)
        val result = extraData.getDouble("test")
        assert(result.getOrNull() == value)
        assert(result.isSuccess)
    }

    @Test
    fun `test get double failed when key not exist`() {
        val extraData = PermissionExtraData.empty()
        val result = extraData.getDouble("test")
        assert(result is PermissionExtraDataResult.KeyNotFound)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get double failed when value is not double`() {
        val extraData = PermissionExtraData.empty()
        extraData.set("test", "114.514")
        val result = extraData.getDouble("test")
        assert(result is PermissionExtraDataResult.WrongType)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get double failed when value is null`() {
        val extraData = PermissionExtraData.empty()
        extraData.setNull("test")
        val result = extraData.getDouble("test")
        assert(result is PermissionExtraDataResult.IsNull)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    // String
    @Test
    fun `test add string and get string`() {
        val value = "114514"
        val extraData = PermissionExtraData.empty()
        extraData.set("test", value)
        val result = extraData.getString("test")
        assert(result.getOrNull() == value)
        assert(result.isSuccess)
    }

    @Test
    fun `test get string failed when key not exist`() {
        val extraData = PermissionExtraData.empty()
        val result = extraData.getString("test")
        assert(result is PermissionExtraDataResult.KeyNotFound)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get string failed when value is not string`() {
        val extraData = PermissionExtraData.empty()
        extraData.set("test", 114514)
        val result = extraData.getString("test")
        assert(result is PermissionExtraDataResult.WrongType)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get string failed when value is null`() {
        val extraData = PermissionExtraData.empty()
        extraData.setNull("test")
        val result = extraData.getString("test")
        assert(result is PermissionExtraDataResult.IsNull)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    // Boolean
    @Test
    fun `test add boolean and get boolean`() {
        val value = true
        val extraData = PermissionExtraData.empty()
        extraData.set("test", value)
        val result = extraData.getBoolean("test")
        assert(result.getOrNull() == value)
        assert(result.isSuccess)
    }

    @Test
    fun `test get boolean failed when key not exist`() {
        val extraData = PermissionExtraData.empty()
        val result = extraData.getBoolean("test")
        assert(result is PermissionExtraDataResult.KeyNotFound)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get boolean failed when value is not boolean`() {
        val extraData = PermissionExtraData.empty()
        extraData.set("test", "true")
        val result = extraData.getBoolean("test")
        assert(result is PermissionExtraDataResult.WrongType)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }

    @Test
    fun `test get boolean failed when value is null`() {
        val extraData = PermissionExtraData.empty()
        extraData.setNull("test")
        val result = extraData.getBoolean("test")
        assert(result is PermissionExtraDataResult.IsNull)
        assert(result.getOrNull() == null)
        assert(!result.isSuccess)
    }
}
