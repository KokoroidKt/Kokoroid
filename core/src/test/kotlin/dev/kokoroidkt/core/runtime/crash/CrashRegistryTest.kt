/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.runtime.crash

import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.core.runtime.status.InternalStatus
import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.exceptions.CriticalException
import dev.kokoroidkt.coreApi.message.MessageChain
import kotlinx.serialization.json.JsonElement
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.GlobalContext
import org.koin.core.error.KoinApplicationAlreadyStartedException
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
 * Tests for the [CrashRegistry] interface and its [CrashRegistryImpl] implementation.
 *
 * The focus of the tests is on the `recordAndRequestStop` method, which records a crash event
 * and optionally requests Kokoroid to stop depending on the internal state.
 */
class CrashRegistryTest {
    private lateinit var crashRegistry: CrashRegistryImpl

    @BeforeEach
    fun setUp() {
        crashRegistry = CrashRegistryImpl()
    }

    @Test
    fun `test if method records a crash and changes the status to stopping`() {
        val exception = CriticalException("Critical crash!")
        val event: Event? = null

        crashRegistry.recordAndRequestStop(exception, event)

        Assertions.assertTrue(crashRegistry.isCrashed, "Registry should indicate a crash has occurred")
        Assertions.assertEquals(1, crashRegistry.records.size, "The crash record should be added to the registry")
        Assertions.assertEquals(
            exception,
            crashRegistry.records[0].err,
            "The recorded exception should match the input",
        )
        Assertions.assertNull(
            crashRegistry.records[0].event,
            "The recorded event should be null when no event is passed",
        )
        Assertions.assertTrue(
            crashRegistry.records[0].kokoroidStatus is InternalStatus.BeforeStopping,
            "The Kokoroid status should change to WaitForStopping",
        )
    }

    @Test
    fun `test if method does not change status or log stopping again after the first stop`() {
        val exception1 = CriticalException("First crash!")
        val exception2 = CriticalException("Second crash!")
        val event: Event? = null

        crashRegistry.recordAndRequestStop(exception1, event)
        crashRegistry.recordAndRequestStop(exception2, event)

        Assertions.assertTrue(crashRegistry.isCrashed, "Registry should indicate a crash after multiple crashes")
        Assertions.assertEquals(2, crashRegistry.records.size, "Both crash records should be added to the registry")
        Assertions.assertEquals(
            exception1,
            crashRegistry.records[0].err,
            "The first exception should be recorded first",
        )
        Assertions.assertEquals(
            exception2,
            crashRegistry.records[1].err,
            "The second exception should be recorded second",
        )
    }

    @Test
    fun `test if method handles and records null event properly`() {
        val exception = CriticalException("Critical crash!")
        val event: Event? = null

        crashRegistry.recordAndRequestStop(exception, event)

        Assertions.assertTrue(crashRegistry.isCrashed, "Registry should indicate a crash has occurred")
        Assertions.assertNotNull(crashRegistry.records[0], "Crash record should exist")
        Assertions.assertEquals(
            exception,
            crashRegistry.records[0].err,
            "The recorded exception should match the input",
        )
        Assertions.assertNull(
            crashRegistry.records[0].event,
            "The recorded event should be null when no event is passed",
        )
    }

    @Test
    fun `test if method records crash with event properly`() {
        val exception = CriticalException("Crash with event!")
        val event = object : Event("testEvent", Instant.now(), listOf(), TestBot("")) {}

        crashRegistry.recordAndRequestStop(exception, event)

        Assertions.assertTrue(crashRegistry.isCrashed, "Registry should indicate a crash has occurred")
        Assertions.assertEquals(1, crashRegistry.records.size, "Exactly one crash should be recorded")
        Assertions.assertEquals(
            exception,
            crashRegistry.records[0].err,
            "The recorded exception should match the input",
        )
        Assertions.assertEquals(
            event,
            crashRegistry.records[0].event,
            "The recorded event should match the passed event",
        )
    }

    @Test
    fun `test if no crash is recorded when an empty implementation does nothing`() {
        val mockCrashRegistry =
            object : CrashRegistry {
                override fun recordAndRequestStop(
                    err: CriticalException,
                    event: Event?,
                ) {}

                override val isCrashed: Boolean = false

                override fun logRecords() {
                }
            }

        mockCrashRegistry.recordAndRequestStop(CriticalException("No-op"), null)

        Assertions.assertFalse(mockCrashRegistry.isCrashed, "No-op registry should not indicate any crash occurred")
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun `set up koin`() {
            // 创建测试用的模块
            try {
                GlobalContext.startKoin {
                    modules(allModules)
                }
            } catch (_: KoinApplicationAlreadyStartedException) {
            }
        }

        @JvmStatic
        @AfterAll
        fun `tear down koin`() {
            GlobalContext.stopKoin()
        }
    }
}
