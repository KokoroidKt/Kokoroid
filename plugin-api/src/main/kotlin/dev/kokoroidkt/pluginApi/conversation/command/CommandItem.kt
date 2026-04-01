package dev.kokoroidkt.pluginApi.conversation.command

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.event.MessageEvent
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.user.User
import dev.kokoroidkt.pluginApi.conversation.Reply
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

data class CommandItem(
    val children: Map<String, CommandItem>,
    val thisKeyword: String,
    val thisProcessor: KFunction<*>,
    val goDown: Boolean,
    val rules: RuleChain = RuleChain(),
) {
    val returnTypeWarps: (Any?) -> Reply =
        run {
            when {
                thisProcessor.returnType.isSubtypeOf(Unit::class.starProjectedType)
                -> {
                    { _ -> Reply.NoReply }
                }

                thisProcessor.returnType.isSubtypeOf(MessageChain::class.starProjectedType)
                -> {
                    { obj -> Reply.MessageChainReply(obj as MessageChain) }
                }

                thisProcessor.returnType.isSubtypeOf(BackgroundTask::class.starProjectedType)
                -> {
                    { obj ->
                        @Suppress("UNCHECKED_CAST")
                        Reply.BackgroundTaskReply(obj as BackgroundTask)
                    }
                }

                thisProcessor.returnType.isSubtypeOf(Reply::class.starProjectedType)
                -> {
                    { obj -> obj as Reply }
                }

                else
                -> {
                    throw IllegalArgumentException("Processor thisProcessor return type error: ${thisProcessor.returnType}")
                }
            }
        }

    val arguments: List<KParameter> by lazy {
        if (!thisProcessor.isSuspend) {
            throw IllegalArgumentException("Processor must be suspend thisProcessor. caused by : ${thisProcessor.name}")
        }
        thisProcessor
            .parameters
            .filter {
                it.kind == KParameter.Kind.VALUE && (
                    it.type.isSubtypeOf(Event::class.starProjectedType) ||
                        it.type.isSubtypeOf(MessageEvent::class.starProjectedType) ||
                        it.type.isSubtypeOf(MessageChain::class.starProjectedType) ||
                        it.type.isSubtypeOf(User::class.starProjectedType) ||
                        it.type.isSubtypeOf(Bot::class.starProjectedType) ||
                        (
                            it.type.isSubtypeOf(List::class.starProjectedType) &&
                                it.type.arguments
                                    .firstOrNull()
                                    ?.type
                                    ?.isSubtypeOf(CommandArg::class.starProjectedType) == true
                        )
                )
            }.toList()
    }

    suspend fun execute(
        args: List<CommandArg>,
        bot: Bot,
        event: MessageEvent,
        user: User,
        rawChain: MessageChain,
        session: Session,
    ): ProcessorStatus {
        if (!rules.check(
                bot,
                event,
                rawChain,
                listOf(user),
            )
        ) {
            return ProcessorStatus.Unmatched(event::class.starProjectedType, event)
        }
        val arg =
            arguments
                .mapNotNull {
                    when {
                        it.type.isSupertypeOf(event::class.starProjectedType) ||
                            it.type.isSupertypeOf(MessageEvent::class.starProjectedType) ||
                            it.type.isSupertypeOf(Event::class.starProjectedType) -> {
                            it to event
                        }

                        it.type.isSupertypeOf(user::class.starProjectedType) -> {
                            it to user
                        }

                        it.type.isSupertypeOf(rawChain::class.starProjectedType) -> {
                            it to rawChain
                        }

                        it.type.isSupertypeOf(bot::class.starProjectedType) -> {
                            it to bot
                        }

                        it.type.isSubtypeOf(List::class.starProjectedType) &&
                            it.type.arguments
                                .firstOrNull()
                                ?.type
                                ?.isSubtypeOf(CommandArg::class.starProjectedType) == true
                        -> {
                            it to args
                        }

                        else -> {
                            null
                        }
                    }
                }.toMap()

        if (arg.size != arguments.size) {
            session.state = SessionState.Finished(Reply.Unprocessed)
            return ProcessorStatus.Unmatched(arguments.first { it !in arg.keys }.type, listOf(event, user, rawChain, bot))
        }

        val result = thisProcessor.callSuspendBy(arg)
        session.state = SessionState.Finished(returnTypeWarps(result))

        return ProcessorStatus.Processed
    }
}
