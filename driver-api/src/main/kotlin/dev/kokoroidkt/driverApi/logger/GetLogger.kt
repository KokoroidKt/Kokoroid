// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.driverApi.logger

import dev.kokoroidkt.coreApi.logging.LoggerFactory
import dev.kokoroidkt.driverApi.driver.Driver
import dev.kokoroidkt.driverApi.utils.metadata
import org.koin.java.KoinJavaComponent.getKoin

fun Driver.getLogger() = getKoin().get<LoggerFactory>().invoke(this.metadata()?.name.toString(), this::class)
