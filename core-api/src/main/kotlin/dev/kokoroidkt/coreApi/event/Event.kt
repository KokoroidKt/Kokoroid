// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.coreApi.event

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.user.UserGroup
import java.time.Instant

abstract class Event(
    /**
     * 事件ID，应保证唯一
     * 推荐格式：事件类型:平台唯一识别码@Adapter完全类名
     */
    val eventId: String,
    /**
     * 事件被处理时的时间戳
     */
    val timestamp: Instant,
    /**
     * 触发事件的用户组
     *
     */
    val users: UserGroup,
    /**
     * 收到这个信息的bot
     */
    val bot: Bot,
) {
    /**
     * 事件是否被停止向下传播
     */
    var propagationStopped: Boolean = false
        protected set

    /**
     * 停止传播事件
     */
    fun stopPropagation() {
        propagationStopped = true
    }

    override fun toString() = "Event[$eventId#$timestamp]"
}
