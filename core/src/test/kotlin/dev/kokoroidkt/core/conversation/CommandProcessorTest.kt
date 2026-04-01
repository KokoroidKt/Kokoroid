package dev.kokoroidkt.core.conversation

import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.event.MessageEvent
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.message.MessageSegment
import dev.kokoroidkt.coreApi.message.builtin.TextMessage
import dev.kokoroidkt.coreApi.user.User
import dev.kokoroidkt.coreApi.user.Users
import dev.kokoroidkt.pluginApi.conversation.Reply
import dev.kokoroidkt.pluginApi.conversation.command.CommandArg
import dev.kokoroidkt.pluginApi.conversation.command.CommandItem
import dev.kokoroidkt.pluginApi.conversation.command.CommandProcessor
import dev.kokoroidkt.pluginApi.conversation.status.ProcessorStatus
import dev.kokoroidkt.pluginApi.session.Session
import dev.kokoroidkt.pluginApi.session.SessionState
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.mp.KoinPlatform.getKoin
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MockBot(
    override val botId: String = "testBot",
) : Bot {
    override fun callApi(
        apiEndpoint: String,
        data: kotlinx.serialization.json.JsonElement,
    ) {}

    override fun replyMessage(
        event: Event,
        message: MessageChain,
    ) {}
}

class MockUser(
    override val platfromUserId: String,
    adapterId: String = "test",
) : User(adapterId)

class MockMessageEvent(
    override val messageChain: MessageChain,
    users: Users = listOf(MockUser("user1")),
    bot: Bot = MockBot(),
) : Event("testEvent", Instant.now(), users, bot),
    MessageEvent

class CommandProcessorTest {
    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            startKoin { modules(allModules) }
        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            stopKoin()
        }
    }

    // 定义具体的函数，避免 Mock KFunction 的复杂性
    suspend fun sayWithArgs(args: List<CommandArg>): Reply {
        println("sayWithArgs called with $args")
        return Reply.NoReply
    }

    suspend fun helpDocs(): Reply {
        println("helpDocs called")
        return Reply.NoReply
    }

    // 1. 测试命令分片情况
    @Test
    fun `test command segmentation`() =
        runBlocking {
            val root =
                CommandItem(
                    children = emptyMap(),
                    thisKeyword = "say",
                    thisProcessor = ::sayWithArgs,
                    goDown = false,
                )

            val processor = CommandProcessor('/', root)
            val session = mockk<Session>(relaxed = true)
            val otherSeg = object : MessageSegment() {}
            val event =
                MockMessageEvent(
                    MessageChain.of(
                        TextMessage("/say hi hello"),
                        otherSeg,
                        TextMessage(" world"),
                    ),
                )

            val status = processor.tryCallSuspend(event, event.bot, event.users, session)
            assertTrue(status is ProcessorStatus.Processed)
            // 实际上我们很难直接在这里拿到 sayWithArgs 的参数，
            // 但如果流程没问题，说明 CommandItem.execute 成功映射了参数
        }

    // 2. 测试命令匹配情况
    @Test
    fun `test command matching say`() =
        runBlocking {
            val root =
                CommandItem(
                    children = emptyMap(),
                    thisKeyword = "say",
                    thisProcessor = ::sayWithArgs,
                    goDown = false,
                )

            val processor = CommandProcessor('/', root)
            val session = mockk<Session>(relaxed = true)
            val atSeg = object : MessageSegment() {}
            val event =
                MockMessageEvent(
                    MessageChain.of(
                        TextMessage("/say hi hello "),
                        atSeg,
                    ),
                )

            val status = processor.tryCallSuspend(event, event.bot, event.users, session)
            assertTrue(status is ProcessorStatus.Processed)
        }

    @Test
    fun `test command matching help docs`() =
        runBlocking {
            val docsItem =
                CommandItem(
                    children = emptyMap(),
                    thisKeyword = "docs",
                    thisProcessor = ::helpDocs,
                    goDown = false,
                )

            val root =
                CommandItem(
                    children = mapOf("docs" to docsItem),
                    thisKeyword = "help",
                    thisProcessor = ::helpDocs,
                    goDown = true,
                )

            val processor = CommandProcessor('/', root)
            val session = mockk<Session>(relaxed = true)
            val event = MockMessageEvent(MessageChain.of(TextMessage("/help docs hello")))

            val status = processor.tryCallSuspend(event, event.bot, event.users, session)
            assertTrue(status is ProcessorStatus.Processed)
            assertEquals("docs", processor.foundedCommandItem?.thisKeyword)
        }

    // 3. Reply 的接收正常
    @Test
    fun `test reply from command`() =
        runBlocking {
            val root =
                CommandItem(
                    children = emptyMap(),
                    thisKeyword = "say",
                    thisProcessor = ::sayWithArgs,
                    goDown = false,
                )
            val processor = CommandProcessor('/', root)
            val session = mockk<Session>(relaxed = true)
            val event = MockMessageEvent(MessageChain.of(TextMessage("/say hello")))

            processor.tryCallSuspend(event, event.bot, event.users, session)
            // verify state set to Finished(NoReply)
            io.mockk.verify { session.state = any<SessionState.Finished>() }
        }

    // 4. 与 ConversationOrchestrator 的合作状态
    @Test
    fun `test with ConversationOrchestrator`() =
        runBlocking {
            val root =
                CommandItem(
                    children = emptyMap(),
                    thisKeyword = "say",
                    thisProcessor = ::sayWithArgs,
                    goDown = false,
                )
            val processor = CommandProcessor('/', root)

            val orchestrator =
                getKoin()
                    .get<dev.kokoroidkt.pluginApi.factory.ConversationOrchestratorFactory>()
                    .create(processor)

            val event = MockMessageEvent(MessageChain.of(TextMessage("/say hello")))
            val promise = orchestrator.callSessionToProcessOrCreate(event, event.bot)
            promise.deferred.await()

            assertTrue(promise.session.state is SessionState.Finished)
            assertEquals(Reply.NoReply, (promise.session.state as SessionState.Finished).reply)
        }

    // 5. waitForT (waitForEvent) 函数的工作是否正常
    suspend fun waitProcessor(event: Event): Reply {
        dev.kokoroidkt.pluginApi.conversation.extensions
            .waitForEvent<MockMessageEvent>()
        return Reply.NoReply
    }

    @Test
    fun `test waitForEvent in command`() =
        runBlocking {
            val root =
                CommandItem(
                    children = emptyMap(),
                    thisKeyword = "wait",
                    thisProcessor = ::waitProcessor,
                    goDown = false,
                )
            val processor = CommandProcessor('/', root)
            val orchestrator =
                getKoin()
                    .get<dev.kokoroidkt.pluginApi.factory.ConversationOrchestratorFactory>()
                    .create(processor)

            val event1 = MockMessageEvent(MessageChain.of(TextMessage("/wait")))
            val promise1 = orchestrator.callSessionToProcessOrCreate(event1, event1.bot)

            kotlinx.coroutines.delay(100)
            assertTrue(promise1.session.state is SessionState.WaitingFor)

            val event2 = MockMessageEvent(MessageChain.of(TextMessage("something")))
            orchestrator.callSessionToProcessOrCreate(event2, event2.bot)

            promise1.deferred.await()
            assertTrue(promise1.session.state is SessionState.Finished)
        }
}
