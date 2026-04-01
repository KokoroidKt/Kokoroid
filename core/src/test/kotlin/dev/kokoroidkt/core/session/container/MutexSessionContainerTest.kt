// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.session.container

import dev.kokoroidkt.core.MockEvent
import dev.kokoroidkt.core.MockUser
import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.pluginApi.Processable
import dev.kokoroidkt.pluginApi.conversation.Processor
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
 * Tests for the MutexSessionContainer class.
 * The `getOrCreateSession` method ensures that either a matching session is retrieved
 * or a new session is created based on the provided event, processor, and user group.
 */
class MutexSessionContainerTest {
    val processor: Processable = conversation { setProcessor(::mockProcessor) }
    val orchestrator = getKoin().get<ConversationOrchestratorFactory>().create(processor)

    @Test
    fun `test getOrCreateSession creates new session when no matching session exists`() =
        runBlocking {
            val container = MutexSessionContainer()
            val event = MockEvent(users = userGroupA)

            val session = container.getOrCreateSession(event, processor, userGroupA, orchestrator)

            assertNotNull(session)
            assertEquals(userGroupA, session.users)
        }

    @Test
    fun `test getOrCreateSession retrieves existing session if match exists`() =
        runBlocking {
            val container = MutexSessionContainer()
            val orchestrator = getKoin().get<ConversationOrchestratorFactory>().create(processor)
            val preExistingSession =
                getKoin().get<SessionFactoty>().createSession(
                    processor = conversation { setProcessor(::mockProcessor) },
                    user = userGroupA,
                    conversationOrchestrator = orchestrator,
                )

            container.registerSession(preExistingSession)

            val event = MockEvent(users = userGroupA)
            val retrievedSession =
                container.getOrCreateSession(
                    event,
                    conversation { setProcessor(::mockProcessor) },
                    userGroupA,
                    orchestrator,
                )

            assertNotNull(retrievedSession)
            assertSame(preExistingSession, retrievedSession)
        }

    @Test
    fun `test getOrCreateSession does not create duplicate sessions for same user group`() =
        runBlocking {
            val container = MutexSessionContainer()
            val event = MockEvent(users = userGroupA)
            val processor: Processable = conversation { setProcessor(::mockProcessor) }

            val session1 = container.getOrCreateSession(event, processor, userGroupA, orchestrator)
            val session2 = container.getOrCreateSession(event, processor, userGroupA, orchestrator)

            assertSame(session1, session2)
        }

    @Test
    fun `test getOrCreateSession creates new session for different user groups`() =
        runBlocking {
            val container = MutexSessionContainer()
            val eventA = MockEvent(users = userGroupA)
            val eventB = MockEvent(users = userGroupB)
            val processor: Processable = conversation { setProcessor(::mockProcessor) }

            val sessionA = container.getOrCreateSession(eventA, processor, userGroupA, orchestrator)
            val sessionB = container.getOrCreateSession(eventB, processor, userGroupB, orchestrator)

            assertNotSame(sessionA, sessionB)
        }

    // Mock processor for testing purposes
    private fun mockProcessor(): Reply = throw NotImplementedError("This is a test processor mock.")

    private val userGroupA = listOf(MockUser(platfromUserId = "A"))
    private val userGroupB = listOf(MockUser(platfromUserId = "B"))

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
