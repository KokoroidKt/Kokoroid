// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.rule

import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.pluginApi.dsl.rule
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertFalse
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

class TestRule {
    @Test
    fun `test success`() {
        runBlocking {
            assert(
                ruleChainSuccess.check(
                    MockBot(),
                    MockEvent(),
                    MessageChain.empty(),
                    listOf(MockUser()),
                ),
            )
        }
    }

    @Test
    fun `test failed`() {
        runBlocking {
            assertFalse(
                ruleChainFailed.check(
                    MockBot(),
                    MockEvent(),
                    MessageChain.empty(),
                    listOf(MockUser()),
                ),
            )
        }
    }
}
