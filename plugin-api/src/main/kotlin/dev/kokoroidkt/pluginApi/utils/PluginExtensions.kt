// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.utils

import dev.kokoroidkt.coreApi.config.ConfigHelper
import dev.kokoroidkt.coreApi.exceptions.CriticalException
import dev.kokoroidkt.pluginApi.conversation.ConversationOrchestrator
import dev.kokoroidkt.pluginApi.conversation.Processable
import dev.kokoroidkt.pluginApi.exceptions.ConversationRegisterFailedException
import dev.kokoroidkt.pluginApi.factory.ConversationOrchestratorFactory
import dev.kokoroidkt.pluginApi.plugin.Plugin
import dev.kokoroidkt.pluginApi.plugin.PluginMeta
import dev.kokoroidkt.pluginApi.plugin.PluginRegistry
import org.koin.java.KoinJavaComponent.getKoin
import org.koin.mp.KoinPlatform
import java.nio.file.Path
import java.nio.file.Paths

fun getRegistry(): PluginRegistry = getKoin().get<PluginRegistry>()

fun Plugin.getId(): String {
    val registry = getRegistry()
    try {
        return registry.getPluginId(this::class.java)!!
    } catch (e: NullPointerException) {
        throw CriticalException("Plugin Id found: ${this::class.qualifiedName}", cause = e)
    }
}

internal fun Plugin.getContainer() =
    try {
        this.getId().let { getRegistry()[it] }!!
    } catch (e: NullPointerException) {
        throw CriticalException("Plugin Container Not found: ${this::class.qualifiedName}", cause = e)
    }

fun Plugin.metadata(): PluginMeta = getContainer().metadata

fun Plugin.addConversation(processor: Processable) {
    val orchestrator: ConversationOrchestrator
    val container = getContainer()
    try {
        orchestrator = getKoin().get<ConversationOrchestratorFactory>().create(processor)
        container.registerOrchestrator(orchestrator)
    } catch (e: Exception) {
        throw ConversationRegisterFailedException(
            "Register Conversation ${processor.name()} failed",
            container,
            e,
        )
    }
}

fun <T> Plugin.saveConfigToFile(
    config: T,
    path: Path = Paths.get("/settings.conf"),
) {
    KoinPlatform
        .getKoin()
        .get<ConfigHelper>()
        .encodeHoconToFile(config, Path.of("plugin", metadata().name).resolve(path))
}

fun <T> Plugin.loadConfigFromFile(path: Path = Paths.get("/settings.conf")) =
    KoinPlatform.getKoin().get<ConfigHelper>().decodeHoconFile<T>(Path.of("plugin", metadata().name).resolve(path))
