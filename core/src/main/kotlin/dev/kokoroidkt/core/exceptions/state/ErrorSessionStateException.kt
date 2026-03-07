/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.exceptions.state

import dev.kokoroidkt.core.exceptions.CoreException
import dev.kokoroidkt.pluginApi.session.SessionState

class ErrorSessionStateException(
    require: SessionState,
    acuall: SessionState,
    cause: Throwable? = null,
) : CoreException("Require ${require::class.qualifiedName}, Acuall ${acuall::class.qualifiedName}", cause)
