/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.runtime.crash

import dev.kokoroidkt.core.runtime.status.InternalStatus
import dev.kokoroidkt.coreApi.event.Event

data class CrashRecord(
    val err: Throwable,
    val timestamp: Long = System.currentTimeMillis(),
    val kokoroidStatus: InternalStatus,
    val event: Event?,
) {
    override fun toString() =
        "CrashRecode" +
            "(" +
            "err=${err.javaClass.name}(${err.message}), " +
            "timestamp=$timestamp, " +
            "status=$kokoroidStatus, " +
            "event=${event?.javaClass?.simpleName ?: "null"}" +
            ")"
}
