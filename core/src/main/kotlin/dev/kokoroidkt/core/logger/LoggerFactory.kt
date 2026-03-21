// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.logger

import dev.kokoroidkt.coreApi.logging.KokoroidLogger

internal fun Any.getLogger(prefix: String): KokoroidLogger = DefaultKokoroidLogger(this::class.simpleName.toString(), prefix)

internal fun Any.getLogger(): KokoroidLogger = DefaultKokoroidLogger(this::class.simpleName.toString(), this::class.simpleName.toString())
