/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.runtime.crash

import dev.kokoroidkt.core.runtime.state.InternalState
import dev.kokoroidkt.coreApi.event.Event

data class CrashRecord(
    val err: Throwable,
    val timestamp: Long = System.currentTimeMillis(),
    val kokoroidState: InternalState,
    val event: Event?,
) {
    override fun toString() =
        "CrashRecode" +
            "(" +
            "err=${err.javaClass.name}(${err.message}), " +
            "timestamp=$timestamp, " +
            "status=$kokoroidState, " +
            "event=${event?.javaClass?.simpleName ?: "null"}" +
            ")"
}
