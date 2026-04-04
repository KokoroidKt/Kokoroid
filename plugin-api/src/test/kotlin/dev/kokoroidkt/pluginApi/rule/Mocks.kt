// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
//
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.rule

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.permission.GrantedPermission
import dev.kokoroidkt.coreApi.user.User
import kotlinx.serialization.json.JsonElement
import java.time.Instant

class MockBot(
    override val botId: String = "test_bot",
) : Bot {
    override fun callApi(
        apiEndpoint: String,
        data: JsonElement,
    ) {
    }

    override fun replyMessage(
        event: Event,
        message: MessageChain,
    ) {
    }
}

class MockUser(
    override val platformUserId: String = "test_user",
    adapterId: String = "test_adapter",
    private val isOpMock: Boolean = false,
) : User(adapterId) {
    override val userId: String = "$platformUserId@$adapterId"

    override val isOp: Boolean
        get() = isOpMock

    override fun findPermission(
        namespace: String,
        permissionNode: String,
    ): GrantedPermission? = null

    override fun addPermission(permission: GrantedPermission) {}
}

class MockEvent(
    eventId: String = "test_event",
    timestamp: Instant = Instant.now(),
    users: List<User> = emptyList(),
    bot: Bot = MockBot(),
) : Event(eventId, timestamp, users, bot)
