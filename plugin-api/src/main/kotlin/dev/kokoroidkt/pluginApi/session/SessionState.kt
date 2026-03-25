// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.session

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.user.Users
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
                users: Users,
                bot: Bot,
            ): Boolean

            class EventItem(
                val eventClass: KClass<out Event>,
                val users: Users,
                continuation: CancellableContinuation<Event>,
            ) : Item<Event>(continuation) {
                override fun isNeedToProcess(
                    event: Event,
                    users: Users,
                    bot: Bot,
                ): Boolean = eventClass.isInstance(event) && users.any { u -> u in this@EventItem.users }
            }

            class MessageItem(
                val users: Users,
                continuation: CancellableContinuation<MessageChain>,
            ) : Item<MessageChain>(continuation) {
                override fun isNeedToProcess(
                    event: Event,
                    users: Users,
                    bot: Bot,
                ): Boolean = users.any { u -> u in this@MessageItem.users }
            }
        }
    }

    class Finished(
        val reply: Reply,
    ) : SessionState()
}
