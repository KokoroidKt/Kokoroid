/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.adapterApi.exceptions

import dev.kokoroidkt.adapterApi.adapter.AdapterContainer

class AdapterNotFoundException(
    message: String,
    cause: Throwable? = null,
    causeByAdapter: AdapterContainer? = null,
) : AdapterException(message, cause = cause)
