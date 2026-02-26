/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.adapterApi.logger

import dev.kokoroidkt.adapterApi.adapter.Adapter
import dev.kokoroidkt.adapterApi.utils.getMetadata
import dev.kokoroidkt.coreApi.logging.LoggerFactory
import org.koin.java.KoinJavaComponent.getKoin

fun Adapter.getLogger() = getKoin().get<LoggerFactory>().invoke(this.getMetadata()?.name.toString(), this::class)
