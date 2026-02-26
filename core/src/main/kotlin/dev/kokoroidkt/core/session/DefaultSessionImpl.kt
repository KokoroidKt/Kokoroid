/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.session

import dev.kokoroidkt.core.conversation.DefaultConversationOrchestrator
import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.event.MessageEvent
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.user.User
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.pluginApi.conversation.ConversationContext
import dev.kokoroidkt.pluginApi.conversation.ConversationOrchestrator
import dev.kokoroidkt.pluginApi.conversation.ConversationScope
import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.conversation.Reply
import dev.kokoroidkt.pluginApi.session.Session
import dev.kokoroidkt.pluginApi.session.SessionStatus
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.starProjectedType

class DefaultSessionImpl(
    private var _status: SessionStatus = SessionStatus.Alive(),
    private val processor: Processor,
    override val users: UserGroup,
) : Session {
    override var status: SessionStatus
        @Synchronized
        get() = _status

        @Synchronized
        set(value) {
            _status = value
        }

    override fun toString() = "SessionImpl(status: SessionStatus=$status, users=${users.joinToString { ", " }})"

    override fun hashCode(): Int = users.hashCode()

    override fun equals(other: Any?): Boolean = other.hashCode() == this.hashCode()

    override suspend fun process(
        conversationOrchestrator: ConversationOrchestrator,
        event: Event,
        bot: Bot,
    ): CompletableDeferred<Unit> {
        val deferred = CompletableDeferred<Unit>()
        ConversationScope(ConversationContext(users, this, conversationOrchestrator)).launch {
            when (status) {
                is SessionStatus.Alive -> {
                    processor.tryCallSuspend(event, bot, users, this@DefaultSessionImpl)
                    if (status is SessionStatus.Alive) {
                        throw IllegalStateException("Session is already alive after processing event")
                    }
                    deferred.complete(Unit)
                }

                is SessionStatus.Finished -> {
                    deferred.complete(Unit)
                }

                is SessionStatus.WaitingFor -> {
                    when (val waitItem = (status as SessionStatus.WaitingFor).item) {
                        is SessionStatus.WaitingFor.Item.EventItem -> {
                            with(waitItem) {
                                if (userGroup.any { it in this@DefaultSessionImpl.users } && eventClass.isInstance(event)) {
                                    continuation.resume(event)
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
