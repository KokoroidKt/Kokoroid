package dev.kokoroidkt.coreApi.utils

import dev.kokoroidkt.coreApi.classloader.ExtensionClassloader
import dev.kokoroidkt.coreApi.logging.KokoroidLogger
import dev.kokoroidkt.coreApi.logging.LoggerFactory
import org.koin.java.KoinJavaComponent.getKoin

/**
 * 拓展获得Logger的简易方法
 * Logger被注入了ExtensionClassLoader，此处可以直接取出
 * 此拓展函数只能由Kokoroid加载的拓展使用，否则会抛出IllegalStateException
 *
 * @param default 默认Logger名称，当无法获取到Logger时使用
 * @return 对应Logger
 */
fun Any.getExtensionLogger(default: String = "Extension"): KokoroidLogger {
    val classLoader =
        this::class.java.classLoader
            ?: return getKoin().get<LoggerFactory>().invoke(default, this::class)
    if (classLoader !is ExtensionClassloader) return getKoin().get<LoggerFactory>().invoke(default, this::class)
    return classLoader.logger
}
