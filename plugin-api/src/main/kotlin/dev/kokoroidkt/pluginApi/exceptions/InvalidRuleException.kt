// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.exceptions

class InvalidRuleException(
    message: String,
    cause: Throwable?,
) : RuleException(message, cause)
