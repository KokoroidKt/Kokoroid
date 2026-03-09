/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.conversation

import dev.kokoroidkt.coreApi.user.User
import dev.kokoroidkt.coreApi.user.UserGroup
import dev.kokoroidkt.pluginApi.session.Session
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlin.coroutines.CoroutineContext

/**
 * Kokoroid 对话的协程上下文元素
 * 在每一次对话创建
 * 存储当前对话相关的元数据
 * @constructor 创建一个空的协程上下文
 */
class ConversationContext(
    /**
     * 此对话上下文的用户
     * 如果Users为空，则代表开启本次对话的事件无法拥有上下文（如：群聊解散）
     * 这个用户信息应该携带在事件里面
     */
    val currentUsers: UserGroup,
    /**
     * 当前事件
     */
    val session: Session,
    val conversationOrchestrator: ConversationOrchestrator,
    var deferred: CompletableDeferred<Unit>,
) : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<ConversationContext>

    override val key: CoroutineContext.Key<*> = Key
}
