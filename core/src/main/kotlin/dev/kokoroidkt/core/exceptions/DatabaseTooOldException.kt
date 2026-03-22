package dev.kokoroidkt.core.exceptions

import dev.kokoroidkt.coreApi.exceptions.CriticalException

class DatabaseTooOldException(
    message: String,
    cause: Throwable? = null,
) : CriticalException(message, cause)
