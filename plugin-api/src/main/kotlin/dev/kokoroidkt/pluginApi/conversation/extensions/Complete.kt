package dev.kokoroidkt.pluginApi.conversation.extensions

import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.pluginApi.conversation.ConversationContext
import dev.kokoroidkt.pluginApi.conversation.ConversationScope
import dev.kokoroidkt.pluginApi.session.Session
import dev.kokoroidkt.pluginApi.session.SessionState
import dev.kokoroidkt.pluginApi.utils.startTimeoutWatchdog
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.launch

internal fun ConversationScope.addSessionAndComplete(
    conversationContext: ConversationContext,
    timeoutMilli: Long?,
    continuation: CancellableContinuation<*>,
): Session {
    val session = conversationContext.session

    launch {
        conversationContext.conversationOrchestrator.registerSession(session)
    }
    conversationContext.deferred.complete(Unit)
    startTimeoutWatchdog(timeoutMilli, continuation, session)

    if (session.state is SessionState.Finished) throw IllegalStateException("Session $session is already finished")
    return session
}
