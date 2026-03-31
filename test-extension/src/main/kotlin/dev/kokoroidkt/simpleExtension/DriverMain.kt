// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.simpleExtension

import dev.kokoroidkt.coreApi.exceptions.CriticalException
import dev.kokoroidkt.driverApi.driver.Driver
import dev.kokoroidkt.driverApi.logger.getLogger
import dev.kokoroidkt.driverApi.utils.loadConfigFromFile
import dev.kokoroidkt.driverApi.utils.saveConfigToFile
import dev.kokoroidkt.simpleExtension.config.MockConfig
import dev.kokoroidkt.simpleExtension.utils.Util

class DriverMain : Driver() {
    override fun onLoad() {
        getLogger().info { "Driver Main Loaded and ${Util("DriverMain").sayHi()}" }
        val config = MockConfig("123", 456)
        saveConfigToFile<MockConfig>(config)
        val new = loadConfigFromFile<MockConfig>()
        if (config != new) {
            throw CriticalException("Config function assert false")
        }
    }

    override fun onStart() {
        getLogger().info { "Driver Main Ready and ${Util("DriverMain").sayHi()}" }
    }

    override fun onStop() {
        getLogger().info { "Driver Main stopped and ${Util("DriverMain").sayHi()}" }
    }

    override fun onUnload() {
        getLogger().info { "Driver Main UnLoaded and ${Util("DriverMain").sayHi()}" }
    }
}
