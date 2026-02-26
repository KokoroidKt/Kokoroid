/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.session.container

import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.conversation.Reply
import dev.kokoroidkt.pluginApi.session.Session
import kotlin.reflect.KFunction

interface SessionContainer {
    suspend fun getMatchedSession(event: Event): Session?

    suspend fun getOrCreateSession(
        event: Event,
        processor: Processor,
        userGroup: UserGroup,
    ): Session

    suspend fun registerSession(session: Session)

    suspend fun unregisterSession(session: Session)

    suspend fun snapshot(): List<Session>
}
