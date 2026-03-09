package dev.kokoroidkt.core.conversation

import dev.kokoroidkt.core.conversation.extension.TestBot
import dev.kokoroidkt.core.conversation.extension.TestEvent
import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.message.MessageSegment
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.coreApi.user.special.NoUser
import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.conversation.Reply
import dev.kokoroidkt.pluginApi.conversation.extensions.waitForEvent
import dev.kokoroidkt.pluginApi.dsl.conversation
import dev.kokoroidkt.pluginApi.factory.ConversationOrchestratorFactory
import dev.kokoroidkt.pluginApi.plugin.Plugin
import dev.kokoroidkt.pluginApi.session.SessionState
import dev.kokoroidkt.pluginApi.task.BackgroundTask
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

class TestEvent(
    eventId: String,
    users: UserGroup = NoUser.NO_USER_GROUP,
) : Event(eventId, Instant.now(), users, TestBot()) {
    fun sayHi() {
        println("hi")
    }

    fun getCheckId(): String = "testEvent$eventId"
}

suspend fun replyWithNoRepy(event: TestEvent) {
    println("hi! from no reply")
    waitForEvent<TestEvent>()
    println("Bye from no reply")
}

suspend fun replyWithMessageChain(event: TestEvent): MessageChain {
    println("hi! from no reply")
    waitForEvent<TestEvent>()
    println("Bye from no reply")
    return MessageChain.of(
        object : MessageSegment() {
            override val isTextConvertible: Boolean
                get() = false
        },
    )
}

suspend fun replyWithBackgroundTask(event: TestEvent): BackgroundTask {
    println("hi! from no reply")
    waitForEvent<TestEvent>()
    println("Bye from no reply")
    return { println("executed!") }
}

suspend fun replyWithReply(event: TestEvent): Reply {
    println("hi! from no reply")
    waitForEvent<TestEvent>()
    println("Bye from no reply")
    return Reply.NoReply
}

suspend fun ohNo(event: TestEvent): Plugin? {
    println("ohno")
    assert(false)
    return null
}

class TestProcessor {
    @Test
    fun `test failed`() {
        runBlocking {
            try {
                val orchestrator = getKoin().get<ConversationOrchestratorFactory>().create(conversation { setProcessor(::ohNo) })
                val promise1 = orchestrator.callSessionToProcessOrCreate(TestEvent("123"), TestBot("123"))
            } catch (_: IllegalArgumentException) {
                println("catched!")
                assert(true)
                return@runBlocking
            }
            assert(false)
        }
    }

    @Test
    fun `test process`() {
        for (funcPair in listOf(
            ::replyWithReply to Reply.NoReply::class,
            ::replyWithNoRepy to Reply.NoReply::class,
            ::replyWithMessageChain to Reply.MessageChainReply::class,
            ::replyWithBackgroundTask to Reply.BackgroundTaskReply::class,
        )) {
            // val checkList = mutableListOf<Int>()
            runBlocking {
                val orchestrator = getKoin().get<ConversationOrchestratorFactory>().create(conversation { setProcessor(funcPair.first) })
                val promise1 = orchestrator.callSessionToProcessOrCreate(TestEvent("123"), TestBot("123"))
                println(promise1.session.state)
                launch {
                    delay(100)
                    val promise2 = orchestrator.callSessionToProcessOrCreate(TestEvent("321"), TestBot("321"))
                    // promise2.deferred.await()

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

                    val reply = (promise1.session.state as SessionState.Finished).reply
                    assert(funcPair.second.isInstance(reply))
                } else {
                    assert(false)
                }
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
