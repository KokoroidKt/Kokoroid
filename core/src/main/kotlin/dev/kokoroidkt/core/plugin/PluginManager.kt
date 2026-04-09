// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.plugin

import dev.kokoroidkt.pluginApi.plugin.Plugin
import dev.kokoroidkt.pluginApi.plugin.PluginContainer
import dev.kokoroidkt.pluginApi.plugin.PluginMeta

abstract class PluginManager {
    internal abstract fun register(container: PluginContainer)

    internal abstract fun create(
        plugin: Plugin,
        meta: PluginMeta,
    ): PluginContainer

    abstract val pluginList: List<PluginContainer>

    internal abstract fun loadPlugin(container: PluginContainer)

    internal abstract fun enablePlugin(container: PluginContainer)

    internal abstract fun disablePlugin(container: PluginContainer)

    internal abstract fun unloadPlugin(container: PluginContainer)

    abstract val length: Int
}
