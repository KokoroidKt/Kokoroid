/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.exceptions

class EventBufferIsFullException(
    val source: String,
    val retryTimes: Int?,
) : CoreException() {
    override val message: String
        get() = "$source Event buffer is full" + if (retryTimes != null) " Retrying times $retryTimes " else ""
}
