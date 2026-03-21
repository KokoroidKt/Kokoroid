// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.driverApi.driver

class DriverContainer(
    val metadata: DriverMeta,
    private val driver: Driver,
    private var enabled: Boolean = false,
) {
    fun isInstance(clazz: Class<*>) = clazz.isInstance(driver)

    val isEnabled: Boolean get() = enabled

    fun load() = driver.onLoad()

    fun unload() = driver.onUnload()

    fun start() {
        driver.onStart()
        enabled = true
    }

    fun stop() {
        driver.onStop()
        enabled = false
    }

    val driverId: String
        get() = "Driver-${metadata.name}@${metadata.mainClass}"

    override fun toString(): String = driverId
}
