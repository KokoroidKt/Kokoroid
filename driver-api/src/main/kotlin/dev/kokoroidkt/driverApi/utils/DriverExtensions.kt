/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.driverApi.utils

import dev.kokoroidkt.driverApi.driver.Driver
import dev.kokoroidkt.driverApi.driver.DriverMeta
import dev.kokoroidkt.driverApi.driver.DriverRegistry
import org.koin.java.KoinJavaComponent.getKoin

fun Driver.getId(): String? = getKoin().get<DriverRegistry>()[this::class.java]?.driverId

fun Driver.metadata(): DriverMeta? = getKoin().get<DriverRegistry>()[this::class.java]?.metadata
