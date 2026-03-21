// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.adapterApi.adapter

interface AdapterRegistry {
    operator fun get(adapterId: String): AdapterContainer?

    fun getAdapterId(adapterClass: Class<*>): String?
}
