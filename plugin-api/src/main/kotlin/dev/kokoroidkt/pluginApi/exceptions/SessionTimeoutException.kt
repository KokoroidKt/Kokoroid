/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.exceptions

import dev.kokoroidkt.coreApi.exceptions.KokoroidException

class SessionTimeoutException(
    message: String = "",
    cause: Throwable? = null,
) : KokoroidException(message, cause)
