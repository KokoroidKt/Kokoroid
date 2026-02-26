/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.exceptions.status

import dev.kokoroidkt.core.runtime.status.InternalStatus
import dev.kokoroidkt.coreApi.exceptions.CriticalException

class NotAllowedInternalStatusChange(
    cause: Throwable? = null,
    val from: InternalStatus,
    val to: InternalStatus,
) : CriticalException(cause) {
    override val message: String
        get() = "Not allowed status change from $from to $to"
}
