// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.adapter

import dev.kokoroidkt.adapterApi.adapter.Adapter
import dev.kokoroidkt.adapterApi.adapter.AdapterContainer
import dev.kokoroidkt.adapterApi.adapter.AdapterMeta
import dev.kokoroidkt.adapterApi.adapter.AdapterRegistry
import dev.kokoroidkt.adapterApi.exceptions.AdapterNotFoundException
import dev.kokoroidkt.adapterApi.utils.getId
import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.user.User
import dev.kokoroidkt.coreApi.user.UserContainer
import kotlinx.serialization.json.JsonElement
import org.junit.jupiter.api.*
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.error.KoinApplicationAlreadyStartedException
import org.koin.java.KoinJavaComponent.getKoin
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * 适配器状态枚举
 */
enum class AdapterState {
    BEFORE_LOAD,
    LOADED,
    STARTED,
    STOPPED,
    UNLOADED,
}

/**
 * 最简单的测试适配器实现
 */
class SimpleTestAdapter : Adapter {
    var state = AdapterState.BEFORE_LOAD

    override fun onLoad() {
        state = AdapterState.LOADED
    }

    override fun onStart() {
        state = AdapterState.STARTED
    }

    override fun onStop() {
        state = AdapterState.STOPPED
    }

    override fun onUnload() {
        state = AdapterState.UNLOADED
    }

    override fun getBot(botId: String): Bot =
        object : Bot {
            override fun callApi(
                apiEndpoint: String,
                data: JsonElement,
            ) {
                // DO NOTHING
            }

            override fun replyMessage(
                event: Event,
                message: MessageChain,
            ) {
            }

            override val botId: String
                get() = "" // DO NOTHING
        }

    override fun getBotList(): List<Bot> = emptyList()

    override fun getUserContainer(): UserContainer =
        object : UserContainer {
            override fun getUserById(userId: String): User? = null

            override val size: Int = 0

            override fun isEmpty(): Boolean = true

            override fun contains(element: User): Boolean = false

            override fun iterator(): Iterator<User> = emptyList<User>().iterator()

            override fun containsAll(elements: Collection<User>): Boolean = false
        }

    // override fun decodeJsonToEvent(): Event = object : Event("", 1L) {}
}

/**
 * 测试 AdapterRegistryImpl 的测试类
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TestAdapterRegistry {
    // 获取 AdapterRegistry 实例
    private val registry: AdapterRegistryImpl = getKoin().get<AdapterRegistry>() as AdapterRegistryImpl

    // 测试用的适配器实例
    private val testAdapter = SimpleTestAdapter()

    // 测试用的适配器元数据

    @Test
    @Order(1)
    fun `test failed when plugin not registered`() {
        try {
            registry.loadAdapter(AdapterContainer(SimpleTestAdapter(), testAdapterMeta))
            assertTrue(false)
        } catch (e: AdapterNotFoundException) {
            assertTrue(true)
        }
        try {
            registry.unloadAdapter(AdapterContainer(SimpleTestAdapter(), testAdapterMeta))
            assertTrue(false)
        } catch (e: AdapterNotFoundException) {
            assertTrue(true)
        }
    }

    private val testAdapterMeta =
        AdapterMeta(
            name = "TestAdapter",
            version = "1.0.0",
            mainClass = "dev.kokoroidkt.core.test.adapter.SimpleTestAdapter",
            authors = listOf("TestAuthor"),
            description = "测试适配器",
            website = null,
            priority = 500,
        )

    @Test
    @Order(2)
    fun `test register adapter`() {
        // 注册适配器
        val container = registry.register(testAdapter, testAdapterMeta)

        // 验证注册结果
        assertNotNull(container)
        assertEquals(testAdapterMeta, container.metadata)

        // 验证适配器 ID 格式
        assertTrue(container.adapterId.startsWith("Adapter-TestAdapter@"))
    }

    @Test
    @Order(3)
    fun `test get adapter id`() {
        // 注册适配器
        val container = registry.register(testAdapter, testAdapterMeta)

        // 验证通过扩展函数获取的 ID 与容器中的 ID 一致
        val adapterIdFromExtension = testAdapter.getId()
        assertEquals(container.adapterId, adapterIdFromExtension)
    }

    @Test
    @Order(4)
    fun `test adapter lifecycle`() {
        // 注册适配器
        val container = registry.register(testAdapter, testAdapterMeta)

        // 验证初始状态
        assertEquals(AdapterState.BEFORE_LOAD, testAdapter.state)

        // 测试加载适配器
        registry.loadAdapter(container)
        assertEquals(AdapterState.LOADED, testAdapter.state)

        registry.startAdapter(container)
        assertEquals(AdapterState.STARTED, testAdapter.state)

        registry.stopAdapter(container)
        assertEquals(AdapterState.STOPPED, testAdapter.state)

        // 测试卸载适配器
        registry.unloadAdapter(container)
        assertEquals(AdapterState.UNLOADED, testAdapter.state)
    }

    @Test
    @Order(5)
    fun `test get adapter id by class`() {
        // 注册适配器
        val container = registry.register(testAdapter, testAdapterMeta)

        // 通过类获取适配器 ID
        val adapterId = registry.getAdapterId(SimpleTestAdapter::class.java)
        assertNotNull(adapterId)
        assertEquals(container.adapterId, adapterId)
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
