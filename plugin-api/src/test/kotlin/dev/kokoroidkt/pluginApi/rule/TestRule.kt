// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroid.pluginApi.rule

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.user.User
import dev.kokoroidkt.pluginApi.dsl.rule
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonElement
import org.junit.jupiter.api.Assertions.assertFalse
import java.time.Instant
import kotlin.test.Test

val ruleChainSuccess =
    rule {
        with { true }
    }

val ruleChainFailed =
    rule {
        with { true }
        with { false }
    }

class TestBot : Bot {
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

    override val botId: String
        get() = ""
}

class TestRule {
    @Test
    fun `test success`() {
        runBlocking {
            assert(
                ruleChainSuccess.check(
                    TestBot(),
                    object : Event("", Instant.now(), users = listOf(), TestBot()) {},
                    MessageChain.empty(),
                    listOf(
                        object : User() {
                            override val userId: String
                                get() = ""
                        },
                    ),
                ),
            )
        }
    }

    @Test
    fun `test failed`() {
        runBlocking {
            assertFalse(
                ruleChainFailed.check(
                    TestBot(),
                    object : Event("", Instant.now(), users = listOf(), TestBot()) {},
                    MessageChain.empty(),
                    listOf(
                        object : User() {
                            override val userId: String
                                get() = ""
                        },
                    ),
                ),
            )
        }
    }
}
