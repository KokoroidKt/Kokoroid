/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.exceptions

import dev.kokoroidkt.coreApi.exceptions.KokoroidException

abstract class CoreException(
    message: String = "",
    cause: Throwable? = null,
) : KokoroidException(message, cause)
