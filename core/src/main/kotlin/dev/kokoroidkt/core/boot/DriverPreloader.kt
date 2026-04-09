package dev.kokoroidkt.core.boot

import dev.kokoroidkt.driverApi.driver.Driver
import dev.kokoroidkt.driverApi.driver.DriverContainer
import dev.kokoroidkt.driverApi.driver.DriverMeta
import java.nio.file.Path

class DriverPreloader {
    val jarPaths: MutableList<Path> = mutableListOf()
    val instants: MutableList<DriverContainer> = mutableListOf()

    fun addJar(path: Path) {
        jarPaths.add(path)
    }

    fun install(
        driver: Driver,
        meta: DriverMeta,
    ) {
        instants.add(DriverContainer(meta, driver))
    }

    fun install(driver: DriverContainer) {
        instants.add(driver)
    }
}
