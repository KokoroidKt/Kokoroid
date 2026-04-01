// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.conversation.extension

import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.event.MessageEvent
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.message.MessageSegment
import dev.kokoroidkt.coreApi.message.TextConvertible
import dev.kokoroidkt.coreApi.user.Users
import dev.kokoroidkt.coreApi.user.special.NoUser
import dev.kokoroidkt.pluginApi.conversation.Reply
import dev.kokoroidkt.pluginApi.conversation.extensions.waitForMessage
import dev.kokoroidkt.pluginApi.dsl.conversation
import dev.kokoroidkt.pluginApi.dsl.rule
import dev.kokoroidkt.pluginApi.factory.ConversationOrchestratorFactory
import dev.kokoroidkt.pluginApi.session.SessionState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.mp.KoinPlatform.getKoin
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertTrue

class TestMessageEvent(
    eventId: String,
    override val messageChain: MessageChain,
    users: Users = NoUser.NO_USER_GROUP,
) : Event(eventId, Instant.now(), users, TestBot("")),
    MessageEvent {
    fun sayHi() {
        println("hi from message event")
    }

    fun getCheckId(): String = "testMessageEvent$eventId"
}

class AnotherMessageEvent(
    eventId: String,
    override val messageChain: MessageChain,
    users: Users = NoUser.NO_USER_GROUP,
) : Event(eventId, Instant.now(), users, TestBot("")),
    MessageEvent {
    fun sayHi() {
        println("hi from another message event")
    }

    fun getCheckId(): String = "anotherMessageEvent$eventId"
}

suspend fun waitForMessageProcessor(event: TestMessageEvent): Reply {
    event.sayHi()
    println(event.getCheckId())
    val message = waitForMessage()
    println("Received message: $message")
    return Reply.NoReply
}

suspend fun waitForMessageWithRuleProcessor(event1: TestMessageEvent): Reply {
    event1.sayHi()
    println(event1.getCheckId())
    val message =
        waitForMessage(
            rules =
                rule { with { event.eventId == "Matched" } },
        )
    delay(100)
    println("Received message: $message")
    return Reply.NoReply
}

suspend fun waitForMessageWithUserGroupProcessor(event: TestMessageEvent): Reply {
    event.sayHi()
    println(event.getCheckId())
    val message = waitForMessage(event.users)
    println("Received message with user group: $message")
    return Reply.NoReply
}

suspend fun waitForMessageWithTimeoutProcessor(event: TestMessageEvent): Reply {
    event.sayHi()
    println(event.getCheckId())
    try {
        val message = waitForMessage(timeoutMilli = 50)
        println("Received message with timeout: $message")
    } catch (e: Exception) {
        println("Timeout exception caught: ${e.message}")
    }
    return Reply.NoReply
}

class TestWaitForMessage {
    @Test
    fun `test wait for message`() {
        runBlocking {
            val orchestrator =
                getKoin().get<ConversationOrchestratorFactory>().create(
                    conversation { setProcessor(::waitForMessageProcessor) },
                )
            val promise1 =
                orchestrator.callSessionToProcessOrCreate(
                    TestMessageEvent("123", MessageChain.empty()),
                    TestBot("123"),
                )
            println(promise1.session.state)
            launch {
                delay(100)
                val promise2 =
                    orchestrator.callSessionToProcessOrCreate(
                        TestMessageEvent(
                            "321",
                            MessageChain.of(
                                object : MessageSegment(), TextConvertible {
                                    override fun toPlainText(): String = "Hello World"
                                },
                            ),
                        ),
                        TestBot("321"),
                    )

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
    fun `test wait for message with rule`() {
        runBlocking {
            val orchestrator =
                getKoin().get<ConversationOrchestratorFactory>().create(
                    conversation { setProcessor(::waitForMessageWithRuleProcessor) },
                )
            val promise1 =
                orchestrator.callSessionToProcessOrCreate(
                    TestMessageEvent("123", MessageChain.empty()),
                    TestBot("123"),
                )
            println(promise1.session.state)
            launch {
                delay(100)
                val promise2 =
                    orchestrator.callSessionToProcessOrCreate(
                        TestMessageEvent(
                            "Mqweqwdascd",
                            MessageChain.of(
                                object : MessageSegment(), TextConvertible {
                                    override fun toPlainText(): String = "Hello World"
                                },
                            ),
                        ),
                        TestBot("321"),
                    )

                println("promise2 with status: ${promise2.session.state}")
                assertTrue(promise2.session.state is SessionState.WaitingFor)
            }
            launch {
                delay(300)
                val promise3 =
                    orchestrator.callSessionToProcessOrCreate(
                        TestMessageEvent(
                            "Matched",
                            MessageChain.of(
                                object : MessageSegment(), TextConvertible {
                                    override fun toPlainText(): String = "Hello World"
                                },
                            ),
                        ),
                        TestBot("321"),
                    )

                println("with status: ${promise3.session.state}")
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
    fun `test wait for message with user group`() {
        runBlocking {
            val orchestrator =
                getKoin().get<ConversationOrchestratorFactory>().create(
                    conversation {
                        setProcessor(::waitForMessageWithUserGroupProcessor)
                    },
                )
            val promise1 =
                orchestrator.callSessionToProcessOrCreate(
                    TestMessageEvent("123", MessageChain.empty()),
                    TestBot("123"),
                )
            println(promise1.session.state)
            launch {
                delay(100)
                val promise2 =
                    orchestrator.callSessionToProcessOrCreate(
                        TestMessageEvent(
                            "321",
                            MessageChain.of(
                                object : MessageSegment(), TextConvertible {
                                    override fun toPlainText(): String = "User Group Test"
                                },
                            ),
                        ),
                        TestBot("321"),
                    )

                promise2.deferred.await()
                assert(promise2.deferred.isCompleted)
                if (promise1.session.state is SessionState.Finished) {
                    println("Reply: ${(promise1.session.state as SessionState.Finished).reply}")
                    assert(true)
                } else {
                    assert(false)
                }
            }
            promise1.deferred.await()
        }
    }

    @Test
    fun `test wait for message timeout`() {
        runBlocking {
            val orchestrator =
                getKoin().get<ConversationOrchestratorFactory>().create(
                    conversation {
                        setProcessor(::waitForMessageWithTimeoutProcessor)
                    },
                )
            val promise1 =
                orchestrator.callSessionToProcessOrCreate(
                    TestMessageEvent("123", MessageChain.empty()),
                    TestBot("123"),
                )
            println(promise1.session.state)

            delay(200) // 等待超时发生

            if (promise1.deferred.isCompleted) {
                println("Promise completed after timeout")
                assert(true)
            } else {
                println("Promise still active after timeout")
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
