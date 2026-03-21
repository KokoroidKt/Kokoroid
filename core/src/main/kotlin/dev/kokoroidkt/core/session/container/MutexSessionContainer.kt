// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.session.container

import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.pluginApi.conversation.ConversationOrchestrator
import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.session.Session
import dev.kokoroidkt.pluginApi.session.container.SessionContainer
import dev.kokoroidkt.pluginApi.session.container.SessionFactoty
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MutexSessionContainer :
    SessionContainer,
    KoinComponent {
    val sessions: MutableList<Session> = mutableListOf()
    val mutexLock: Mutex = Mutex()
    val sessionFactory: SessionFactoty by inject()

    override suspend fun getMatchedSession(event: Event): Session? {
        mutexLock.withLock {
            return sessions.find { event.users.any { u -> it.users.contains(u) } }
        }
    }

    override suspend fun getOrCreateSession(
        event: Event,
        processor: Processor,
        userGroup: UserGroup,
        orchestrator: ConversationOrchestrator,
    ): Session {
        mutexLock.withLock {
            val session = sessions.find { event.users.any { u -> it.users.contains(u) } }
            if (session != null) return session
            val newSession = sessionFactory.createSession(userGroup, processor, orchestrator)
            sessions.add(newSession)
            return newSession
        }
    }

    override suspend fun registerSession(session: Session) {
        mutexLock.withLock {
            val found = sessions.find { session == it }
            if (found != null) return
            sessions.add(session)
        }
    }

    override suspend fun unregisterSession(session: Session) {
        mutexLock.withLock {
            sessions.remove(session)
        }
    }

    override suspend fun snapshot(): List<Session> {
        mutexLock.withLock {
            return sessions.toList()
        }
    }
}
