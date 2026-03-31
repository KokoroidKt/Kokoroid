// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.conversation.extensions

import dev.kokoroidkt.coreApi.user.Users
import dev.kokoroidkt.pluginApi.conversation.ConversationContext
import dev.kokoroidkt.pluginApi.conversation.ConversationScope

fun ConversationScope.getCurrentUser(): Users {
    val conversationContext: ConversationContext =
        this.coroutineContext[ConversationContext.Key]
            ?: throw IllegalStateException("ConversationContext not found in coroutine context")
    return conversationContext.currentUsers
}
