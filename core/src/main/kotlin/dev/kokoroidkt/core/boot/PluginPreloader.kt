package dev.kokoroidkt.core.boot

import dev.kokoroidkt.pluginApi.plugin.Plugin
import dev.kokoroidkt.pluginApi.plugin.PluginContainer
import dev.kokoroidkt.pluginApi.plugin.PluginMeta
import java.nio.file.Path

class PluginPreloader {
    val jarPaths: MutableList<Path> = mutableListOf()
    val instants: MutableList<PluginContainer> = mutableListOf()

    fun addJar(path: Path) {
        jarPaths.add(path)
    }

    fun install(
        plugin: Plugin,
        meta: PluginMeta,
    ) {
        instants.add(PluginContainer(plugin, meta))
    }

    fun install(plugin: PluginContainer) {
        instants.add(plugin)
    }
}
