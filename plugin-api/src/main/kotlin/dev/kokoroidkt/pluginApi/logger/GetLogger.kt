// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.logger

import dev.kokoroidkt.coreApi.logging.KokoroidLogger
import dev.kokoroidkt.coreApi.logging.LoggerFactory
import dev.kokoroidkt.pluginApi.plugin.Plugin
import dev.kokoroidkt.pluginApi.utils.metadata
import org.koin.java.KoinJavaComponent.getKoin

/**
 * 为插件获取一个日志记录器实例
 * @return [KokoroidLogger] Logger
 */
fun Plugin.getLogger(): KokoroidLogger =
    getKoin().get<LoggerFactory>().invoke(
        this.metadata().name,
        this::class,
    )
