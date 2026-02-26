/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.driverApi.driver

interface DriverRegistry {
    operator fun get(driverId: String): DriverContainer?

    fun getDriverId(driverClass: Class<*>): String?

    operator fun get(driverClass: Class<*>): DriverContainer?
}
