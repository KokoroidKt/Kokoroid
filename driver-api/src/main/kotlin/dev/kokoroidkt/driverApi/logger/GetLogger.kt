/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.driverApi.logger

import dev.kokoroidkt.coreApi.logging.LoggerFactory
import dev.kokoroidkt.driverApi.driver.Driver
import dev.kokoroidkt.driverApi.utils.metadata
import org.koin.java.KoinJavaComponent.getKoin

fun Driver.getLogger() = getKoin().get<LoggerFactory>().invoke(this.metadata()?.name.toString(), this::class)
