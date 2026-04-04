// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
//
// SPDX-FileContributor: Junie
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.rule.builtin

import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.permission.PermissionModel
import dev.kokoroidkt.pluginApi.rule.MockBot
import dev.kokoroidkt.pluginApi.rule.MockEvent
import dev.kokoroidkt.pluginApi.rule.MockUser
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PermissionRuleTest {
    @Test
    fun `requirePermission should return true if any user has permission`() =
        runBlocking {
            val permissionModel = mockk<PermissionModel>()
            val user1 = MockUser("user1")
            val user2 = MockUser("user2")
            val bot = MockBot()
            val event = MockEvent(users = listOf(user1, user2))
            val chain = MessageChain.empty()

            every { permissionModel.verify(user1, any()) } returns false
            every { permissionModel.verify(user2, any()) } returns true

            val rule = requirePermission(permissionModel)
            val result = rule.check(bot, event, chain, listOf(user1, user2))

            assertTrue(result)
        }

    @Test
    fun `requirePermission should return false if no user has permission`() =
        runBlocking {
            val permissionModel = mockk<PermissionModel>()
            val user1 = MockUser("user1")
            val bot = MockBot()
            val event = MockEvent(users = listOf(user1))
            val chain = MessageChain.empty()

            every { permissionModel.verify(user1, any()) } returns false

            val rule = requirePermission(permissionModel)
            val result = rule.check(bot, event, chain, listOf(user1))

            assertFalse(result)
        }

    @Test
    fun `requirePermission should return false if users list is null or empty`() =
        runBlocking {
            val permissionModel = mockk<PermissionModel>()
            val bot = MockBot()
            val event = MockEvent()
            val chain = MessageChain.empty()

            val rule = requirePermission(permissionModel)

            assertFalse(rule.check(bot, event, chain, null))
            assertFalse(rule.check(bot, event, chain, emptyList()))
        }

    @Test
    fun `requirePermission should return true if user is op and requireExact is false`() =
        runBlocking {
            val permissionModel = mockk<PermissionModel>()
            val user1 = MockUser("user1", isOpMock = true)
            val bot = MockBot()
            val event = MockEvent(users = listOf(user1))
            val chain = MessageChain.empty()

            // Even if verify returns false, it should return true because user is OP
            every { permissionModel.verify(user1, any()) } returns false

            val rule = requirePermission(permissionModel, requireExact = false)
            val result = rule.check(bot, event, chain, listOf(user1))

            assertTrue(result)
        }

    @Test
    fun `requirePermission should return false if user is op but requireExact is true and user lacks permission`() =
        runBlocking {
            val permissionModel = mockk<PermissionModel>()
            val user1 = MockUser("user1", isOpMock = true)
            val bot = MockBot()
            val event = MockEvent(users = listOf(user1))
            val chain = MessageChain.empty()

            every { permissionModel.verify(user1, any()) } returns false

            val rule = requirePermission(permissionModel, requireExact = true)
            val result = rule.check(bot, event, chain, listOf(user1))

            assertFalse(result)
        }

    @Test
    fun `withoutPermission should return true if no user has permission`() =
        runBlocking {
            val permissionModel = mockk<PermissionModel>()
            val user1 = MockUser("user1")
            val bot = MockBot()
            val event = MockEvent(users = listOf(user1))
            val chain = MessageChain.empty()

            every { permissionModel.verify(user1, any()) } returns false

            val rule = withoutPermission(permissionModel)
            val result = rule.check(bot, event, chain, listOf(user1))

            assertTrue(result)
        }

    @Test
    fun `withoutPermission should return false if any user has permission`() =
        runBlocking {
            val permissionModel = mockk<PermissionModel>()
            val user1 = MockUser("user1")
            val bot = MockBot()
            val event = MockEvent(users = listOf(user1))
            val chain = MessageChain.empty()

            every { permissionModel.verify(user1, any()) } returns true

            val rule = withoutPermission(permissionModel)
            val result = rule.check(bot, event, chain, listOf(user1))

            assertFalse(result)
        }

    @Test
    fun `requireOp should return true if any user is op`() =
        runBlocking {
            val user1 = MockUser("user1", isOpMock = false)
            val user2 = MockUser("user2", isOpMock = true)
            val bot = MockBot()
            val event = MockEvent(users = listOf(user1, user2))
            val chain = MessageChain.empty()

            val rule = requireOp()
            val result = rule.check(bot, event, chain, listOf(user1, user2))

            assertTrue(result)
        }

    @Test
    fun `requireOp should return false if no user is op`() =
        runBlocking {
            val user1 = MockUser("user1", isOpMock = false)
            val bot = MockBot()
            val event = MockEvent(users = listOf(user1))
            val chain = MessageChain.empty()

            val rule = requireOp()
            val result = rule.check(bot, event, chain, listOf(user1))

            assertFalse(result)
        }

    @Test
    fun `requireOp should return false if users list is null or empty`() =
        runBlocking {
            val bot = MockBot()
            val event = MockEvent()
            val chain = MessageChain.empty()

            val rule = requireOp()

            assertFalse(rule.check(bot, event, chain, null))
            assertFalse(rule.check(bot, event, chain, emptyList()))
        }
}
