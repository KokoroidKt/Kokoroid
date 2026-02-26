package dev.kokoroid.simpleExtension

import dev.kokoroid.simpleExtension.utils.Util
import dev.kokoroidkt.pluginApi.logger.getLogger
import dev.kokoroidkt.pluginApi.plugin.KotlinPlugin

class PluginMain : KotlinPlugin() {
    override fun onLoad() {
        getLogger().info { "Plugin Main Loaded and ${Util("PluginMain").sayHi()}" }
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
