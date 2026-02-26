/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.driver

import dev.kokoroidkt.core.classloader.JarClassLoader
import dev.kokoroidkt.core.exceptions.LoadDriverFailedException
import dev.kokoroidkt.driverApi.driver.Driver
import dev.kokoroidkt.driverApi.driver.DriverMeta
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.util.jar.JarFile

class DriverLoader(
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
    fun loadDriver(): Pair<Driver, DriverMeta> {
        val metadataEntry = jar.getEntry("driver-meta.json")
        try {
            val metadata: DriverMeta =
                jar.getInputStream(metadataEntry).use {
                    json.decodeFromStream<DriverMeta>(it)
                }
            val classLoader = JarClassLoader(jarFile)
            val clazz = classLoader.loadClass(metadata.mainClass)
            val driver = clazz.getConstructor().newInstance() as Driver
            return driver to metadata
        } catch (e: Exception) {
            throw LoadDriverFailedException(
                msg = "Error while loading driver: ${e.message}",
                cause = e,
                jarFile = jarFile,
            )
        }
    }
}
