/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.di

import dev.kokoroidkt.adapterApi.adapter.AdapterRegistry
import dev.kokoroidkt.core.adapter.AdapterManager
import dev.kokoroidkt.core.adapter.AdapterRegistryImpl
import dev.kokoroidkt.core.config.Config
import dev.kokoroidkt.core.driver.DriverManager
import dev.kokoroidkt.core.driver.DriverRegistryImpl
import dev.kokoroidkt.core.factory.ConversationOrchestratorFactoryImpl
import dev.kokoroidkt.core.factory.DefaultSessionFactoryImpl
import dev.kokoroidkt.core.factory.SessionContainerFactoryImpl
import dev.kokoroidkt.core.plugin.PluginManager
import dev.kokoroidkt.core.plugin.PluginRegistryImpl
import dev.kokoroidkt.core.runtime.GlobalEventLoop
import dev.kokoroidkt.core.runtime.KokoroidLauncher
import dev.kokoroidkt.core.runtime.crash.CrashRegistry
import dev.kokoroidkt.core.runtime.crash.CrashRegistryImpl
import dev.kokoroidkt.core.runtime.status.RuntimeStatus
import dev.kokoroidkt.core.utils.binds
import dev.kokoroidkt.coreApi.logging.LoggerFactory
import dev.kokoroidkt.driverApi.driver.DriverRegistry
import dev.kokoroidkt.pluginApi.factory.ConversationOrchestratorFactory
import dev.kokoroidkt.pluginApi.plugin.PluginRegistry
import dev.kokoroidkt.pluginApi.session.container.SessionContainer
import dev.kokoroidkt.pluginApi.session.container.SessionFactoty
import logger.DefaultKokoroidLogger
import org.koin.dsl.module
import kotlin.reflect.KClass

val pluginModules =
    module {
        single { PluginRegistryImpl() }
            .binds(PluginRegistry::class, PluginManager::class)
    }

val adapterModules =
    module {
        single { AdapterRegistryImpl() }
            .binds(AdapterManager::class, AdapterRegistry::class)
    }

val driverModules =
    module {
        single { DriverRegistryImpl() }
            .binds(DriverManager::class, DriverRegistry::class)
    }

val loggerModules =
    module {
        factory<LoggerFactory> {
            { prefix: String, clazz: KClass<*> ->
                DefaultKokoroidLogger(
                    name = clazz.simpleName ?: "Unknown",
                    prefix = prefix,
                )
            }
        }
    }

val basicModules =
    module {
        single<Config> { Config() }
        single<GlobalEventLoop> { GlobalEventLoop() }
        single<KokoroidLauncher> { KokoroidLauncher() }
        single<CrashRegistry> { CrashRegistryImpl() }
    }

val runtimeModules =
    module {
        single<RuntimeStatus> { RuntimeStatus() }
        factory<SessionContainer> {
            SessionContainerFactoryImpl().createSessionContainer()
        }
        single<SessionFactoty> { DefaultSessionFactoryImpl() }
        single<ConversationOrchestratorFactory> { ConversationOrchestratorFactoryImpl() }
    }

val allModules =
    listOf(
        basicModules,
        pluginModules,
        adapterModules,
        driverModules,
        loggerModules,
        runtimeModules,
    )
