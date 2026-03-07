/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.conversation.extension

import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.event.MessageEvent
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.message.MessageSegment
import dev.kokoroidkt.coreApi.message.TextConvertible
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.coreApi.user.special.NoUser
import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.conversation.Reply
import dev.kokoroidkt.pluginApi.conversation.extensions.waitForMessage
import dev.kokoroidkt.pluginApi.factory.ConversationOrchestratorFactory
import dev.kokoroidkt.pluginApi.session.SessionState
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
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class TestMessageEvent(
    eventId: String,
    override val messageChain: MessageChain,
    users: UserGroup = NoUser.NO_USER_GROUP,
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
    users: UserGroup = NoUser.NO_USER_GROUP,
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
        val checkList = mutableListOf<Int>()
        runBlocking {
            val orchestrator = getKoin().get<ConversationOrchestratorFactory>().create(Processor(::waitForMessageProcessor))
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
                                    override val isTextConvertible: Boolean
                                        get() = true

                                    override fun toPlainText(): String = "Hello World"
                                },
                            ),
                        ),
                        TestBot("321"),
                    )
                checkList.add(1)
                assertEquals(promise1, promise2)
                println("with status: ${promise2.session.state}")
            }
            promise1.deferred.await()
            checkList.add(2)
            delay(200)
            if (promise1.deferred.isActive) {
                println("promise is still active!!")
                assert(false)
            }

            assert(promise1.deferred.isCompleted)
            if (promise1.session.state is SessionState.Finished) {
                println("Reply: ${(promise1.session.state as SessionState.Finished).reply}")
                checkList.add(3)
                assert(checkList[0] == 1)
                assert(checkList[1] == 2)
                assert(checkList[2] == 3)
                assert(true)
            } else {
                assert(false)
            }
        }
    }

    @Test
    fun `test wait for message with user group`() {
        runBlocking {
            val orchestrator = getKoin().get<ConversationOrchestratorFactory>().create(Processor(::waitForMessageWithUserGroupProcessor))
            val promise1 =
                orchestrator.callSessionToProcessOrCreate(
                    TestMessageEvent("123", MessageChain.empty()),
                    TestBot("123"),
                )
            println(promise1.session.state)

            delay(100)
            val promise2 =
                orchestrator.callSessionToProcessOrCreate(
                    TestMessageEvent(
                        "321",
                        MessageChain.of(
                            object : MessageSegment(), TextConvertible {
                                override val isTextConvertible: Boolean
                                    get() = true

                                override fun toPlainText(): String = "User Group Test"
                            },
                        ),
                    ),
                    TestBot("321"),
                )
            assertEquals(promise1, promise2)

            promise2.deferred.await()
            assert(promise2.deferred.isCompleted)

            if (promise2.session.state is SessionState.Finished) {
                println("Reply: ${(promise2.session.state as SessionState.Finished).reply}")
                assert(true)
            } else {
                assert(false)
            }
        }
    }

    @Test
    fun `test wait for message timeout`() {
        runBlocking {
            val orchestrator = getKoin().get<ConversationOrchestratorFactory>().create(Processor(::waitForMessageWithTimeoutProcessor))
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
