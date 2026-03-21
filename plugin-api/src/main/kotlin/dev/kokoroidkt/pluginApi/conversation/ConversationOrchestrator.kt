// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

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
