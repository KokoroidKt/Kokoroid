/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.utils

import dev.kokoroidkt.coreApi.exceptions.CriticalException
import dev.kokoroidkt.pluginApi.conversation.ConversationOrchestrator
import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.exceptions.ConversationRegisterFailedException
import dev.kokoroidkt.pluginApi.factory.ConversationOrchestratorFactory
import dev.kokoroidkt.pluginApi.plugin.Plugin
import dev.kokoroidkt.pluginApi.plugin.PluginMeta
import dev.kokoroidkt.pluginApi.plugin.PluginRegistry
import org.koin.java.KoinJavaComponent.getKoin
import kotlin.reflect.KFunction

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

fun Plugin.addConversation(processor: Processor) {
    val orchestrator: ConversationOrchestrator
    val container = getContainer()
    try {
        orchestrator = getKoin().get<ConversationOrchestratorFactory>().create(processor)
        container.registerOrchestrator(orchestrator)
    } catch (e: Exception) {
        throw ConversationRegisterFailedException("Register Conversation ${processor.function.name} failed", container, e)
    }
}
