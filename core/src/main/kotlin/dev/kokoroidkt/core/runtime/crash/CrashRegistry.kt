// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.runtime.crash

import dev.kokoroidkt.core.constants.ExitStatus
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.exceptions.CriticalException
import kotlin.system.exitProcess

interface CrashRegistry {
    fun recordAndRequestStop(
        err: CriticalException,
        event: Event?,
        exitCode: Int = ExitStatus.CRITICAL_ERROR_EXIT,
    )

    fun stopNow(exitCode: Int) {
        exitProcess(exitCode)
    }

    val exitCode: Int

    val isCrashed: Boolean

    fun logRecords()
}
