/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.plugin

import dev.kokoroidkt.core.classloader.JarClassLoader
import dev.kokoroidkt.core.exceptions.LoadPluginFailedException
import dev.kokoroidkt.pluginApi.plugin.Plugin
import dev.kokoroidkt.pluginApi.plugin.PluginMeta
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.util.jar.JarFile

class PluginLoader(
    val jarFile: File,
) {
    private val jar = JarFile(jarFile)
    private val json =
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            coerceInputValues = true
        }

    internal fun loadPlugin(): Pair<Plugin, PluginMeta> {
        val metadata = getMetadata()
        var plugin: Plugin
        try {
            val clazz = JarClassLoader(jarFile).loadClass(metadata.mainClass)
            plugin = clazz.getConstructor().newInstance() as Plugin
        } catch (e: Exception) {
            throw LoadPluginFailedException(
                "Error while loading plugin from ${jarFile.absolutePath} (${e.message})",
                cause = e,
                jarFile = jarFile,
            )
        }
        return plugin to metadata
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun getMetadata(): PluginMeta {
        try {
            val metadataEntry =
                jar.getJarEntry("plugin-meta.json") ?: throw LoadPluginFailedException(
                    "cannot find metadata in jar file $jarFile",
                    jarFile = jarFile,
                )

            return jar.getInputStream(metadataEntry).use {
                json.decodeFromStream<PluginMeta>(it)
            }
        } catch (e: LoadPluginFailedException) {
            throw e
        } catch (e: Exception) {
            throw LoadPluginFailedException(
                "Error while loading plugin metadata from jar file $jarFile",
                cause = e,
                jarFile = jarFile,
            )
        }
    }
}
