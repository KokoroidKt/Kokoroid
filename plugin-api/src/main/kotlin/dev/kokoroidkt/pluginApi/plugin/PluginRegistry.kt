// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.plugin

interface PluginRegistry {
    operator fun get(pluginId: String): PluginContainer?

    fun getPluginId(pluginClass: Class<*>): String?
}
