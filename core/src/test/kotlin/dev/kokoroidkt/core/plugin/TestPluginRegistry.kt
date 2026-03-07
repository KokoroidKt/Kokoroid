/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.plugin

import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.pluginApi.exceptions.PluginNotFoundException
import dev.kokoroidkt.pluginApi.plugin.KotlinPlugin
import dev.kokoroidkt.pluginApi.plugin.PluginContainer
import dev.kokoroidkt.pluginApi.plugin.PluginMeta
import dev.kokoroidkt.pluginApi.plugin.PluginRegistry
import dev.kokoroidkt.pluginApi.utils.getId
import junit.framework.TestCase.assertTrue
import logger.getLogger
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.TestMethodOrder
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import org.koin.java.KoinJavaComponent.getKoin
import kotlin.test.Test

enum class PluginState {
    BEFORE_LOAD,
    LOADED,
    ENABLED,
    DISABLED,
    UNLOADED,
}

class MaybeAPlugin : KotlinPlugin() {
    var state = PluginState.BEFORE_LOAD

    override fun onLoad() {
        getLogger().info { "This Plugin loaded!" }
        state = PluginState.LOADED
    }

    override fun onEnable() {
        getLogger().info { "This Plugin enabled!" }
        state = PluginState.ENABLED
    }

    override fun onDisable() {
        getLogger().info { "This Plugin disabled!" }
        state = PluginState.DISABLED
    }

    override fun onUnload() {
        getLogger().info { "This Plugin unloaded!" }
        state = PluginState.UNLOADED
    }
}

@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TestPluginContainer {
    var registry: PluginRegistryImpl = getKoin().get<PluginRegistry>() as PluginRegistryImpl
    val plugin: MaybeAPlugin = MaybeAPlugin()
    val meta =
        PluginMeta(
            name = "TestPlugin",
            author = arrayOf("TestAuthor1", "TestAuthor2"),
            description = "这是一个用于测试的插件",
            version = "1.0.0",
            mainClass = "dev.kokoroidkt.core.test.plugin.MaybeAPlugin",
            website = "https://example.com",
            dependedPlugins = arrayOf("dependency-plugin-1", "dependency-plugin-2"),
            loadBefore = arrayOf("plugin-to-load-before"),
            loadAfter = arrayOf("plugin-to-load-after"),
            priority = 500,
        )

    @Test
    @Order(1)
    fun `test failed when not registered`() {
        try {
            registry.loadPlugin(PluginContainer(plugin, meta))
            assertTrue(false)
        } catch (e: PluginNotFoundException) {
            assertTrue(true)
        }
        try {
            registry.enablePlugin(PluginContainer(plugin, meta))
            assertTrue(false)
        } catch (e: PluginNotFoundException) {
            assertTrue(true)
        }
        try {
            registry.disablePlugin(PluginContainer(plugin, meta))
            assertTrue(false)
        } catch (e: PluginNotFoundException) {
            assertTrue(true)
        }
        try {
            registry.unloadPlugin(PluginContainer(plugin, meta))
            assertTrue(false)
        } catch (e: PluginNotFoundException) {
            assertTrue(true)
        }
    }

    @Test
    @Order(2)
    fun `test register a plugin`() {
        val container = registry.register(plugin, meta)
        assert(container.metadata == meta)
        println(container.pluginId)
    }

    @Test
    @Order(3)
    fun `test get plugin id`() {
        val container = registry.register(plugin, meta)
        assert(container.pluginId == plugin.getId())
    }

    @Test
    @Order(4)
    fun `test life circle`() {
        val container = registry.register(plugin, meta)

        assert(plugin.state == PluginState.BEFORE_LOAD)
        registry.loadPlugin(container)
        assert(plugin.state == PluginState.LOADED)
        registry.enablePlugin(container)
        assert(plugin.state == PluginState.ENABLED)
        registry.disablePlugin(container)
        assert(plugin.state == PluginState.DISABLED)
        registry.enablePlugin(container)
        assert(plugin.state == PluginState.ENABLED)
        registry.unloadPlugin(container)
        assert(plugin.state == PluginState.UNLOADED)
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun `set up koin`() {
            try {
                startKoin {
                    modules(allModules)
                }
            } catch (_: KoinApplicationAlreadyStartedException) {
            }
        }

        @JvmStatic
        @AfterAll
        fun `tear down koin`() {
            stopKoin()
        }
    }
}
