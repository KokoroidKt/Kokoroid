// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.simpleExtension

import dev.kokoroidkt.coreApi.exceptions.CriticalException
import dev.kokoroidkt.pluginApi.logger.getLogger
import dev.kokoroidkt.pluginApi.plugin.KotlinPlugin
import dev.kokoroidkt.pluginApi.utils.loadConfigFromFile
import dev.kokoroidkt.pluginApi.utils.saveConfigToFile
import dev.kokoroidkt.simpleExtension.config.MockConfig
import dev.kokoroidkt.simpleExtension.utils.Util

class PluginMain : KotlinPlugin() {
    override fun onLoad() {
        getLogger().info { "Plugin Main Loaded and ${Util("PluginMain").sayHi()}" }
        val config = MockConfig("123", 456)
        saveConfigToFile<MockConfig>(config)
        val new = loadConfigFromFile<MockConfig>()
        if (config != new) {
            throw CriticalException("Config function assert false")
        }
    }

    override fun onEnable() {
        getLogger().info { "Plugin Main enabled and ${Util("PluginMain").sayHi()}" }
    }

    override fun onDisable() {
        getLogger().info { "Plugin Main disabled and ${Util("PluginMain").sayHi()}" }
    }

    override fun onUnload() {
        getLogger().info { "Plugin Main unLoaded and ${Util("PluginMain").sayHi()}" }
    }
}
