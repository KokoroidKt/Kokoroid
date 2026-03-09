/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.session

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.event.MessageEvent
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.pluginApi.conversation.ConversationContext
import dev.kokoroidkt.pluginApi.conversation.ConversationOrchestrator
import dev.kokoroidkt.pluginApi.conversation.ConversationScope
import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.session.Session
import dev.kokoroidkt.pluginApi.session.SessionState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlin.coroutines.resume

class DefaultSessionImpl(
    private var _state: SessionState = SessionState.Alive(),
    private val processor: Processor,
    override val users: UserGroup,
    conversationOrchestrator: ConversationOrchestrator,
) : Session {
    override var state: SessionState
        @Synchronized
        get() = _state

        @Synchronized
        set(value) {
            _state = value
        }
    val context = ConversationContext(users, this, conversationOrchestrator, CompletableDeferred<Unit>())

    override fun toString() = "SessionImpl(state: SessionState=$state, users=${users.joinToString { ", " }})"

    override fun hashCode(): Int = users.hashCode()

    override fun equals(other: Any?): Boolean = other.hashCode() == this.hashCode()

    override suspend fun process(
        event: Event,
        bot: Bot,
    ): CompletableDeferred<Unit> {
        val deferred = CompletableDeferred<Unit>()
        context.deferred = deferred
        ConversationScope(context).launch {
            when (state) {
                is SessionState.Alive -> {
                    processor.tryCallSuspend(event, bot, users, this@DefaultSessionImpl)
                    if (state is SessionState.Alive) {
                        throw IllegalStateException("Session is already alive after processing event")
                    }
                    deferred.complete(Unit)
                    context.deferred.complete(Unit)
                }

                is SessionState.Finished -> {
                    deferred.complete(Unit)
                    context.deferred.complete(Unit)
                }

                is SessionState.WaitingFor -> {
                    val checkResult =
                        (state as SessionState.WaitingFor).rules.check(
                            bot,
                            event,
                            if (event is MessageEvent) event.messageChain else null,
                            event.users,
                        )
                    if (!checkResult) {
                        return@launch
                    }
                    when (val waitItem = (state as SessionState.WaitingFor).item) {
                        is SessionState.WaitingFor.Item.EventItem -> {
                            with(waitItem) {
                                if (eventClass.isInstance(event)) {
                                    continuation.resume(event)
                                }
                            }
                        }

                        is SessionState.WaitingFor.Item.MessageItem -> {
                            with(waitItem) {
                                if (event is MessageEvent) {
                                    continuation.resume(event.messageChain)
                                }
                            }
                        }
                    }
                }
            }
        }
        return deferred
    }
}
