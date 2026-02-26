/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.runtime.crash

import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.exceptions.CriticalException

interface CrashRegistry {
    fun recordAndRequestStop(
        err: CriticalException,
        event: Event?,
    )

    val isCrashed: Boolean

    fun logRecords()
}
