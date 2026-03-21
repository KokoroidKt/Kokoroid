// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

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
