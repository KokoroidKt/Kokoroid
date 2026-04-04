package dev.kokoroidkt.coreApi.utils

import dev.kokoroidkt.coreApi.classloader.ExtensionClassloader
import dev.kokoroidkt.coreApi.logging.KokoroidLogger

/**
 * 拓展获得Logger的简易方法
 * Logger被注入了ExtensionClassLoader，此处可以直接取出
 * 此拓展函数只能由Kokoroid加载的拓展使用，否则会抛出IllegalStateException
 *
 * @return 对应Logger
 */
fun Any.getExtensionLogger(): KokoroidLogger {
    val classLoader =
        this::class.java.classLoader
            ?: throw IllegalStateException("Class loader cannot be null for object: ${this::class.java.simpleName}")
    if (classLoader !is ExtensionClassloader) throw IllegalStateException("Class loader must be ExtensionClassloader")
    return classLoader.logger
}
