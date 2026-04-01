package dev.kokoroidkt.pluginApi.conversation.command

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.user.User
import dev.kokoroidkt.pluginApi.conversation.Reply
import kotlin.reflect.KFunction

typealias CommandProcessFunc = KFunction<Reply>

data class CommandContexts(
    val bot: Bot,
    val event: Event,
    val user: User,
    val command: String,
)
