/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.plugin

import dev.kokoroidkt.pluginApi.conversation.ConversationOrchestrator

data class PluginContainer(
    private val plugin: Plugin,
    val metadata: PluginMeta,
    private var enabled: Boolean = false,
) {
    val isEnabled: Boolean get() = enabled
    private val _orchestrators = mutableListOf<ConversationOrchestrator>()
    val orchestrators
        get() = _orchestrators.toList()

    val pluginId: String
        get() = "Plugin-${metadata.name}@${metadata.mainClass}"

    fun isInstance(clazz: Class<*>) = clazz.isInstance(plugin)

    fun registerOrchestrator(orchestrator: ConversationOrchestrator) {
        _orchestrators + orchestrator
    }

    fun unregisterOrchestrator(orchestrator: ConversationOrchestrator) {
        _orchestrators - orchestrator
    }

    fun load() = plugin.onLoad()

    fun unload() = plugin.onUnload()

    fun enable() {
        plugin.onEnable()
        enabled = true
    }

    fun disable() {
        plugin.onDisable()
        enabled = false
    }

    override fun toString(): String = pluginId
}
