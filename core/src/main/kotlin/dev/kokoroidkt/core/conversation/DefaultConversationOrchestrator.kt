/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.conversation

import dev.kokoroidkt.core.config.Config
import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.pluginApi.conversation.ConversationOrchestrator
import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.session.Session
import dev.kokoroidkt.pluginApi.session.SessionPromise
import dev.kokoroidkt.pluginApi.session.container.SessionContainer
import dev.kokoroidkt.pluginApi.session.container.SessionFactoty
import kotlinx.coroutines.CompletableDeferred
import logger.getLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.ConcurrentHashMap

class DefaultConversationOrchestrator(
    val processor: Processor,
) : ConversationOrchestrator(),
    KoinComponent {
    val config: Config by inject()
    val id: String = "ConversationOrchestrator{${processor.function.name}}"
    val logger = getLogger(id)

    private val promiseMap: MutableMap<Session, CompletableDeferred<Unit>> = ConcurrentHashMap()
    val sessionContainer: SessionContainer by inject()

    private val sessionFactory: SessionFactoty by inject()

    override suspend fun callSessionToProcessOrCreate(
        event: Event,
        bot: Bot,
    ): SessionPromise {
        val session =
            sessionContainer.getMatchedSession(event)
                ?: sessionFactory.createSession(event.users, processor)

        var deferred = session.process(this, event, bot)
        val existingDeferred = promiseMap[session]
        if (existingDeferred != null && existingDeferred.isActive) {
            deferred = existingDeferred
        } else {
            promiseMap[session] = deferred
        }
        logger.debug { "session $session: state -> ${session.state::class.qualifiedName}" }
        return SessionPromise(session, deferred)
    }

    override suspend fun registerSession(session: Session) {
        sessionContainer.registerSession(session)
    }

    override fun getProcessorQualifiedName(): String = processor.function.name

    override suspend fun isExist(session: Session): Boolean = sessionContainer.snapshot().any { session == it }
}
