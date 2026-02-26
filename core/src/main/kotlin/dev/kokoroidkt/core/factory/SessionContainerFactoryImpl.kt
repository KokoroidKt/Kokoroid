/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.factory

import dev.kokoroidkt.core.config.Config
import dev.kokoroidkt.core.config.ConversationSessionsStoreType
import dev.kokoroidkt.core.session.container.CowSessionContainer
import dev.kokoroidkt.core.session.container.MutexSessionContainer
import dev.kokoroidkt.pluginApi.session.container.SessionContainer
import dev.kokoroidkt.pluginApi.session.container.SessionContainerFactory
import org.koin.core.component.KoinComponent

class SessionContainerFactoryImpl :
    SessionContainerFactory,
    KoinComponent {
    val config by lazy {
        getKoin().get<Config>()
    }

    override fun createSessionContainer(): SessionContainer =
        when (config.basic.performance.session.storeType) {
            ConversationSessionsStoreType.MUTEX -> MutexSessionContainer()
            ConversationSessionsStoreType.COW -> CowSessionContainer()

            // ConversationSessionsStoreType.SHARDED_COW -> TODO()
        }
}
