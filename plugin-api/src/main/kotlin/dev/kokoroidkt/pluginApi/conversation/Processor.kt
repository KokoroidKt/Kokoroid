// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.conversation

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.event.MessageEvent
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.user.User
import dev.kokoroidkt.coreApi.user.Users
import dev.kokoroidkt.pluginApi.Processable
import dev.kokoroidkt.pluginApi.conversation.status.ProcessorStatus
import dev.kokoroidkt.pluginApi.rule.RuleChain
import dev.kokoroidkt.pluginApi.session.Session
import dev.kokoroidkt.pluginApi.session.SessionState
import dev.kokoroidkt.pluginApi.task.BackgroundTask
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.starProjectedType

class Processor(
    val function: KFunction<*>,
    val rules: RuleChain,
) : Processable {
    val returnTypeWarps: (Any?) -> Reply =
        run {
            when {
                function.returnType.isSubtypeOf(Unit::class.starProjectedType)
                -> {
                    { _ -> Reply.NoReply }
                }

                function.returnType.isSubtypeOf(MessageChain::class.starProjectedType)
                -> {
                    { obj -> Reply.MessageChainReply(obj as MessageChain) }
                }

                function.returnType.isSubtypeOf(BackgroundTask::class.starProjectedType)
                -> {
                    { obj ->
                        @Suppress("UNCHECKED_CAST")
                        Reply.BackgroundTaskReply(obj as BackgroundTask)
                    }
                }

                function.returnType.isSubtypeOf(Reply::class.starProjectedType)
                -> {
                    { obj -> obj as Reply }
                }

                else
                -> {
                    throw IllegalArgumentException("Processor function return type error: ${function.returnType}")
                }
            }
        }

    val arguments: List<KParameter> by lazy {
        if (!function.isSuspend) throw IllegalArgumentException("Processor must be suspend function. caused by : ${function.name}")
        function
            .parameters
            .filter {
                it.type.isSubtypeOf(Event::class.starProjectedType) ||
                    it.type.isSubtypeOf(MessageChain::class.starProjectedType) ||
                    it.type.isSubtypeOf(User::class.starProjectedType) ||
                    it.type.isSubtypeOf(Bot::class.starProjectedType)
            }.toList()
    }

    override suspend fun tryCallSuspend(
        event: Event,
        bot: Bot,
        users: Users,
        session: Session,
    ): ProcessorStatus {
        // check
        if (arguments.any { it.type.isSubtypeOf(MessageEvent::class.starProjectedType) } && event !is MessageEvent) {
            return ProcessorStatus.Unmatched(MessageEvent::class.starProjectedType, event)
        }
        if (arguments.any { it.type.isSubtypeOf(MessageChain::class.starProjectedType) } && event !is MessageEvent) {
            return ProcessorStatus.Unmatched(MessageEvent::class.starProjectedType, event)
        }
        val messageChain = (event as? MessageEvent)?.messageChain

        // process
        val arg =
            arguments
                .map {
                    when {
                        it.type.isSupertypeOf(event::class.starProjectedType) -> {
                            it to event
                        }

                        it.type.isSupertypeOf(users::class.starProjectedType) -> {
                            it to users
                        }

                        messageChain != null && it.type.isSupertypeOf(messageChain::class.starProjectedType) -> {
                            it to messageChain
                        }

                        it.type.isSupertypeOf(bot::class.starProjectedType) -> {
                            it to bot
                        }

                        else -> {
                            session.state = SessionState.Finished(Reply.Unprocessed)
                            return ProcessorStatus.Unmatched(it.type, listOf(event, users, messageChain, bot))
                        }
                    }
                }.toMap()

        val result = function.callSuspendBy(arg)

        session.state = SessionState.Finished(returnTypeWarps(result))
        return ProcessorStatus.Processed
    }
}
