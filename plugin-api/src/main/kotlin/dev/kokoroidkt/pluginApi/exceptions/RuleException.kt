package dev.kokoroidkt.pluginApi.exceptions

import dev.kokoroidkt.coreApi.exceptions.KokoroidException

abstract class RuleException(
    message: String,
    cause: Throwable? = null,
) : KokoroidException(message, cause)
