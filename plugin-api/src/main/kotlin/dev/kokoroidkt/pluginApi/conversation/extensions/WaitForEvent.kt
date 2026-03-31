// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.conversation.extensions

import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.user.Users
import dev.kokoroidkt.pluginApi.conversation.ConversationContext
import dev.kokoroidkt.pluginApi.conversation.ConversationScope
import dev.kokoroidkt.pluginApi.rule.RuleChain
import dev.kokoroidkt.pluginApi.session.SessionState
import dev.kokoroidkt.pluginApi.utils.startTimeoutWatchdog
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.reflect.KClass

/**
 * 等待一个事件
 * 处理到此处时，事件循环会放行事件向下传递，而你的Processor函数会被挂起
 *
 * @param eventClass 被等待的事件的类型
 * @param timeoutMilli 最大等待时长，单位毫秒，不设置则无限制
 * @param users 只接受哪些用户的事件，默认为当前会话的用户
 * @return 你所等待的事件
 */
suspend fun waitForEvent(
    eventClass: KClass<out Event>,
    timeoutMilli: Long? = null,
    users: Users? = null,
): Event {
    val conversationContext =
        currentCoroutineContext()[ConversationContext.Key]
            ?: throw IllegalStateException("ConversationContext not found in coroutine context")
    val scope = ConversationScope(conversationContext)
    return scope.waitForEvent(eventClass, timeoutMilli, users)
}

/**
 * 等待一个事件
 * 处理到此处时，事件循环会放行事件向下传递，而你的Processor函数会被挂起
 *
 * @param timeoutMilli 最大等待时长，单位毫秒，不设置则无限制
 * @param users 只接受哪些用户的事件，默认为当前会话的用户
 * @return 你所等待的事件
 */
suspend inline fun <reified T : Event> waitForEvent(
    timeoutMilli: Long? = null,
    users: Users? = null,
    rules: RuleChain = RuleChain(),
): T {
    val conversationContext =
        currentCoroutineContext()[ConversationContext.Key]
            ?: throw IllegalStateException("ConversationContext not found in coroutine context")
    val scope = ConversationScope(conversationContext)
    return scope.waitForEvent(T::class, timeoutMilli, users, rules) as T
}

suspend fun ConversationScope.waitForEvent(
    eventClass: KClass<out Event>,
    timeoutMilli: Long? = null,
    users: Users? = null,
    rules: RuleChain = RuleChain(),
): Event =
    suspendCancellableCoroutine { continuation ->

        val conversationContext =
            this.coroutineContext[ConversationContext.Key]
                ?: throw IllegalStateException("ConversationContext not found in coroutine context")
        // 立马写入会话
        val session = addSessionAndComplete(conversationContext, timeoutMilli, continuation)
        startTimeoutWatchdog(timeoutMilli, continuation, session)

        session.state =
            SessionState.WaitingFor(
                SessionState.WaitingFor.Item.EventItem(
                    eventClass,
                    users ?: session.users,
                    continuation,
                ),
                rules,
            )
    }

suspend inline fun <reified T : Event> ConversationScope.waitForEvent(
    timeout: Long,
    users: Users? = null,
    rules: RuleChain = RuleChain(),
): T = waitForEvent(T::class, timeout, users, rules) as T
