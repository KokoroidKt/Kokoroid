// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.driverApi.driver

interface DriverRegistry {
    operator fun get(driverId: String): DriverContainer?

    fun getDriverId(driverClass: Class<*>): String?

    operator fun get(driverClass: Class<*>): DriverContainer?
}
