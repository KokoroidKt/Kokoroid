// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.exceptions.state

import dev.kokoroidkt.core.exceptions.CoreException
import dev.kokoroidkt.pluginApi.session.SessionState

class ErrorSessionStateException(
    require: SessionState,
    acuall: SessionState,
    cause: Throwable? = null,
) : CoreException("Require ${require::class.qualifiedName}, Acuall ${acuall::class.qualifiedName}", cause)
