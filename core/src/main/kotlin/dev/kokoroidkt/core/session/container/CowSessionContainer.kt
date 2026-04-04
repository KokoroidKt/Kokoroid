// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.session.container

import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.user.Users
import dev.kokoroidkt.pluginApi.conversation.ConversationOrchestrator
import dev.kokoroidkt.pluginApi.conversation.Processable
import dev.kokoroidkt.pluginApi.session.Session
import dev.kokoroidkt.pluginApi.session.SessionState
import dev.kokoroidkt.pluginApi.session.container.SessionContainer
import dev.kokoroidkt.pluginApi.session.container.SessionFactoty
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.update

@OptIn(ExperimentalAtomicApi::class)
class CowSessionContainer :
    SessionContainer,
    KoinComponent {
    private val sessions: AtomicReference<List<Session>> = AtomicReference(listOf())
    private val sessionFactory: SessionFactoty by inject()

    override suspend fun getMatchedSession(event: Event): Session? =
        snapshot().firstOrNull { session ->
            event.users.any { u -> u in session.users }
        }

    override suspend fun getOrCreateSession(
        event: Event,
        processor: Processable,
        users: Users,
        orchestrator: ConversationOrchestrator,
    ): Session {
        while (true) {
            val cur = sessions.load()
            val found = cur.firstOrNull { it.state !is SessionState.Finished && it.users == event.users }
            if (found != null) return found

            val newSession = sessionFactory.createSession(users, processor, orchestrator)
            val next = cur + newSession

            if (sessions.compareAndSet(cur, next)) return newSession
        }
    }

    override suspend fun registerSession(session: Session) {
        while (true) {
            val cur = sessions.load()
            val found = cur.firstOrNull { it == session }
            if (found != null) return

            val next = cur + session

            if (sessions.compareAndSet(cur, next)) return
        }
    }

    override suspend fun unregisterSession(session: Session) {
        sessions.update { it - session }
    }

    override suspend fun snapshot(): List<Session> = sessions.load()
}
