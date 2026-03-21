// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.rule

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.user.User
import dev.kokoroidkt.coreApi.user.UserGroup

/**
 * Rule信息，存优先级用
 */
typealias RuleInfo = Pair<Rule, Int>

class RuleChain {
    val rules: MutableList<RuleInfo> = mutableListOf()

    fun addRule(
        rule: RuleFunction,
        priority: Int = 5000,
    ): RuleChain {
        rules.add(Pair(RuleWrapper(rule), priority))
        return this
    }

    operator fun plus(other: RuleChain): RuleChain {
        rules += other.rules
        return this
    }

    suspend fun check(
        bot: Bot?,
        event: Event,
        messageChain: MessageChain?,
        user: UserGroup?,
    ): Boolean {
        rules
            .sortedByDescending { it.second }
            .forEach {
                if (!it.first.check(bot, event, messageChain, user)) return false
            }
        return true
    }

    fun addRules(vararg ruleInfo: RuleInfo): RuleChain {
        ruleInfo.forEach { rules.add(it) }
        return this
    }

    fun addRules(ruleInfos: List<RuleInfo>): RuleChain {
        ruleInfos.forEach { rules.add(it) }
        return this
    }
}
