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

/**
 * 等待一个事件
 * 处理到此处时，事件循环会放行事件向下传递，而你的Processor函数会被挂起
 *
 * @param eventClass 被等待的事件的类型
 * @param timeoutMilli 最大等待时长，单位毫秒，不设置则无限制
 * @param userGroup 只接受哪些用户的事件，默认为当前会话的用户
 * @return 你所等待的事件
 */
suspend fun waitForEvent(
    eventClass: KClass<out Event>,
    timeoutMilli: Long? = null,
    userGroup: UserGroup? = null,
): Event {
    val conversationContext =
        currentCoroutineContext()[ConversationContext.Key]
            ?: throw IllegalStateException("ConversationContext not found in coroutine context")
    val scope = ConversationScope(conversationContext)
    return scope.waitForEvent(eventClass, timeoutMilli, userGroup)
}

/**
 * 等待一个事件
 * 处理到此处时，事件循环会放行事件向下传递，而你的Processor函数会被挂起
 *
 * @param timeoutMilli 最大等待时长，单位毫秒，不设置则无限制
 * @param userGroup 只接受哪些用户的事件，默认为当前会话的用户
 * @return 你所等待的事件
 */
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
        if (session.state is SessionState.Finished) throw IllegalStateException("Session $session is already finished")

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

        session.state =
            SessionState.WaitingFor(
                SessionState.WaitingFor.Item.EventItem(
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
