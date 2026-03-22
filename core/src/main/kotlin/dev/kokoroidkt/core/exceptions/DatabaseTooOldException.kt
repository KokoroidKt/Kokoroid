// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.core.exceptions

import dev.kokoroidkt.coreApi.exceptions.CriticalException

class DatabaseTooOldException(
    message: String,
    cause: Throwable? = null,
) : CriticalException(message, cause)
