// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.coreApi.logging

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KLoggingEventBuilder
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.oshai.kotlinlogging.Marker

/**
 * KokoroidLogger 接口是Kokoroid的日志包装
 * @param name 日志记录器的名称
 * @param prefix 添加到每条日志消息前的自定义前缀
 * @param delegate 实际执行日志记录的 KLogger 实例，默认使用 KotlinLogging 创建
 */
interface KokoroidLogger : KLogger {
    override val name: String
    val prefix: String
    val delegate: KLogger
}
