/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.conversation

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.pluginApi.session.Session
import dev.kokoroidkt.pluginApi.session.SessionPromise

abstract class ConversationOrchestrator {
    abstract suspend fun callSessionToProcessOrCreate(
        event: Event,
        bot: Bot,
    ): SessionPromise

    abstract suspend fun registerSession(session: Session)

    abstract fun getProcessorQualifiedName(): String

    abstract suspend fun isExist(session: Session): Boolean
}
