/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.runtime.state

import dev.kokoroidkt.core.exceptions.state.NotAllowedInternalStateChange
import logger.getLogger

class RuntimeState {
    private var _state: InternalState = InternalState.Initializing()
    private val logger = getLogger("RuntimeState")

    var state: InternalState
        get() = _state
        set(value) {
            if (!_state.checkIsAllowChangeStateTo(value) && value !is InternalState.BeforeStopping) {
                throw NotAllowedInternalStateChange(
                    from = _state,
                    to = value,
                )
            }
            _state = value
            logger.debug { "Set Status to ${_state})" }
        }
}
