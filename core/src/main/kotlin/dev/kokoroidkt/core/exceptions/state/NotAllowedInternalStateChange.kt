// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.exceptions.state

import dev.kokoroidkt.core.runtime.state.InternalState
import dev.kokoroidkt.coreApi.exceptions.CriticalException

class NotAllowedInternalStateChange(
    cause: Throwable? = null,
    val from: InternalState,
    val to: InternalState,
) : CriticalException(cause) {
    override val message: String
        get() = "Not allowed state change from $from to $to"
}
