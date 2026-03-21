// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

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
