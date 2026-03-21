// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

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
