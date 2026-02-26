/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package logger

import dev.kokoroidkt.coreApi.logging.KokoroidLogger

internal fun Any.getLogger(prefix: String): KokoroidLogger = DefaultKokoroidLogger(this::class.simpleName.toString(), prefix)

internal fun Any.getLogger(): KokoroidLogger = DefaultKokoroidLogger(this::class.simpleName.toString(), this::class.simpleName.toString())
