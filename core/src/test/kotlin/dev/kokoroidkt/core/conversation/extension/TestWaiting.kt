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
import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.conversation.Reply
import dev.kokoroidkt.pluginApi.conversation.extensions.waitForEvent
import dev.kokoroidkt.pluginApi.factory.ConversationOrchestratorFactory
import dev.kokoroidkt.pluginApi.session.SessionStatus
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
            val orchestrator = getKoin().get<ConversationOrchestratorFactory>().create(Processor(::finishWithoutWait))
            val promise1 = orchestrator.callSessionToProcessOrCreate(TestEvent("123"), TestBot("123"))
            println(promise1.session.status)
            assertFalse(orchestrator.isExist(promise1.session))
            promise1.deferred.await()
            println((promise1.session.status as SessionStatus.Finished).reply)
        }
    }

    @Test
    fun `test wait for event`() {
        val checkList = mutableListOf<Int>()
        runBlocking {
            val orchestrator = getKoin().get<ConversationOrchestratorFactory>().create(Processor(::waitForEventProcessor))
            val promise1 = orchestrator.callSessionToProcessOrCreate(TestEvent("123"), TestBot("123"))
            println(promise1.session.status)
            launch {
                delay(100)
                val promise2 = orchestrator.callSessionToProcessOrCreate(TestEvent("321"), TestBot("321"))
                checkList.add(1)
                assertEquals(promise1, promise2)
                println("with status: ${promise2.session.status}")
            }
            promise1.deferred.await()
            checkList.add(2)
            delay(200)
            if (promise1.deferred.isActive) {
                println("promise is still active!!")
                assert(false)
            }

            assert(promise1.deferred.isCompleted)
            if (promise1.session.status is SessionStatus.Finished) {
                println("Reply: ${(promise1.session.status as SessionStatus.Finished).reply}")
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
    fun `test for event matching`() {
        runBlocking {
            val orchestrator = getKoin().get<ConversationOrchestratorFactory>().create(Processor(::waitForEventProcessor))
            val promise1 = orchestrator.callSessionToProcessOrCreate(TestEvent("123"), TestBot("123"))
            println(promise1.session.status)
            delay(100)

            val promise2 = orchestrator.callSessionToProcessOrCreate(AnotherTestEvent("456"), TestBot("321"))
            assertEquals(promise1, promise2)
            assert(
                promise2.session.status is SessionStatus.WaitingFor &&
                    (promise2.session.status as SessionStatus.WaitingFor).item is SessionStatus.WaitingFor.Item.EventItem &&
                    ((promise2.session.status as SessionStatus.WaitingFor).item as SessionStatus.WaitingFor.Item.EventItem).eventClass ==
                    TestEvent::class,
            ) {
                "wtf session is waiting for: ${promise2.session.status}[${(promise2.session.status as SessionStatus.WaitingFor).item}]"
            }
            assert(promise2.deferred.isActive) { "promise is still active!!" }
            println("with status: ${promise2.session.status}")

            delay(100)
            val promise3 = orchestrator.callSessionToProcessOrCreate(TestEvent("321"), TestBot("321"))
            assertEquals(promise1, promise2)
            assertEquals(promise1, promise3)
            println("with status: ${promise3.session.status}")

            launch {
                delay(200)
                if (promise3.deferred.isActive) {
                    println("promise is still active!!")
                    assert(false)
                }
            }

            promise3.deferred.await()
            assert(promise3.deferred.isCompleted)
            if (promise3.session.status is SessionStatus.Finished) {
                println("Reply: ${(promise3.session.status as SessionStatus.Finished).reply}")
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
