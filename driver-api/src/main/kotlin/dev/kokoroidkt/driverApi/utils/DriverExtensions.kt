// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.driverApi.utils

import dev.kokoroidkt.coreApi.config.ConfigHelper
import dev.kokoroidkt.driverApi.driver.Driver
import dev.kokoroidkt.driverApi.driver.DriverMeta
import dev.kokoroidkt.driverApi.driver.DriverRegistry
import org.koin.java.KoinJavaComponent.getKoin
import org.koin.mp.KoinPlatform
import java.nio.file.Path
import java.nio.file.Paths

fun Driver.getId(): String? = getKoin().get<DriverRegistry>()[this::class.java]?.driverId

fun Driver.metadata(): DriverMeta? = getKoin().get<DriverRegistry>()[this::class.java]?.metadata

fun <T> Driver.saveConfigToFile(
    config: T,
    path: Path = Paths.get("/settings.conf"),
) {
    KoinPlatform.getKoin().get<ConfigHelper>().encodeHoconToFile(config, Path.of("driver", metadata()!!.name).resolve(path))
}

fun <T> Driver.loadConfigFromFile(path: Path = Paths.get("/settings.conf")): T =
    KoinPlatform.getKoin().get<ConfigHelper>().decodeHoconFile<T>(Path.of("driver", metadata()!!.name).resolve(path))
