// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.pluginApi.conversation.command

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.event.MessageEvent
import dev.kokoroidkt.coreApi.message.TextConvertible
import dev.kokoroidkt.coreApi.message.builtin.TextMessage
import dev.kokoroidkt.coreApi.user.Users
import dev.kokoroidkt.pluginApi.Processable
import dev.kokoroidkt.pluginApi.conversation.status.ProcessorStatus
import dev.kokoroidkt.pluginApi.session.Session
import kotlin.reflect.full.starProjectedType

class CommandProcessor(
    val prefix: Char,
    val root: CommandItem,
) : Processable {
    var foundedCommandItem: CommandItem? = null

    override suspend fun tryCallSuspend(
        event: Event,
        bot: Bot,
        users: Users,
        session: Session,
    ): ProcessorStatus {
        // filter
        if (event !is MessageEvent) return ProcessorStatus.Unmatched(MessageEvent::class.starProjectedType, event)
        val plainText = event.messageChain.toPlainText()
        if (plainText.startsWith(prefix.toString()).not()) {
            return ProcessorStatus.Unmatched(prefix, plainText[0])
        }
        // process
        var currentCommandItem = root
        for (segIndex in event.messageChain.indices) {
            val seg = event.messageChain[segIndex]
            // 文字就切片
            if (seg is TextConvertible) {
                val args =
                    if (segIndex == 0) {
                        seg.toPlainText().removePrefix(prefix.toString()).split(" ")
                    } else {
                        seg.toPlainText().split(" ")
                    }
                for (argIndex in args.indices) {
                    val arg = args[argIndex]
                    if (currentCommandItem.thisKeyword == arg && !currentCommandItem.goDown) {
                        val leftArgs: MutableList<CommandArg> =
                            args
                                .subList(argIndex + 1, args.size)
                                .map {
                                    CommandArg.TextArg(it)
                                }.toMutableList()
                        leftArgs.addAll(
                            event.messageChain.subList(segIndex + 1, event.messageChain.size).flatMap {
                                if (it is TextMessage) {
                                    return@flatMap it.text.split(" ").map { text -> CommandArg.TextArg(text) }
                                } else {
                                    return@flatMap listOf(CommandArg.Segment(it))
                                }
                            },
                        )
                        foundedCommandItem = currentCommandItem
                        return foundedCommandItem!!.execute(
                            leftArgs,
                            bot,
                            event,
                            users.first(),
                            event.messageChain,
                            session,
                        )
                    }
                    if (argIndex + 1 < args.size) {
                        currentCommandItem = currentCommandItem.children[args[argIndex + 1]]!!
                    } else {
                        return ProcessorStatus.Unmatched(prefix, plainText[0])
                    }
                }
            }
        }
        return ProcessorStatus.Unmatched(prefix, plainText[0])
    }

    override fun name(): String = "CommandProcessor(root=${root.thisKeyword}, rootProcessor=${root.thisProcessor.name})"
}
