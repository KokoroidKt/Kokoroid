// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.driver

import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.driverApi.driver.Driver
import dev.kokoroidkt.driverApi.driver.DriverContainer
import dev.kokoroidkt.driverApi.driver.DriverMeta
import dev.kokoroidkt.driverApi.driver.DriverRegistry
import dev.kokoroidkt.driverApi.exception.DriverNotFoundException
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
 * 驱动器状态枚举
 */
enum class DriverState {
    BEFORE_LOAD,
    LOADED,
    UNLOADED,
    STOPPED,
    STARTED,
}

/**
 * 最简单的测试驱动器实现
 */
class SimpleTestDriver : Driver() {
    var state = DriverState.BEFORE_LOAD

    override fun onLoad() {
        state = DriverState.LOADED
    }

    override fun onStart() {
        state = DriverState.STARTED
    }

    override fun onStop() {
        state = DriverState.STOPPED
    }

    override fun onUnload() {
        state = DriverState.UNLOADED
    }
}

/**
 * 测试 DriverRegistryImpl 的测试类
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TestDriverRegistry {
    // 获取 DriverRegistry 实例
    private val registry: DriverRegistryImpl = getKoin().get<DriverRegistry>() as DriverRegistryImpl

    // 测试用的驱动器实例
    private val testDriver = SimpleTestDriver()

    // 测试用的驱动器元数据
    private val testDriverMeta =
        DriverMeta(
            name = "TestDriver",
            version = "1.0.0",
            mainClass = "dev.kokoroidkt.core.test.driver.SimpleTestDriver",
            authors = listOf("TestAuthor"),
            description = "测试驱动器",
            website = null,
            priority = 500,
        )

    @Test
    @Order(1)
    fun `test failed when driver not registered`() {
        try {
            registry.loadDriver(DriverContainer(testDriverMeta, testDriver))
            assertTrue(false)
        } catch (e: DriverNotFoundException) {
            assertTrue(true)
        }
        try {
            registry.unloadDriver(DriverContainer(testDriverMeta, testDriver))
            assertTrue(false)
        } catch (e: DriverNotFoundException) {
            assertTrue(true)
        }
    }

    @Test
    @Order(2)
    fun `test register driver`() {
        // 注册驱动器
        val container = registry.create(testDriver, testDriverMeta)

        assertEquals(testDriverMeta, container.metadata)
    }

    @Test
    @Order(3)
    fun `test driver lifecycle`() {
        val container = registry.create(testDriver, testDriverMeta)

        assertEquals(DriverState.BEFORE_LOAD, testDriver.state)

        registry.loadDriver(container)
        assertEquals(DriverState.LOADED, testDriver.state)
        registry.startDriver(container)
        assertEquals(DriverState.STARTED, testDriver.state)

        registry.stopDriver(container)
        assertEquals(DriverState.STOPPED, testDriver.state)

        registry.unloadDriver(container)
        assertEquals(DriverState.UNLOADED, testDriver.state)
    }

    @Test
    @Order(4)
    fun `test get driver by id`() {
        val container = registry.create(testDriver, testDriverMeta)

        val retrievedDriver = registry[container.driverId]
        assertNotNull(retrievedDriver)

        val nonExistentDriver = registry["NonExistentDriver"]
        assertEquals(null, nonExistentDriver)
    }

    @Test
    @Order(5)
    fun `test get driver id by class`() {
        // 注册驱动器
        val container = registry.create(testDriver, testDriverMeta)

        // 通过类获取驱动器 ID
        val driverId = registry.getDriverId(SimpleTestDriver::class.java)
        assertNotNull(driverId)
        assertEquals(container.driverId, driverId)

        // 测试获取不存在的类
        val nonExistentDriverId = registry.getDriverId(String::class.java)
        assertEquals(null, nonExistentDriverId)
    }

    @Test
    @Order(6)
    fun `test multiple drivers registration`() {
        // 创建第二个测试驱动器
        val secondDriver = SimpleTestDriver()
        val secondDriverMeta =
            DriverMeta(
                name = "SecondTestDriver",
                version = "2.0.0",
                mainClass = "dev.kokoroidkt.core.test.driver.SimpleTestDriver",
                authors = listOf("SecondAuthor"),
                description = "第二个测试驱动器",
                website = null,
                priority = 600,
            )

        // 注册两个驱动器
        val container1 = registry.create(testDriver, testDriverMeta)
        val container2 = registry.create(secondDriver, secondDriverMeta)

        // 验证驱动器 ID 不同
        assertTrue(container1.driverId != container2.driverId)
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
