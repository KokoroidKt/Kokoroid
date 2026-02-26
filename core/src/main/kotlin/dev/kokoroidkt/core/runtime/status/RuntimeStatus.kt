/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.runtime.status

import dev.kokoroidkt.core.exceptions.status.NotAllowedInternalStatusChange
import logger.getLogger
import java.util.concurrent.locks.Lock

class RuntimeStatus {
    private var _status: InternalStatus = InternalStatus.Initializing()
    private val logger = getLogger("RuntimeStatus")

    var status: InternalStatus
        get() = _status
        set(value) {
            if (!_status.checkIsAllowChangeStatusTo(value) && value !is InternalStatus.BeforeStopping) {
                throw NotAllowedInternalStatusChange(
                    from = _status,
                    to = value,
                )
            }
            _status = value
            logger.debug { "Set Status to ${_status})" }
        }
}
