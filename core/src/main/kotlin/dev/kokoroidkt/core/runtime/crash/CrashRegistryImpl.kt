// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.runtime.crash

import dev.kokoroidkt.core.config.Config
import dev.kokoroidkt.core.constants.ExitStatus
import dev.kokoroidkt.core.logger.getLogger
import dev.kokoroidkt.core.runtime.state.InternalState
import dev.kokoroidkt.core.runtime.state.RuntimeState
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.exceptions.CriticalException
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class CrashRegistryImpl :
    CrashRegistry,
    KoinComponent {
    private var callStopped: Boolean = false
    private var lock = ReentrantLock()
    private val config: Config by inject()
    private val logger = getLogger("CrashReport")

    override val isCrashed: Boolean
        get() = records.isNotEmpty()

    val records = mutableListOf<CrashRecord>()
    val runtimeState = getKoin().get<RuntimeState>()
    var _exitCode: Int = ExitStatus.CRITICAL_ERROR_EXIT

    override val exitCode get() = _exitCode

    override fun recordAndRequestStop(
        err: CriticalException,
        event: Event?,
        exitCode: Int,
    ) {
        this._exitCode = exitCode
        lock.withLock {
            records.add(
                CrashRecord(
                    err = err,
                    kokoroidState = runtimeState.state,
                    event = event,
                ),
            )
            if (!callStopped) {
                logger.error(err) { "Crash occurred, stopping Kokoroid" }
                runtimeState.state =
                    InternalState
                        .BeforeStopping()
                callStopped = true
            }
        }
    }

    override fun logRecords() {
        for (record in records) {
            logger.error { "# Crash $record" }
            logger.error(record.err) {}
        }
    }
}
