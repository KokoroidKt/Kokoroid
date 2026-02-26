package dev.kokoroid.simpleExtension

import dev.kokoroid.simpleExtension.utils.Util
import dev.kokoroidkt.driverApi.driver.Driver
import dev.kokoroidkt.driverApi.logger.getLogger

class DriverMain : Driver() {
    override fun onLoad() {
        getLogger().info { "Driver Main Loaded and ${Util("DriverMain").sayHi()}" }
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
