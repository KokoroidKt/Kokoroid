/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.session

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.pluginApi.conversation.Reply
import dev.kokoroidkt.pluginApi.rule.RuleChain
import kotlinx.coroutines.CancellableContinuation
import kotlin.reflect.KClass

sealed class SessionState {
    class Alive : SessionState()

    class WaitingFor(
        val item: Item<*>,
        val rules: RuleChain,
    ) : SessionState() {
        sealed class Item<T>(
            val continuation: CancellableContinuation<T>,
        ) {
            abstract fun isNeedToProcess(
                event: Event,
                users: UserGroup,
                bot: Bot,
            ): Boolean

            class EventItem(
                val eventClass: KClass<out Event>,
                val userGroup: UserGroup,
                continuation: CancellableContinuation<Event>,
            ) : Item<Event>(continuation) {
                override fun isNeedToProcess(
                    event: Event,
                    users: UserGroup,
                    bot: Bot,
                ): Boolean = eventClass.isInstance(event) && users.any { u -> u in userGroup }
            }

            class MessageItem(
                val userGroup: UserGroup,
                continuation: CancellableContinuation<MessageChain>,
            ) : Item<MessageChain>(continuation) {
                override fun isNeedToProcess(
                    event: Event,
                    users: UserGroup,
                    bot: Bot,
                ): Boolean = users.any { u -> u in userGroup }
            }
        }
    }

    class Finished(
        val reply: Reply,
    ) : SessionState()
}
