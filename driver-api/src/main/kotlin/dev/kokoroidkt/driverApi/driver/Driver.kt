/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.driverApi.driver

abstract class Driver {
    abstract fun onLoad()

    /**
     * 在Adapter，Plugin都准备好后调用
     */
    abstract fun onStart()

    abstract fun onStop()

    abstract fun onUnload()
}
