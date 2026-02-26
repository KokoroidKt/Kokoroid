/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.driverApi.exception

import dev.kokoroidkt.coreApi.exceptions.KokoroidException
import dev.kokoroidkt.driverApi.driver.DriverContainer

abstract class DriverException(
    message: String,
    cause: Throwable? = null,
    val causedByDriver: DriverContainer? = null,
) : KokoroidException(
        message,
        cause = cause,
    )
