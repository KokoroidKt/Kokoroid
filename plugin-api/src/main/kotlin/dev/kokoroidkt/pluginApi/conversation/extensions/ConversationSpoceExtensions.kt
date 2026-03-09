/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.conversation.extensions

import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.pluginApi.conversation.ConversationContext
import dev.kokoroidkt.pluginApi.conversation.ConversationScope
import dev.kokoroidkt.pluginApi.exceptions.SessionTimeoutException
import dev.kokoroidkt.pluginApi.session.SessionState
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException
import kotlin.reflect.KClass

fun ConversationScope.getCurrentUser(): UserGroup {
    val conversationContext: ConversationContext =
        this.coroutineContext[ConversationContext.Key]
            ?: throw IllegalStateException("ConversationContext not found in coroutine context")
    return conversationContext.currentUsers
}
