/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.exceptions.status

import dev.kokoroidkt.core.exceptions.CoreException
import dev.kokoroidkt.pluginApi.session.SessionStatus

class ErrorSessionStatusException(
    require: SessionStatus,
    acuall: SessionStatus,
    cause: Throwable? = null,
) : CoreException("Require ${require::class.qualifiedName}, Acuall ${acuall::class.qualifiedName}", cause)
