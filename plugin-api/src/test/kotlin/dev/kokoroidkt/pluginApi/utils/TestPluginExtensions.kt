// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.utils

import dev.kokoroidkt.coreApi.config.kokoroidConfigRoot
import dev.kokoroidkt.pluginApi.plugin.Plugin
import dev.kokoroidkt.pluginApi.plugin.PluginContainer
import dev.kokoroidkt.pluginApi.plugin.PluginMeta
import dev.kokoroidkt.pluginApi.plugin.PluginRegistry
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.io.path.deleteIfExists

@Serializable
data class TestPluginConfig(
    val foo: String,
    val bar: Int,
)

class MockPlugin : Plugin {
    override fun onLoad() {}

    override fun onEnable() {}

    override fun onDisable() {}

    override fun onUnload() {}
}

class MockPluginRegistry(
    private val plugin: Plugin,
    private val meta: PluginMeta,
) : PluginRegistry {
    private val container = PluginContainer(plugin, meta)

    override fun get(pluginId: String): PluginContainer? = if (pluginId == container.pluginId) container else null

    override fun getPluginId(pluginClass: Class<*>): String? = if (pluginClass == plugin::class.java) container.pluginId else null
}

class TestPluginExtensions {
    private val testMeta =
        PluginMeta(
            name = "TestPlugin",
            version = "1.0.0",
            mainClass = "dev.kokoroidkt.pluginApi.utils.MockPlugin",
            author = arrayOf("test"),
            description = "test",
            website = "test",
            dependedPlugins = emptyArray(),
            loadBefore = emptyArray(),
            loadAfter = emptyArray(),
        )
    private val plugin = MockPlugin()

    @BeforeEach
    fun setup() {
        startKoin {
            modules(
                module {
                    single<PluginRegistry> { MockPluginRegistry(plugin, testMeta) }
                },
            )
        }
    }

    @AfterEach
    fun tearDown() {
        stopKoin()
        kokoroidConfigRoot.resolve("plugin/TestPlugin/settings.conf").deleteIfExists()
    }

    @Test
    fun testPluginSaveAndLoadConfig() {
        val config = TestPluginConfig("hello", 123)
        plugin.saveConfigToFile(config)

        val loadedConfig = plugin.loadConfigFromFile<TestPluginConfig>()
        assertEquals(config, loadedConfig)
    }
}
