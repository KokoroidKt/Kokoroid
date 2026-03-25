// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.session.container

import dev.kokoroidkt.coreApi.user.Users
import dev.kokoroidkt.pluginApi.conversation.ConversationOrchestrator
import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.session.Session

fun interface
SessionFactoty {
    fun createSession(
        user: Users,
        processor: Processor,
        conversationOrchestrator: ConversationOrchestrator,
    ): Session
}
