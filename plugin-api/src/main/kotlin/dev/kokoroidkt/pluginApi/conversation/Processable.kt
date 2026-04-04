package dev.kokoroidkt.pluginApi.conversation

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.user.Users
import dev.kokoroidkt.pluginApi.conversation.status.ProcessorStatus
import dev.kokoroidkt.pluginApi.session.Session

interface Processable {
    suspend fun tryCallSuspend(
        event: Event,
        bot: Bot,
        users: Users,
        session: Session,
    ): ProcessorStatus

    fun name(): String
}
