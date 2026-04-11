// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.adapterApi.utils

import dev.kokoroidkt.adapterApi.adapter.Adapter
import dev.kokoroidkt.adapterApi.adapter.AdapterContainer
import dev.kokoroidkt.adapterApi.adapter.AdapterMeta
import dev.kokoroidkt.adapterApi.adapter.AdapterRegistry
import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.config.kokoroidConfigRoot
import dev.kokoroidkt.coreApi.user.UserContainer
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.nio.file.Files
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists

@Serializable
data class TestConfigData(
    val foo: String,
    val bar: Int,
)

class MockAdapter : Adapter {
    override fun onLoad() {}

    override fun onStart() {}

    override fun onStop() {}

    override fun onUnload() {}

    override fun getBot(botId: String): Bot = throw NotImplementedError()

    override fun getBotList(): List<Bot> = emptyList()

    override fun getUserContainer(): UserContainer = throw NotImplementedError()
}

class MockAdapterRegistry(
    private val adapter: Adapter,
    private val meta: AdapterMeta,
) : AdapterRegistry {
    private val container = AdapterContainer(adapter, meta)

    override fun get(adapterId: String): AdapterContainer? = if (adapterId == container.adapterId) container else null

    override fun getAdapterId(adapterClass: Class<*>): String? = if (adapterClass == adapter::class.java) container.adapterId else null
}

class TestAdapterExtensions {
    private val testMeta =
        AdapterMeta(
            name = "TestAdapter",
            version = "1.0.0",
            mainClass = "dev.kokoroidkt.adapterApi.utils.MockAdapter",
            authors = listOf("test"),
            description = "test",
            website = "test",
        )
    private val adapter = MockAdapter()

    @BeforeEach
    fun setup() {
        startKoin {
            modules(
                module {
                    single<AdapterRegistry> { MockAdapterRegistry(adapter, testMeta) }
                },
            )
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        val configPath = kokoroidConfigRoot.resolve("adapter/TestAdapter/settings.conf")
        configPath.deleteIfExists()
    }

    @Test
    fun testAdapterSaveAndLoadConfig() {
        val config = TestConfigData("hello", 123)
        adapter.saveConfigToFile(config)

        val loadedConfig = adapter.loadConfigFromFile<TestConfigData>()
        assertEquals(config, loadedConfig)
    }
}
