/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package logger

import dev.kokoroidkt.coreApi.logging.KokoroidLogger
import dev.kokoroidkt.coreApi.logging.LogLevelManager
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KLoggingEventBuilder
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.Level
import io.github.oshai.kotlinlogging.Marker

class DefaultKokoroidLogger(
    override val name: String,
    override val prefix: String,
    override val delegate: KLogger = KotlinLogging.logger(name),
) : KokoroidLogger {
    // Kotlin 风格的日志方法（带 lambda）
    // 这些方法使用 lambda 参数来延迟消息计算，只有在日志级别确实会被记录时才会计算消息内容
    override fun trace(message: () -> Any?) = delegate.trace { "[$prefix] ${message()}" }

    override fun debug(message: () -> Any?) = delegate.debug { "[$prefix] ${message()}" }

    override fun info(message: () -> Any?) = delegate.info { "[$prefix] ${message()}" }

    override fun warn(message: () -> Any?) = delegate.warn { "[$prefix] ${message()}" }

    override fun error(message: () -> Any?) = delegate.error { "[$prefix] ${message()}" }

    // 带异常的 Kotlin 风格日志方法
    // 这些方法在记录日志消息的同时，还可以记录异常堆栈信息
    override fun trace(
        throwable: Throwable?,
        message: () -> Any?,
    ) = delegate.trace(throwable) { "[$prefix] ${message()}" }

    override fun debug(
        throwable: Throwable?,
        message: () -> Any?,
    ) = delegate.debug(throwable) { "[$prefix] ${message()}" }

    override fun info(
        throwable: Throwable?,
        message: () -> Any?,
    ) = delegate.info(throwable) { "[$prefix] ${message()}" }

    override fun isLoggingEnabledFor(
        level: Level,
        marker: Marker?,
    ): Boolean = LogLevelManager.getLevel().equals(level)

    override fun warn(
        throwable: Throwable?,
        message: () -> Any?,
    ) = delegate.warn(throwable) { "[$prefix] ${message()}" }

    override fun error(
        throwable: Throwable?,
        message: () -> Any?,
    ) = delegate.error(throwable) { "[$prefix] ${message()}" }

    // at 方法实现
    // 提供更灵活的日志构建方式，允许构建复杂的日志构建块
    override fun at(
        level: Level,
        marker: Marker?,
        block: KLoggingEventBuilder.() -> Unit,
    ) {
        delegate.at(level, marker) {
            val originalMessage = message
            // 在原有消息前添加自定义前缀
            message = "[$prefix] $originalMessage"
            // 执行日志构建块的代码
            block()
        }
    }
}
