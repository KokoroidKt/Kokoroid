/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.session

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.pluginApi.conversation.ConversationOrchestrator
import dev.kokoroidkt.pluginApi.conversation.Reply
import kotlinx.coroutines.CompletableDeferred

interface Session {
    val users: UserGroup
    var status: SessionStatus

    suspend fun process(
        conversationOrchestrator: ConversationOrchestrator,
        event: Event,
        bot: Bot,
    ): CompletableDeferred<Unit>
}
