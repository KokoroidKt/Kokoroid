// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.adapter

import dev.kokoroidkt.adapterApi.adapter.Adapter
import dev.kokoroidkt.adapterApi.adapter.AdapterContainer
import dev.kokoroidkt.adapterApi.adapter.AdapterMeta
import dev.kokoroidkt.adapterApi.adapter.AdapterRegistry
import dev.kokoroidkt.adapterApi.exceptions.AdapterNotFoundException

class AdapterRegistryImpl :
    AdapterManager(),
    AdapterRegistry {
    val adapters = mutableMapOf<String, AdapterContainer>()
    override val adapterList
        get() = adapters.entries.map { it.value }.toList()

    fun requireAdapterExist(container: AdapterContainer) {
        try {
            adapters[container.adapterId]!!
        } catch (e: NullPointerException) {
            throw AdapterNotFoundException(
                "Adapter with id ${container.adapterId} has not registered yet.",
                cause = e,
                causeByAdapter = container,
            )
        }
    }

    override fun register(
        adapter: Adapter,
        metadata: AdapterMeta,
    ): AdapterContainer {
        val container = AdapterContainer(adapter, metadata)
        adapters[container.adapterId] = container
        return container
    }

    override fun startAdapter(container: AdapterContainer) {
        requireAdapterExist(container)
        container.start()
    }

    override fun stopAdapter(container: AdapterContainer) {
        requireAdapterExist(container)
        container.stop()
    }

    override fun loadAdapter(container: AdapterContainer) {
        requireAdapterExist(container)
        container.load()
    }

    override fun unloadAdapter(container: AdapterContainer) {
        requireAdapterExist(container)
        container.unload()
    }

    override val length
        get() = adapters.size

    override fun get(adapterId: String): AdapterContainer? = adapters[adapterId]

    override fun getAdapterId(adapterClass: Class<*>): String? =
        adapters.entries
            .firstOrNull { it.value.isInstance(adapterClass) }
            ?.value
            ?.adapterId
}
