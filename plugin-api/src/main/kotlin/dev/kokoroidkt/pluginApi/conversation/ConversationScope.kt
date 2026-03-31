// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.conversation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

/**
 * Kokoroid对话的协程作用域
 *
 * @property coroutineContext
 * @constructor Create empty Conversation spoce
 */
class ConversationScope(
    conversationContext: ConversationContext,
    parent: CoroutineContext? = null,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : CoroutineScope {
    override val coroutineContext: CoroutineContext =
        (parent ?: (SupervisorJob() + dispatcher)) + conversationContext
}
