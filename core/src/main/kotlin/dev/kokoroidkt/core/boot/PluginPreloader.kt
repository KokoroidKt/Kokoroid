package dev.kokoroidkt.core.boot

import dev.kokoroidkt.pluginApi.plugin.Plugin
import dev.kokoroidkt.pluginApi.plugin.PluginContainer
import dev.kokoroidkt.pluginApi.plugin.PluginMeta
import java.nio.file.Path

class PluginPreloader {
    val jarPaths: MutableList<String> = mutableListOf()
    val classes: MutableList<PluginContainer> = mutableListOf()

    fun addJar(path: Path) {
        jarPaths.add(path.toString())
    }

    fun install(
        plugin: Plugin,
        meta: PluginMeta,
    ) {
        classes.add(PluginContainer(plugin, meta))
    }
}
