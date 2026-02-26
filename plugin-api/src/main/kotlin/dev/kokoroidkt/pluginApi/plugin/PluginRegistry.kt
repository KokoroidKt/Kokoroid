/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.plugin

interface PluginRegistry {
    operator fun get(pluginId: String): PluginContainer?

    fun getPluginId(pluginClass: Class<*>): String?
}
