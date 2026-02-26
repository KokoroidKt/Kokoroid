/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.exceptions

class InvalidRuleException(
    message: String,
    cause: Throwable?,
) : RuleException(message, cause)
