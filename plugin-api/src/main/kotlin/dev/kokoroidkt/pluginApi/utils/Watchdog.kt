// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.utils

import dev.kokoroidkt.pluginApi.conversation.ConversationScope
import dev.kokoroidkt.pluginApi.exceptions.SessionTimeoutException
import dev.kokoroidkt.pluginApi.session.Session
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.milliseconds

internal fun ConversationScope.startTimeoutWatchdog(
    timeoutMilli: Long?,
    continuation: CancellableContinuation<*>,
    session: Session,
) {
    timeoutMilli?.let { if (it < 0) throw IllegalStateException("Timeout must be greater than 0") }
    if (timeoutMilli != null) {
        launch {
            delay(timeoutMilli.milliseconds)
            if (continuation.isActive) {
                continuation.resumeWithException(
                    SessionTimeoutException("Session $session time out: already waited $timeoutMilli seconds."),
                )
            }
        }
    }
}
