package dev.kokoroidkt.pluginApi.conversation.extensions

import dev.kokoroidkt.coreApi.message.MessageChain
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

/**
 * 等待一条消息
 * 处理到此处时，事件循环会放行事件向下传递，而你的Processor函数会被挂起
 *
 * @param userGroup 只接受哪些用户的消息，默认为当前会话的用户
 * @param timeoutMilli 最大等待时长，单位毫秒，不设置则无限制
 * @return
 */
suspend fun waitForMessage(
    userGroup: UserGroup? = null,
    timeoutMilli: Long? = null,
): MessageChain {
    val conversationContext =
        currentCoroutineContext()[ConversationContext.Key]
            ?: throw IllegalStateException("ConversationContext not found in coroutine context")
    val scope = ConversationScope(conversationContext)
    return scope.waitForMessage(userGroup, timeoutMilli)
}

/**
 * 等待一条消息
 * 处理到此处时，事件循环会放行事件向下传递，而你的Processor函数会被挂起
 *
 * @param userGroup 只接受哪些用户的消息，默认为当前会话的用户
 * @param timeoutMilli 最大等待时长，单位毫秒，不设置则无限制
 * @return
 */
suspend fun ConversationScope.waitForMessage(
    userGroup: UserGroup? = null,
    timeoutMilli: Long? = null,
): MessageChain =
    suspendCancellableCoroutine { continuation ->
        timeoutMilli?.let { if (it < 0) throw IllegalStateException("timeoutMilli must gather than 0") }
        val conversationContext =
            this.coroutineContext[ConversationContext.Key]
                ?: throw IllegalStateException("ConversationContext not found in coroutine context")
        // 立马写入会话
        val session = conversationContext.session
        launch {
            conversationContext.conversationOrchestrator.registerSession(session)
        }
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
        if (session.state is SessionState.Finished) throw IllegalStateException("Session $session is already finished")

        session.state = SessionState.WaitingFor(SessionState.WaitingFor.Item.MessageItem(userGroup ?: session.users, continuation))
    }
