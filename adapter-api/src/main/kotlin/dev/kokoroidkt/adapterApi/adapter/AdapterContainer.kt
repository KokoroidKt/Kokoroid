/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.adapterApi.adapter

data class AdapterContainer(
    private val adapter: Adapter,
    val metadata: AdapterMeta,
    private var enabled: Boolean = false,
) {
    val isEnabled: Boolean get() = enabled
    val adapterId: String
        get() = "Adapter-${metadata.name}@${metadata.mainClass}"

    fun isInstance(clazz: Class<*>) = clazz.isInstance(adapter)

    fun load() = adapter.onLoad()

    fun start() = adapter.onStart()

    fun stop() = adapter.onStop()

    fun unload() = adapter.onUnload()

    override fun toString(): String = adapterId
}
