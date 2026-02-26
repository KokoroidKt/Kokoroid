/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.conversation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KFunction

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
