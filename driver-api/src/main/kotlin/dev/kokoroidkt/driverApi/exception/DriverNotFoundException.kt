/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.driverApi.exception

import dev.kokoroidkt.driverApi.driver.DriverContainer

class DriverNotFoundException(
    message: String,
    cause: Throwable? = null,
    causedByDriver: DriverContainer? = null,
) : DriverException(
        message = message,
        cause = cause,
        causedByDriver = causedByDriver,
    )
