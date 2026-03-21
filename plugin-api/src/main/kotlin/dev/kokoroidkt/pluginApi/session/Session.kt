// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.session

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.pluginApi.conversation.ConversationOrchestrator
import kotlinx.coroutines.CompletableDeferred

interface Session {
    val users: UserGroup
    var state: SessionState

    suspend fun process(
        event: Event,
        bot: Bot,
    ): CompletableDeferred<Unit>
}
