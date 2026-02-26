/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.adapterApi.adapter

interface AdapterRegistry {
    operator fun get(adapterId: String): AdapterContainer?

    fun getAdapterId(adapterClass: Class<*>): String?
}
