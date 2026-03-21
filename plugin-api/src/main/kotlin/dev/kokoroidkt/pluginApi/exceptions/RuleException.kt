// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.exceptions

import dev.kokoroidkt.coreApi.exceptions.KokoroidException

abstract class RuleException(
    message: String,
    cause: Throwable? = null,
) : KokoroidException(message, cause)
