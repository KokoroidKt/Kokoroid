// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.adapterApi.utils

import dev.kokoroidkt.adapterApi.adapter.Adapter
import dev.kokoroidkt.adapterApi.adapter.AdapterMeta
import dev.kokoroidkt.adapterApi.adapter.AdapterRegistry
import dev.kokoroidkt.coreApi.config.ConfigHelper
import dev.kokoroidkt.driverApi.driver.DriverRegistry
import org.koin.mp.KoinPlatform.getKoin
import java.nio.file.Path
import java.nio.file.Paths
import java.sql.Driver

fun Adapter.getId(): String? = getKoin().get<AdapterRegistry>().getAdapterId(this::class.java)

@Suppress("UNCHECKED_CAST")
fun <T : Driver> getDriver(clazz: Class<out T>): T? = getKoin().get<DriverRegistry>()[clazz] as T?

fun Adapter.getMetadata(): AdapterMeta? = this.getId()?.let { getKoin().get<AdapterRegistry>()[it]?.metadata }

fun <T> Adapter.saveConfigToFile(
    config: T,
    path: Path = Paths.get("/settings.conf"),
) {
    getKoin().get<ConfigHelper>().encodeHoconToFile(config, Path.of("adapter", getMetadata()!!.name).resolve(path))
}

fun <T> Adapter.loadConfigFromFile(path: Path = Paths.get("/settings.conf")): T =
    getKoin().get<ConfigHelper>().decodeHoconFile<T>(Path.of("adapter", getMetadata()!!.name).resolve(path))
