// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

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
