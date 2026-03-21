// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.adapterApi.logger

import dev.kokoroidkt.adapterApi.adapter.Adapter
import dev.kokoroidkt.adapterApi.utils.getMetadata
import dev.kokoroidkt.coreApi.logging.LoggerFactory
import org.koin.java.KoinJavaComponent.getKoin

fun Adapter.getLogger() = getKoin().get<LoggerFactory>().invoke(this.getMetadata()?.name.toString(), this::class)
