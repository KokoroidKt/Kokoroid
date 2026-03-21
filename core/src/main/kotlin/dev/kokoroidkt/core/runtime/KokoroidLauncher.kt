// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.runtime

import dev.kokoroidkt.adapterApi.adapter.Adapter
import dev.kokoroidkt.adapterApi.adapter.AdapterMeta
import dev.kokoroidkt.core.adapter.AdapterLoader
import dev.kokoroidkt.core.adapter.AdapterManager
import dev.kokoroidkt.core.config.Config
import dev.kokoroidkt.core.constants.ExitStatus
import dev.kokoroidkt.core.driver.DriverLoader
import dev.kokoroidkt.core.driver.DriverManager
import dev.kokoroidkt.core.logger.getLogger
import dev.kokoroidkt.core.plugin.PluginLoader
import dev.kokoroidkt.core.plugin.PluginManager
import dev.kokoroidkt.core.runtime.crash.CrashRegistry
import dev.kokoroidkt.core.runtime.state.InternalState
import dev.kokoroidkt.core.runtime.state.RuntimeState
import dev.kokoroidkt.coreApi.exceptions.CriticalException
import dev.kokoroidkt.driverApi.driver.Driver
import dev.kokoroidkt.driverApi.driver.DriverMeta
import dev.kokoroidkt.pluginApi.plugin.Plugin
import dev.kokoroidkt.pluginApi.plugin.PluginMeta
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.io.path.walk
import kotlin.system.exitProcess

class KokoroidLauncher : KoinComponent {
    private val pluginManager: PluginManager by inject()
    private val adapterManager: AdapterManager by inject()
    private val driverManager: DriverManager by inject()
    private val config: Config by inject()
    private val globalEventLoop: GlobalEventLoop by inject()
    private val crashRegistry: CrashRegistry by inject()
    private val runtimeState: RuntimeState by inject()

    private val shutdownThread =
        Thread {
            val logger = getLogger("Shutdown")
            logger.info { "Shutting down...\n" }
            runtimeState.state = InternalState.BeforeStopping()
            val ex = runCatching { stopAllExtensions() }.exceptionOrNull()
            if (ex != null) {
                logger.error(ex) { "Error when shutdown: ${ex::class.qualifiedName}: ${ex.message}" }
            }
            if (crashRegistry.isCrashed) {
                logger.error { "###### Kokoroid Crash Report ######" }
                crashRegistry.logRecords()
                runtimeState.state =
                    InternalState.Stopped(ExitStatus.CRITICAL_ERROR_EXIT)
            } else {
                runtimeState.state = InternalState.Stopped(ExitStatus.SUCCESS_EXIT)
            }
            logger.info { "Kokoroid shutdown, bye" }
        }

    /**
     * 启动Kokoroid
     */
    fun launch(validatingOnly: Boolean) {
        Runtime.getRuntime().addShutdownHook(shutdownThread)
        val logger = getLogger("KokoroidLifecycle")
        try {
            initAllExtensions()
            if (!validatingOnly) {
                runtimeState.state =
                    InternalState.Running()
                runBlocking {
                    globalEventLoop.start()
                }
            }
        } catch (e: CriticalException) {
            logger.error(e) { "CRITICAL Error occurred" }
            crashRegistry.recordAndRequestStop(e, null)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // 漏网异常视为框架不可恢复错误，按 Critical 上报并停机
            logger.error(e) { "occurred Uncatched Exception" }
            crashRegistry.recordAndRequestStop(
                CriticalException(cause = e),
                event = null,
            )
        } finally {
            shutdownThread.start()
            shutdownThread.join()
            val exitCode =
                if (runtimeState.state is InternalState.Stopped) {
                    (runtimeState.state as InternalState.Stopped).statusCode
                } else {
                    logger.error { "Wrong exiting runtimeStatus.status: ${runtimeState.state::class.qualifiedName}" }
                    ExitStatus.WRONG_EXIT_STATE
                }
            exitProcess(exitCode)
        }
    }

    fun stopAllExtensions() {
        runtimeState.state = InternalState.Stopping(InternalState.Stopping.StoppingStep.StoppingDrivers())
        stopDrivers()
        runtimeState.state = InternalState.Stopping(InternalState.Stopping.StoppingStep.StoppingAdapters())
        stopAdapters()
        runtimeState.state = InternalState.Stopping(InternalState.Stopping.StoppingStep.UnloadingPlugins())
        unloadPlugins()
        runtimeState.state = InternalState.Stopping(InternalState.Stopping.StoppingStep.UnloadingAdapters())
        unloadAdapters()
        runtimeState.state = InternalState.Stopping(InternalState.Stopping.StoppingStep.UnloadingDrivers())
        unloadDrivers()
    }

    fun stopDrivers() {
        val logger = getLogger("DriverStopper")
        logger.info { "Stopping Drivers" }
        driverManager.driverList.forEach {
            try {
                logger.debug { "Stopping Driver ${it.driverId}" }
                it.stop()
            } catch (e: Exception) {
                logger.error(e) { "error while stopping ${it.driverId}" }
            }
        }
    }

    fun stopAdapters() {
        val logger = getLogger("AdapterStopper")
        logger.info { "Stopping Adapters" }
        adapterManager.adapterList.forEach {
            try {
                logger.debug { "Stopping Adapter ${it.adapterId}" }
                it.stop()
            } catch (e: Exception) {
                logger.error(e) { "error while stopping ${it.adapterId}" }
            }
        }
    }

    fun unloadPlugins() {
        val logger = getLogger("PluginUnloader")
        logger.info { "Unloading Plugins" }
        pluginManager.pluginList.forEach {
            try {
                logger.debug { "unloading Plugin ${it.pluginId}" }
                it.disable()
                it.unload()
            } catch (e: Exception) {
                logger.error(e) { "error while unloading ${it.pluginId}" }
            }
        }
    }

    fun unloadAdapters() {
        val logger = getLogger("AdapterUnloader")
        logger.info { "Unloading Adapters" }
        adapterManager.adapterList.forEach { container ->
            try {
                logger.debug { "unloading Adapter ${container.adapterId}" }
                adapterManager.unloadAdapter(container)
            } catch (e: Exception) {
                logger.error(e) { "error while unloading ${container.adapterId}" }
            }
        }
    }

    fun unloadDrivers() {
        val logger = getLogger("DriverUnloader")
        logger.info { "Unloading Drivers" }
        driverManager.driverList.forEach { container ->
            try {
                logger.debug { "unloading ${container.driverId}" }
                driverManager.unloadDriver(container)
            } catch (e: Exception) {
                logger.error(e) { "error while unloading ${container.driverId}" }
            }
        }
    }

    /**
     * 初始化所有拓展
     * 顺序：
     * [Driver.onLoad] -> [Adapter.onLoad] -> [Plugin.onLoad] -> [Plugin.onEnable] -> [Adapter.onStart] -> [Driver.onStart]
     * 1. Driver首先加载，Adapter才能检查有没有自己需要的Driver
     * 2. Plugin才能发现有没有自己需要的Adapter
     * 3. Plugin可能向Adapter或者其他组件挂钩/发出请求，所以[Plugin.onEnable]后执行[Adapter.onStart]
     * 4. Driver最后启动，因为Driver需要处理Adapter的网络需求（轮询声明/开启API端点）
     * 5. 按照优先级顺序加载每个Plugin，Plugin启动成功后会立刻调用他的[Plugin.onEnable]方法
     */
    fun initAllExtensions() {
        runtimeState.state = InternalState.Starting(InternalState.Starting.StartingStep.LoadingDrivers())
        loadDrivers()
        runtimeState.state = InternalState.Starting(InternalState.Starting.StartingStep.LoadingAdapters())
        loadAdapters()
        runtimeState.state = InternalState.Starting(InternalState.Starting.StartingStep.LoadingPlugins())
        initPlugins()
        runtimeState.state = InternalState.Starting(InternalState.Starting.StartingStep.StartingAdapters())
        startAdapters()
        runtimeState.state = InternalState.Starting(InternalState.Starting.StartingStep.StartingDrivers())
        startDrivers()
        runtimeState.state = InternalState.AfterStarting()
    }

    fun startAdapters() {
        val logger = getLogger("AdapterStarter")
        logger.info { "Starting Adapters" }
        adapterManager.adapterList.forEach {
            try {
                logger.debug { "starting Adapter ${it.adapterId}" }
                it.start()
            } catch (e: Exception) {
                logger.error(e) { "error while starting ${it.adapterId}" }
            }
        }
    }

    fun startDrivers() {
        val logger = getLogger("DriverStarter")
        logger.info { "Starting Drivers" }
        driverManager.driverList.forEach {
            try {
                logger.debug { "starting Driver ${it.driverId}" }
                it.start()
            } catch (e: Exception) {
                logger.error(e) {
                    "error while starting ${it.driverId}"
                }
            }
        }
    }

    fun loadDrivers() {
        val logger = getLogger("DriverLoader")
        logger.info { "loading drivers..." }
        val driverDirectory = config.basic.driverDirectory
        logger.info {
            val count =
                driverDirectory.walk().count { it.extension == "jar" && it.isRegularFile() }
            "find $count drivers"
        }
        driverDirectory
            .walk()
            .filter { it.isRegularFile() && it.extension == "jar" }
            .mapNotNull {
                try {
                    logger.debug { "try to load ${it.toFile().absolutePath}" }
                    val driverPair =
                        DriverLoader(it.toFile())
                            .loadDriver()
                    val driver: Driver = driverPair.first
                    val metadata: DriverMeta = driverPair.second
                    driverManager.register(driver, metadata)
                } catch (e: Exception) {
                    logger.error(e) {
                        "Failed to load driver: ${e.message}"
                    }
                    null
                }
            }.sortedBy { it.metadata.priority }
            .forEach {
                try {
                    logger.debug { "${it.driverId} metadata: ${Json.encodeToString(it.metadata)}" }
                    logger.info { "Loading ${it.driverId}" }
                    driverManager.loadDriver(it)
                    logger.info { "Loaded ${it.driverId} successfully" }
                } catch (e: Exception) {
                    logger.error(e) { "Failed to load driver: ${e.message}" }
                }
            }
        logger.info { "successfully loaded ${driverManager.length} drivers" }
    }

    fun loadAdapters() {
        val logger = getLogger("AdapterLoader")
        logger.info { "loading adapters..." }
        val adapterDictionary = config.basic.adapterDirectory
        logger.info {
            val count =
                adapterDictionary.walk().count { it.extension == "jar" && it.isRegularFile() }
            "find $count adapters"
        }
        adapterDictionary
            .walk()
            .filter { it.isRegularFile() && it.extension == "jar" }
            .mapNotNull {
                try {
                    logger.debug { "try to load ${it.toFile().absolutePath}" }
                    val adapterPair = AdapterLoader(it.toFile()).loadAdapter()
                    val adapter: Adapter = adapterPair.first
                    val metadata: AdapterMeta = adapterPair.second
                    adapterManager.register(adapter, metadata)
                } catch (e: Exception) {
                    logger.error(e) {
                        "Failed to load adapter: ${e.message}"
                    }
                    null
                }
            }.sortedBy { it.metadata.priority }
            .forEach {
                try {
                    logger.debug { "${it.adapterId} metadata: ${Json.encodeToString(it.metadata)}" }
                    logger.info { "Loading ${it.adapterId}" }
                    adapterManager.loadAdapter(it)
                    logger.info { "Loaded ${it.adapterId} successfully" }
                } catch (e: Exception) {
                    logger.error(e) { "Failed to load adapter: ${e.message}" }
                }
            }
        logger.info { "successfully loaded ${adapterManager.length} adapters" }
    }

    fun initPlugins() {
        val logger = getLogger("PluginLoader")
        logger.info { "loading plugins..." }
        val pluginDirectory = config.basic.pluginDirectory
        logger.info {
            val count =
                pluginDirectory
                    .walk()
                    .count { it.isRegularFile() && it.extension == "jar" }
            "find $count plugins."
        }
        pluginDirectory
            .walk()
            .filter { it.isRegularFile() && it.extension == "jar" }
            .mapNotNull {
                try {
                    logger.debug { "Try to loading: ${it.toFile().absolutePath}" }
                    val pluginPair = PluginLoader(it.toFile()).loadPlugin()
                    val plugin: Plugin = pluginPair.first
                    val metadata: PluginMeta = pluginPair.second
                    pluginManager.register(plugin, metadata)
                } catch (e: Exception) {
                    logger.error(e) { "Failed to load plugin: ${e.message}" }
                    null
                }
            }.sortedBy { it.metadata.priority }
            .forEach {
                try {
                    logger.debug { "${it.pluginId} metadata: ${Json.encodeToString(it.metadata)}" }
                    logger.info { "loading ${it.pluginId}" }
                    pluginManager.loadPlugin(it)
                    logger.debug { "enable ${it.pluginId} plugin" }
                    pluginManager.enablePlugin(it)
                    logger.info { "enable ${it.pluginId} plugin successfully" }
                } catch (e: Exception) {
                    logger.error(e) { "Failed to load plugin: ${e.message}" }
                }
            }
        logger.info { "successfully loaded ${pluginManager.length} plugins" }
    }
}
