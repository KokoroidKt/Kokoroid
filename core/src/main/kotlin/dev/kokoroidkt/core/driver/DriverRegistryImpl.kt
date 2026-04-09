// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.driver

import dev.kokoroidkt.driverApi.driver.Driver
import dev.kokoroidkt.driverApi.driver.DriverContainer
import dev.kokoroidkt.driverApi.driver.DriverMeta
import dev.kokoroidkt.driverApi.driver.DriverRegistry
import dev.kokoroidkt.driverApi.exception.DriverNotFoundException

class DriverRegistryImpl :
    DriverManager(),
    DriverRegistry {
    val drivers = mutableMapOf<String, DriverContainer>()
    override val driverList
        get() = drivers.entries.map { it.value }.toList()

    private fun requireDriverExists(container: DriverContainer) {
        try {
            drivers[container.driverId]!!
        } catch (e: NullPointerException) {
            throw DriverNotFoundException(
                message = "Driver ${container.driverId} has not registered yet",
                cause = e,
                causedByDriver = container,
            )
        }
    }

    override fun create(
        driver: Driver,
        metadata: DriverMeta,
    ): DriverContainer {
        val container = DriverContainer(metadata, driver)
        drivers[container.driverId] = container
        return container
    }

    override fun register(driverContainer: DriverContainer) {
        drivers[driverContainer.driverId] = driverContainer
    }

    override fun loadDriver(container: DriverContainer) {
        requireDriverExists(container)
        container.load()
    }

    override fun unloadDriver(container: DriverContainer) {
        requireDriverExists(container)
        container.unload()
    }

    override fun startDriver(container: DriverContainer) {
        requireDriverExists(container)
        container.start()
    }

    override fun stopDriver(container: DriverContainer) {
        requireDriverExists(container)
        container.stop()
    }

    override fun get(driverId: String): DriverContainer? = drivers[driverId]

    override val length
        get() = drivers.size

    override fun getDriverId(driverClass: Class<*>): String? =
        drivers.entries
            .firstOrNull { it.value.isInstance(driverClass) }
            ?.value
            ?.driverId

    override fun get(driverClass: Class<*>): DriverContainer? =
        drivers.entries
            .firstOrNull { it.value.isInstance(driverClass) }
            ?.value
}
