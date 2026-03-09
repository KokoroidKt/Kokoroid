package dev.kokoroidkt.pluginApi.utils

import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.pluginApi.conversation.ConversationScope
import dev.kokoroidkt.pluginApi.exceptions.SessionTimeoutException
import dev.kokoroidkt.pluginApi.session.Session
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resumeWithException

internal fun ConversationScope.startTimeoutWatchdog(
    timeoutMilli: Long?,
    continuation: CancellableContinuation<*>,
    session: Session,
) {
    timeoutMilli?.let { if (it < 0) throw IllegalStateException("Timeout must be greater than 0") }
    if (timeoutMilli != null) {
        launch {
            delay(timeoutMilli)
            if (continuation.isActive) {
                continuation.resumeWithException(
                    SessionTimeoutException("Session $session time out: already waited $timeoutMilli seconds."),
                )
            }
        }
    }
}
