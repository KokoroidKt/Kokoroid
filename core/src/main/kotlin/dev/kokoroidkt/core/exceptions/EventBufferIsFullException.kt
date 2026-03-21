// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.exceptions

class EventBufferIsFullException(
    val source: String,
    val retryTimes: Int?,
) : CoreException() {
    override val message: String
        get() = "$source Event buffer is full" + if (retryTimes != null) " Retrying times $retryTimes " else ""
}
