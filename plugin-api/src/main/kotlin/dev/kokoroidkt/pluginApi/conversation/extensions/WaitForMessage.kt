package dev.kokoroidkt.pluginApi.conversation.extensions

import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.pluginApi.conversation.ConversationContext
import dev.kokoroidkt.pluginApi.conversation.ConversationScope
import dev.kokoroidkt.pluginApi.rule.RuleChain
import dev.kokoroidkt.pluginApi.session.Session
import dev.kokoroidkt.pluginApi.session.SessionState
import dev.kokoroidkt.pluginApi.utils.startTimeoutWatchdog
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

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
    rules: RuleChain = RuleChain(),
): MessageChain {
    val conversationContext =
        currentCoroutineContext()[ConversationContext.Key]
            ?: throw IllegalStateException("ConversationContext not found in coroutine context")
    val scope = ConversationScope(conversationContext)
    return scope.waitForMessage(userGroup, timeoutMilli, rules)
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
    rules: RuleChain = RuleChain(),
): MessageChain =
    suspendCancellableCoroutine { continuation ->
        timeoutMilli?.let { if (it < 0) throw IllegalStateException("timeoutMilli must gather than 0") }
        val conversationContext =
            this.coroutineContext[ConversationContext.Key]
                ?: throw IllegalStateException("ConversationContext not found in coroutine context")
        // 立马写入会话，并完成deferred
        val session = addSessionAndComplete(conversationContext, timeoutMilli, continuation)

        session.state = SessionState.WaitingFor(SessionState.WaitingFor.Item.MessageItem(userGroup ?: session.users, continuation), rules)
    }
