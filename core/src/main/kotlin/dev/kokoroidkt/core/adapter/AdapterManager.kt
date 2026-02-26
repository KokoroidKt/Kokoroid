/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.adapter

import dev.kokoroidkt.adapterApi.adapter.Adapter
import dev.kokoroidkt.adapterApi.adapter.AdapterContainer
import dev.kokoroidkt.adapterApi.adapter.AdapterMeta

abstract class AdapterManager {
    abstract val length: Int
    abstract val adapterList: List<AdapterContainer>

    internal abstract fun register(
        adapter: Adapter,
        metadata: AdapterMeta,
    ): AdapterContainer

    internal abstract fun startAdapter(container: AdapterContainer)

    internal abstract fun stopAdapter(container: AdapterContainer)

    internal abstract fun loadAdapter(container: AdapterContainer)

    internal abstract fun unloadAdapter(container: AdapterContainer)
}
