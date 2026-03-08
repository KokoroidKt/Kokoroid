/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.session.container

import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.user.User
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.pluginApi.conversation.ConversationOrchestrator
import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.conversation.Reply
import dev.kokoroidkt.pluginApi.dsl.conversation
import dev.kokoroidkt.pluginApi.factory.ConversationOrchestratorFactory
import dev.kokoroidkt.pluginApi.session.container.SessionFactoty
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonElement
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.mp.KoinPlatform.getKoin
import java.time.Instant

class TestBot(
    override val botId: String,
) : Bot {
    override fun callApi(
        apiEndpoint: String,
        data: JsonElement,
    ) {
        println("$botId -> apiEndpoint: $apiEndpoint, data: $data")
    }

    override fun replyMessage(
        event: Event,
        message: MessageChain,
    ) {
    }
}

/**
 * Tests for the MutexSessionContainer class.
 * The `getOrCreateSession` method ensures that either a matching session is retrieved
 * or a new session is created based on the provided event, processor, and user group.
 */
class MutexSessionContainerTest {
    val processor: Processor = conversation { setProcessor(::mockProcessor) }
    val orchestrator = getKoin().get<ConversationOrchestratorFactory>().create(processor)

    @Test
    fun `test getOrCreateSession creates new session when no matching session exists`() =
        runBlocking {
            val container = MutexSessionContainer()
            val event = TestEvent(userGroupA)

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

            val event = TestEvent(userGroupA)
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
            val event = TestEvent(userGroupA)
            val processor: Processor = conversation { setProcessor(::mockProcessor) }

            val session1 = container.getOrCreateSession(event, processor, userGroupA, orchestrator)
            val session2 = container.getOrCreateSession(event, processor, userGroupA, orchestrator)

            assertSame(session1, session2)
        }

    @Test
    fun `test getOrCreateSession creates new session for different user groups`() =
        runBlocking {
            val container = MutexSessionContainer()
            val eventA = TestEvent(userGroupA)
            val eventB = TestEvent(userGroupB)
            val processor: Processor = conversation { setProcessor(::mockProcessor) }

            val sessionA = container.getOrCreateSession(eventA, processor, userGroupA, orchestrator)
            val sessionB = container.getOrCreateSession(eventB, processor, userGroupB, orchestrator)

            assertNotSame(sessionA, sessionB)
        }

    // Mock processor for testing purposes
    private fun mockProcessor(): Reply = throw NotImplementedError("This is a test processor mock.")

    // Mock event class for testing purposes
    private class TestEvent(
        userGroup: UserGroup,
    ) : Event(
            eventId = "test-event",
            timestamp = Instant.now(),
            users = userGroup,
            TestBot(""),
        )

    // Mock user groups for testing

    class MockUser(
        val id: String,
    ) : User() {
        override val userId get() = id
    }

    private val userGroupA = listOf(MockUser("A"))
    private val userGroupB = listOf(MockUser("B"))

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
