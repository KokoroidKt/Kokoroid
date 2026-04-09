package dev.kokoroidkt.core.boot

import dev.kokoroidkt.driverApi.driver.Driver
import dev.kokoroidkt.driverApi.driver.DriverContainer
import dev.kokoroidkt.driverApi.driver.DriverMeta
import java.nio.file.Path

class DriverPreloader {
    val jarPaths: MutableList<String> = mutableListOf()
    val classes: MutableList<DriverContainer> = mutableListOf()

    fun addJar(path: Path) {
        jarPaths.add(path.toString())
    }

    fun install(
        driver: Driver,
        meta: DriverMeta,
    ) {
        classes.add(DriverContainer(meta, driver))
    }

    fun install(driver: DriverContainer) {
        classes.add(driver)
    }
}
