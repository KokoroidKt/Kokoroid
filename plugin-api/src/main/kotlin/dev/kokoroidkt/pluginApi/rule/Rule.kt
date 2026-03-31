// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.rule

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.user.Users
import kotlin.reflect.KFunction

/**
 * Rule函数类型
 * 它必须得是一个suspend函数
 */
typealias RuleFunction = KFunction<Boolean>

fun interface Rule {
    /**
     * 检查规则。
     * @return 如果规则通过则返回 true，否则返回 false。
     */
    suspend fun check(
        bot: Bot?,
        event: Event,
        messageChain: MessageChain?,
        users: Users?,
    ): Boolean
}
