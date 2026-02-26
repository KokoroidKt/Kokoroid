/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.adapter

import dev.kokoroidkt.adapterApi.adapter.Adapter
import dev.kokoroidkt.adapterApi.adapter.AdapterMeta
import dev.kokoroidkt.core.classloader.JarClassLoader
import dev.kokoroidkt.core.exceptions.LoadAdapterFailedException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.util.jar.JarFile

class AdapterLoader(
    private val jarFile: File,
) {
    private val jar = JarFile(jarFile)
    private val json =
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            coerceInputValues = true
        }

    @OptIn(ExperimentalSerializationApi::class)
    fun loadAdapter(): Pair<Adapter, AdapterMeta> {
        val metadataEntry = jar.getEntry("adapter-meta.json")
        try {
            val metadata: AdapterMeta =
                jar.getInputStream(metadataEntry).use {
                    json.decodeFromStream<AdapterMeta>(it)
                }
            val classLoader = JarClassLoader(jarFile)
            val clazz = classLoader.loadClass(metadata.mainClass)
            val adapter = clazz.getConstructor().newInstance() as Adapter
            return adapter to metadata
        } catch (e: Exception) {
            throw LoadAdapterFailedException(
                msg = "Error while loading driver: ${e.message}",
                cause = e,
                jarFile = jarFile,
            )
        }
    }
}
