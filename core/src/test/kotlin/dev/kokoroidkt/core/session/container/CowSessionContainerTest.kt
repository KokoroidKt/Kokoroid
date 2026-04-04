// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

@file:Suppress("ktlint:standard:no-wildcard-imports")

package dev.kokoroidkt.core.session.container

import dev.kokoroidkt.core.MockEvent
import dev.kokoroidkt.core.MockUser
import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.coreApi.user.Users
import dev.kokoroidkt.pluginApi.Processable
import dev.kokoroidkt.pluginApi.conversation.Reply
import dev.kokoroidkt.pluginApi.dsl.conversation
import dev.kokoroidkt.pluginApi.factory.ConversationOrchestratorFactory
import dev.kokoroidkt.pluginApi.session.container.SessionFactoty
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.mp.KoinPlatform.getKoin

/**
 * Unit tests for the CowSessionContainer class and its getOrCreateSession method.
 * This method ensures that a session is either retrieved if it exists or created
 * and registered if it does not exist for a given event, processor, and user group.
 */

class CowSessionContainerTest {
    val processor: Processable = conversation { setProcessor(::testReplyProcessor) }
    val orchestrator = getKoin().get<ConversationOrchestratorFactory>().create(processor)

    @Test
    fun `test when session is found existing session is returned`() =
        runBlocking {
            val container = CowSessionContainer()
            val event = MockEvent(users = testUserGroup(1))

            val userGroup = testUserGroup(1)
            val existingSession = getKoin().get<SessionFactoty>().createSession(userGroup, processor, orchestrator)

            container.registerSession(existingSession)

            val result = container.getOrCreateSession(event, processor, userGroup, orchestrator)

            assertEquals(existingSession, result, "Expected existing session to be returned")
        }

    @Test
    fun `test when session does not exist new one is created and registered`() =
        runBlocking {
            val container = CowSessionContainer()
            val event = MockEvent(users = testUserGroup(1))
            val userGroup = testUserGroup(1)

            val result = container.getOrCreateSession(event, processor, userGroup, orchestrator)

            assertNotNull(result, "Expected new session to be created")
            assertTrue(container.snapshot().contains(result), "Expected new session to be registered")
        }

    @Test
    fun `test new session is created for different user group`() =
        runBlocking {
            val container = CowSessionContainer()
            val event1 = MockEvent(users = testUserGroup(1))
            val event2 = MockEvent(users = testUserGroup(1))
            val userGroup1 = testUserGroup(3)
            val userGroup2 = testUserGroup(5)

            val session1 = container.getOrCreateSession(event1, processor, userGroup1, orchestrator)
            val session2 = container.getOrCreateSession(event2, processor, userGroup2, orchestrator)

            assertNotNull(session1, "Expected session for user group 1 to be created")
            assertNotNull(session2, "Expected session for user group 2 to be created")
            assertNotEquals(session1, session2, "Expected different sessions for different user groups")
        }

    // Mock classes and helpers

    private fun testReplyProcessor(): Reply {
        return Reply.NoReply // Assuming Reply is a callable implementation for testing purposes
    }

    fun testUserGroup(len: Int): Users =
        List(len) { i ->
            MockUser(platformUserId = "user_$i")
        }

    companion object {
        @JvmStatic
        @BeforeAll
        fun startKoinApp() {
            startKoin {
                modules(allModules)
            }
        }

        @JvmStatic
        @AfterAll
        fun stopKoinApp() {
            stopKoin()
        }
    }
}
