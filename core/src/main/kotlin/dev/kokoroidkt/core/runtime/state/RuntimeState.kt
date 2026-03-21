// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.runtime.state

import dev.kokoroidkt.core.exceptions.state.NotAllowedInternalStateChange
import dev.kokoroidkt.core.logger.getLogger

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
