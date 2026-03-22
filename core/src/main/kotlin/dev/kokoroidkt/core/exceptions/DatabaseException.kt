package dev.kokoroidkt.core.exceptions

import dev.kokoroidkt.coreApi.exceptions.CriticalException

abstract class DatabaseException(
    message: String,
    cause: Throwable?,
) : CriticalException(message, cause)
