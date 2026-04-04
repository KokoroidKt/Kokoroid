package dev.kokoroidkt.coreApi.classloader

import dev.kokoroidkt.coreApi.logging.KokoroidLogger

abstract class ExtensionClassloader(
    parent: ClassLoader?,
) : ClassLoader(parent) {
    open var logger: KokoroidLogger
        get() = throw NotImplementedError()
        internal set(value) {
            throw NotImplementedError()
        }
}
