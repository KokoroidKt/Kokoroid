// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.core.conversation

import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.MessageEvent
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.message.builtin.TextMessage
import dev.kokoroidkt.pluginApi.conversation.Reply
import dev.kokoroidkt.pluginApi.conversation.command.CommandArg
import dev.kokoroidkt.pluginApi.conversation.status.ProcessorStatus
import dev.kokoroidkt.pluginApi.dsl.command
import dev.kokoroidkt.pluginApi.session.Session
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import java.time.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CommandDslTest {
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

    // 定义处理器函数
    suspend fun rootHandler(event: MessageEvent): Reply {
        println("rootHandler called with ${event.messageChain.toPlainText()}")
        return Reply.NoReply
    }

    suspend fun level1Handler(args: List<CommandArg>): Reply {
        println("level1Handler called with args: $args")
        return Reply.NoReply
    }

    suspend fun level2Handler(
        bot: Bot,
        event: MessageEvent,
    ): Reply {
        println("level2Handler called by bot ${bot.botId}")
        return Reply.NoReply
    }

    suspend fun level3Handler(chain: MessageChain): Reply {
        println("level3Handler called with chain: ${chain.toPlainText()}")
        return Reply.NoReply
    }

    @Test
    fun `test complex command tree with DSL`() =
        runBlocking {
            try {
                // 构建 4 层命令树
                // /app
                //   /app settings
                //     /app settings network
                //       /app settings network proxy
                //   /app help
                val processor =
                    command("app", '/') {
                        setProcessor(::rootHandler)
                        child("settings") {
                            setProcessor(::level1Handler)
                            child("network") {
                                setProcessor(::level2Handler)
                                child("proxy") {
                                    setProcessor(::level3Handler)
                                }
                            }
                        }
                        child("help") {
                            setProcessor(::level1Handler)
                        }
                    }

                val session = mockk<Session>(relaxed = true)
                val bot = MockBot("test-bot")
                val user = MockUser("user-1")

                // 1. 测试叶子节点 /app help
                println("--- Test 1: /app help ---")
                val event1 = MockMessageEvent(MessageChain.of(TextMessage("/app help")), listOf(user), bot)
                val status1 = processor.tryCallSuspend(event1, bot, listOf(user), session)
                assertTrue(status1 is ProcessorStatus.Processed)
                assertEquals("help", processor.foundedCommandItem?.thisKeyword)

                // 2. 测试深层叶子节点 /app settings network proxy
                println("--- Test 2: /app settings network proxy ---")
                val event2 =
                    MockMessageEvent(MessageChain.of(TextMessage("/app settings network proxy")), listOf(user), bot)
                val status2 = processor.tryCallSuspend(event2, bot, listOf(user), session)
                assertTrue(status2 is ProcessorStatus.Processed)
                assertEquals("proxy", processor.foundedCommandItem?.thisKeyword)

                // 3. 测试带参数的子命令 /app help info
                println("--- Test 3: /app help info ---")
                val event3 = MockMessageEvent(MessageChain.of(TextMessage("/app help info")), listOf(user), bot)
                val status3 = processor.tryCallSuspend(event3, bot, listOf(user), session)
                assertTrue(status3 is ProcessorStatus.Processed)
                assertEquals("help", processor.foundedCommandItem?.thisKeyword)

                // 4. 测试不匹配的情况
                println("--- Test 4: /unknown ---")
                val event4 = MockMessageEvent(MessageChain.of(TextMessage("/unknown")), listOf(user), bot)
                val status4 = processor.tryCallSuspend(event4, bot, listOf(user), session)
                assertTrue(status4 is ProcessorStatus.Unmatched)

                // 5. 测试前缀不匹配
                println("--- Test 5: app help (no prefix) ---")
                val event5 = MockMessageEvent(MessageChain.of(TextMessage("app help")), listOf(user), bot)
                val status5 = processor.tryCallSuspend(event5, bot, listOf(user), session)
                assertTrue(status5 is ProcessorStatus.Unmatched)

                // 6. 测试根节点在有子节点时是否能被触发 (预期：/app)
                println("--- Test 6: /app ---")
                val event6 = MockMessageEvent(MessageChain.of(TextMessage("/app")), listOf(user), bot)
                val status6 = processor.tryCallSuspend(event6, bot, listOf(user), session)
                println("Status6: $status6")
                // 由于 goDown = true，它会试图找下一个参数，但没有了，所以会返回 Unmatched。
                // 这说明带子命令的节点确实不能作为独立命令执行。
                assertTrue(status6 is ProcessorStatus.Unmatched)

                // 7. 测试 5 层深度的路径
                // /app settings network proxy auth token
                val deepProcessor =
                    command("app", '/') {
                        setProcessor(::rootHandler)
                        child("settings") {
                            setProcessor(::level1Handler)
                            child("network") {
                                setProcessor(::level2Handler)
                                child("proxy") {
                                    setProcessor(::level3Handler)
                                    child("auth") {
                                        setProcessor(::level1Handler)
                                        child("token") {
                                            setProcessor(::level2Handler)
                                        }
                                    }
                                }
                            }
                        }
                    }
                println("--- Test 7: Deep tree 5 levels ---")
                val event7 =
                    MockMessageEvent(
                        MessageChain.of(TextMessage("/app settings network proxy auth token")),
                        listOf(user),
                        bot,
                    )
                val status7 = deepProcessor.tryCallSuspend(event7, bot, listOf(user), session)
                assertTrue(status7 is ProcessorStatus.Processed)
                assertEquals("token", deepProcessor.foundedCommandItem?.thisKeyword)

                // 8. 测试带有规则的 DSL
                println("--- Test 8: DSL with addRule ---")
                val ruleProcessor =
                    command("restricted", '/') {
                        addRule {
                            with {
                                // 只有 eventId 为 "Pass" 的事件才允许
                                event.eventId == "Pass"
                            }
                        }
                        setProcessor(::rootHandler)
                    }

                val failEvent = MockMessageEvent(MessageChain.of(TextMessage("/restricted")), listOf(user), bot)
                val failStatus = ruleProcessor.tryCallSuspend(failEvent, bot, listOf(user), session)
                assertTrue(failStatus is ProcessorStatus.Unmatched)

                val passEvent =
                    object : MessageEvent("Pass", Instant.now(), listOf(user), bot) {
                        override val messageChain = MessageChain.of(TextMessage("/restricted"))
                    }
                val passStatus = ruleProcessor.tryCallSuspend(passEvent, bot, listOf(user), session)
                assertTrue(passStatus is ProcessorStatus.Processed)
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
}
