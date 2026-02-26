/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.runtime.crash

import dev.kokoroidkt.core.config.Config
import dev.kokoroidkt.core.runtime.status.InternalStatus
import dev.kokoroidkt.core.runtime.status.RuntimeStatus
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.exceptions.CriticalException
import logger.getLogger
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
    val runtimeStatus = getKoin().get<RuntimeStatus>()

    override fun recordAndRequestStop(
        err: CriticalException,
        event: Event?,
    ) {
        lock.withLock {
            records.add(
                CrashRecord(
                    err = err,
                    kokoroidStatus = runtimeStatus.status,
                    event = event,
                ),
            )
            if (!callStopped) {
                logger.error(err) { "Crash occurred, stopping Kokoroid" }
                runtimeStatus.status =
                    InternalStatus
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
