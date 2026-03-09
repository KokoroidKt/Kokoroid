/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.conversation.extension

import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.coreApi.user.special.NoUser
import dev.kokoroidkt.pluginApi.conversation.Reply
import dev.kokoroidkt.pluginApi.conversation.extensions.waitForEvent
import dev.kokoroidkt.pluginApi.dsl.conversation
import dev.kokoroidkt.pluginApi.dsl.rule
import dev.kokoroidkt.pluginApi.factory.ConversationOrchestratorFactory
import dev.kokoroidkt.pluginApi.session.SessionState
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonElement
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.mp.KoinPlatform.getKoin
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertFalse

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

class TestEvent(
    eventId: String,
    users: UserGroup = NoUser.NO_USER_GROUP,
) : Event(eventId, Instant.now(), users, TestBot("")) {
    fun sayHi() {
        println("hi")
    }

    fun getCheckId(): String = "testEvent$eventId"
}

class AnotherTestEvent(
    eventId: String,
    users: UserGroup = NoUser.NO_USER_GROUP,
) : Event(eventId, Instant.now(), users, TestBot("")) {
    fun sayHi() {
        println("hi from another")
    }

    fun getCheckId(): String = "anotherTestEvent$eventId"
}

suspend fun waitForEventProcessor(event: TestEvent): Reply {
    event.sayHi()
    println(event.getCheckId())
    val event2 = waitForEvent<TestEvent>()
    event2.sayHi()
    println(event2.getCheckId())
    return Reply.NoReply
}

suspend fun waitForEventWthRuleProcessor(event1: TestEvent): Reply {
    event1.sayHi()
    println(event1.getCheckId())
    val event2 =
        waitForEvent<TestEvent>(
            rules = rule { with { event.eventId == "Matched" } },
        )
    delay(50) // processing....
    event2.sayHi()
    println(event2.getCheckId())
    return Reply.NoReply
}

suspend fun finishWithoutWait(event: TestEvent): Reply {
    event.sayHi()
    delay(1000) // 这期间会去检查这个会话是不是被注册了，如果被注册了就验证失败
    println("check successfully")
    return Reply.NoReply
}

class TestWaiting {
    @Test
    fun `test register session on wait only`() {
        runBlocking {
            val orchestrator =
                getKoin().get<ConversationOrchestratorFactory>().create(
                    conversation {
                        setProcessor(::finishWithoutWait)
                    },
                )
            val promise1 = orchestrator.callSessionToProcessOrCreate(TestEvent("123"), TestBot("123"))
            println(promise1.session.state)
            assertFalse(orchestrator.isExist(promise1.session))
            promise1.deferred.await()
            println((promise1.session.state as SessionState.Finished).reply)
        }
    }

    @Test
    fun `test wait for event`() {
        runBlocking {
            val orchestrator =
                getKoin().get<ConversationOrchestratorFactory>().create(
                    conversation {
                        setProcessor(::waitForEventProcessor)
                    },
                )
            val promise1 = orchestrator.callSessionToProcessOrCreate(TestEvent("123"), TestBot("123"))
            println(promise1.session.state)
            launch {
                delay(100)
                val promise2 = orchestrator.callSessionToProcessOrCreate(TestEvent("321"), TestBot("321"))

                println("with status: ${promise2.session.state}")
            }
            promise1.deferred.await()

            delay(200)
            if (promise1.deferred.isActive) {
                println("promise is still active!!")
                assert(false)
            }

            assert(promise1.deferred.isCompleted)
            if (promise1.session.state is SessionState.Finished) {
                println("Reply: ${(promise1.session.state as SessionState.Finished).reply}")

                assert(true)
            } else {
                assert(false)
            }
        }
    }

    @Test
    fun `test wait for event with rule`() {
        runBlocking {
            val orchestrator =
                getKoin().get<ConversationOrchestratorFactory>().create(
                    conversation {
                        setProcessor(::waitForEventWthRuleProcessor)
                    },
                )
            val promise1 = orchestrator.callSessionToProcessOrCreate(TestEvent("123"), TestBot("123"))
            println(promise1.session.state)
            launch {
                delay(100)
                val promise2 = orchestrator.callSessionToProcessOrCreate(TestEvent("321"), TestBot("321"))
                println("promise2 with state: ${promise2.session.state}")
                assertTrue(promise2.session.state is SessionState.WaitingFor)
            }
            launch {
                delay(200)
                val promise3 = orchestrator.callSessionToProcessOrCreate(TestEvent("Matched"), TestBot("321"))
                println("with state: ${promise3.session.state}")
            }
            promise1.deferred.await()

            delay(500)
            if (promise1.deferred.isActive) {
                println("promise is still active!!")
                assert(false)
            }

            assert(promise1.deferred.isCompleted)
            if (promise1.session.state is SessionState.Finished) {
                println("Reply: ${(promise1.session.state as SessionState.Finished).reply}")

                assert(true)
            } else {
                assert(false)
            }
        }
    }

    @Test
    fun `test for event matching`() {
        runBlocking {
            val orchestrator =
                getKoin().get<ConversationOrchestratorFactory>().create(
                    conversation {
                        setProcessor(::waitForEventProcessor)
                    },
                )
            // Non-Matched Event before call
            val promise0 = orchestrator.callSessionToProcessOrCreate(AnotherTestEvent("456"), TestBot("321"))
            val promise1 = orchestrator.callSessionToProcessOrCreate(TestEvent("123"), TestBot("123"))
            println(promise1.session.state)
            delay(100)

            val promise2 = orchestrator.callSessionToProcessOrCreate(AnotherTestEvent("456"), TestBot("321"))

            assert(
                promise2.session.state is SessionState.WaitingFor &&
                    (promise2.session.state as SessionState.WaitingFor).item is SessionState.WaitingFor.Item.EventItem &&
                    ((promise2.session.state as SessionState.WaitingFor).item as SessionState.WaitingFor.Item.EventItem).eventClass ==
                    TestEvent::class,
            ) {
                "wtf session is waiting for: ${promise2.session.state}[${(promise2.session.state as SessionState.WaitingFor).item}]"
            }
            assert(promise2.deferred.isActive) { "promise is still active!!" }
            println("with status: ${promise2.session.state}")

            delay(100)
            val promise3 = orchestrator.callSessionToProcessOrCreate(TestEvent("321"), TestBot("321"))

            println("with status: ${promise3.session.state}")

            launch {
                delay(200)
                if (promise3.deferred.isActive) {
                    println("promise is still active!!")
                    assert(false)
                }
            }

            promise3.deferred.await()
            assert(promise3.deferred.isCompleted)
            if (promise3.session.state is SessionState.Finished) {
                println("Reply: ${(promise3.session.state as SessionState.Finished).reply}")
                assert(true)
            } else {
                assert(false)
            }
        }
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun `start koin`() {
            startKoin { modules(allModules) }
        }

        @AfterAll
        @JvmStatic
        fun `stop koin`() {
            stopKoin()
        }
    }
}
