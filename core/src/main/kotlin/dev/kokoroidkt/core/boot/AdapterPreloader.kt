package dev.kokoroidkt.core.boot

import dev.kokoroidkt.adapterApi.adapter.Adapter
import dev.kokoroidkt.adapterApi.adapter.AdapterContainer
import dev.kokoroidkt.adapterApi.adapter.AdapterMeta
import java.nio.file.Path

class AdapterPreloader {
    val jarPaths: MutableList<String> = mutableListOf()
    val classes: MutableList<AdapterContainer> = mutableListOf()

    fun addJar(path: Path) {
        jarPaths.add(path.toString())
    }

    fun install(
        adapter: Adapter,
        meta: AdapterMeta,
    ) {
        classes.add(AdapterContainer(adapter, meta))
    }
}
