// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroid.transport.raw

data class Raw(
    val data: Data,
    val attribute: Map<String, String>,
)
