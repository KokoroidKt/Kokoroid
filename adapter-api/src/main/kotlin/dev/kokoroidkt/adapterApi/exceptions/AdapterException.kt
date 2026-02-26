/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.adapterApi.exceptions

import dev.kokoroidkt.adapterApi.adapter.AdapterContainer
import dev.kokoroidkt.coreApi.exceptions.KokoroidException

abstract class AdapterException(
    message: String,
    cause: Throwable? = null,
    causeByAdapter: AdapterContainer? = null,
) : KokoroidException(message, cause = cause)
