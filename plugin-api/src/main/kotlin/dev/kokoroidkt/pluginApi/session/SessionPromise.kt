// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.session

import kotlinx.coroutines.Deferred

data class SessionPromise(
    val session: Session,
    val deferred: Deferred<Unit>,
)
