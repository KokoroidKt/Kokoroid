/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.factory

import dev.kokoroidkt.core.session.DefaultSessionImpl
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.pluginApi.conversation.ConversationOrchestrator
import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.session.Session
import dev.kokoroidkt.pluginApi.session.container.SessionFactoty

class SessionFactoryImpl : SessionFactoty {
    override fun createSession(
        user: UserGroup,
        processor: Processor,
        conversationOrchestrator: ConversationOrchestrator,
    ): Session = DefaultSessionImpl(processor = processor, users = user, conversationOrchestrator = conversationOrchestrator)
}
