// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.plugin

import dev.kokoroidkt.pluginApi.exceptions.PluginNotFoundException
import dev.kokoroidkt.pluginApi.plugin.Plugin
import dev.kokoroidkt.pluginApi.plugin.PluginContainer
import dev.kokoroidkt.pluginApi.plugin.PluginMeta
import dev.kokoroidkt.pluginApi.plugin.PluginRegistry

class PluginRegistryImpl :
    PluginManager(),
    PluginRegistry {
    val pluginMap = mutableMapOf<String, PluginContainer>()

    public override fun register(
        plugin: Plugin,
        meta: PluginMeta,
    ): PluginContainer {
        val container = PluginContainer(plugin, meta)
        pluginMap[container.pluginId] = container
        return container
    }

    override val pluginList: List<PluginContainer>
        get() = pluginMap.values.toList()

    private fun requirePluginRegistered(pluginContainer: PluginContainer): PluginContainer {
        try {
            return pluginMap[pluginContainer.pluginId]!!
        } catch (e: NullPointerException) {
            throw PluginNotFoundException(
                "${pluginContainer.pluginId} has not been registered",
                causeByPlugin = pluginContainer,
                cause = e,
            )
        }
    }

    override fun enablePlugin(container: PluginContainer) {
        val container = requirePluginRegistered(container)
        container.enable()
    }

    override fun disablePlugin(container: PluginContainer) {
        val container = requirePluginRegistered(container)
        container.disable()
    }

    override fun loadPlugin(container: PluginContainer) {
        val container = requirePluginRegistered(container)
        container.load()
    }

    override fun unloadPlugin(container: PluginContainer) {
        val container = requirePluginRegistered(container)
        container.unload()
    }

    override fun get(pluginId: String): PluginContainer? = pluginMap[pluginId]

    override fun getPluginId(pluginClass: Class<*>): String? =
        pluginMap.entries
            .firstOrNull { it.value.isInstance(pluginClass) }
            ?.value
            ?.pluginId

    override val length
        get() = pluginMap.size
}
