// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.driver

import dev.kokoroidkt.driverApi.driver.Driver
import dev.kokoroidkt.driverApi.driver.DriverContainer
import dev.kokoroidkt.driverApi.driver.DriverMeta

abstract class DriverManager {
    abstract val length: Int

    abstract val driverList: List<DriverContainer>

    internal abstract fun loadDriver(container: DriverContainer)

    internal abstract fun unloadDriver(container: DriverContainer)

    internal abstract fun startDriver(container: DriverContainer)

    internal abstract fun stopDriver(container: DriverContainer)

    internal abstract fun register(
        driver: Driver,
        metadata: DriverMeta,
    ): DriverContainer
}
