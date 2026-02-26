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
import dev.kokoroidkt.pluginApi.session.SessionStatus
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

suspend fun waitForEvent(
    eventClass: KClass<out Event>,
    /**
     * 最大等待时长，单位毫秒
     * 若timeout为空，则默认无超时
     * timeout必须大于0
     * 超时时间到后，整个对话将关闭
     */
    timeoutMilli: Long? = null,
    userGroup: UserGroup? = null,
): Event {
    val conversationContext =
        currentCoroutineContext()[ConversationContext.Key]
            ?: throw IllegalStateException("ConversationContext not found in coroutine context")
    val scope = ConversationScope(conversationContext)
    return scope.waitForEvent(eventClass, timeoutMilli, userGroup)
}

suspend inline fun <reified T : Event> waitForEvent(
    /**
     * 最大等待时长，单位毫秒
     * 若timeout为空，则默认无超时
     * timeout必须大于0
     * 超时时间到后，整个对话将关闭
     */
    timeoutMilli: Long? = null,
    userGroup: UserGroup? = null,
): T {
    val conversationContext =
        currentCoroutineContext()[ConversationContext.Key]
            ?: throw IllegalStateException("ConversationContext not found in coroutine context")
    val scope = ConversationScope(conversationContext)
    return scope.waitForEvent(T::class, timeoutMilli, userGroup) as T
}

suspend fun ConversationScope.waitForEvent(
    eventClass: KClass<out Event>,
    /**
     * 最大等待时长，单位毫秒
     * 若timeout为空，则默认无超时
     * timeout必须大于0
     * 超时时间到后，整个对话将关闭
     */
    timeoutMilli: Long? = null,
    userGroup: UserGroup? = null,
): Event =
    suspendCancellableCoroutine { continuation ->
        timeoutMilli?.let { if (it < 0) throw IllegalStateException("Timeout must be greater than 0") }

        val conversationContext =
            this.coroutineContext[ConversationContext.Key]
                ?: throw IllegalStateException("ConversationContext not found in coroutine context")
        // 立马写入会话
        val session = conversationContext.session
        launch {
            conversationContext.conversationOrchestrator.registerSession(session)
        }
        if (session.status is SessionStatus.Finished) throw IllegalStateException("Session $session is already finished")

        if (timeoutMilli != null) {
            launch {
                delay(timeoutMilli)
                if (continuation.isActive) {
                    continuation.resumeWithException(
                        SessionTimeoutException("Session $session time out: already waited $timeoutMilli seconds."),
                    )
                }
            }
        }

        session.status =
            SessionStatus.WaitingFor(
                SessionStatus.WaitingFor.Item.EventItem(
                    eventClass,
                    userGroup ?: session.users,
                    continuation,
                ),
            )
    }

suspend inline fun <reified T : Event> ConversationScope.waitForEvent(
    timeout: Long,
    userGroup: UserGroup? = null,
): T = waitForEvent(T::class, timeout, userGroup) as T
