/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.logger

import dev.kokoroidkt.coreApi.logging.KokoroidLogger
import dev.kokoroidkt.coreApi.logging.LoggerFactory
import dev.kokoroidkt.pluginApi.plugin.Plugin
import dev.kokoroidkt.pluginApi.utils.metadata
import org.koin.java.KoinJavaComponent.getKoin

fun Plugin.getLogger(): KokoroidLogger =
    getKoin().get<LoggerFactory>().invoke(
        this.metadata()?.name.toString(),
        this::class,
    )
