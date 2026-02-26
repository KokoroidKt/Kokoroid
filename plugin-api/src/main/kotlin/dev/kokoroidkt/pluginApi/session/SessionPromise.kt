/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.session

import kotlinx.coroutines.Deferred

data class SessionPromise(
    val session: Session,
    val deferred: Deferred<Unit>,
)
