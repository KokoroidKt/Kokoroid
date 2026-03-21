// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

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
