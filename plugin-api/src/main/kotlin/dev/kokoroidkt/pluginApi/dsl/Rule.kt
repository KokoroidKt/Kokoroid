// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.dsl

import dev.kokoroidkt.pluginApi.rule.*

@Suppress("UNCHECKED_CAST")
class RuleCollection {
    val items: MutableList<RuleInfo> = mutableListOf()

    fun with(
        priority: Int = 1000,
        rule: RuleFunction,
    ) {
        items.add(RuleWrapper(rule) to priority)
    }

    fun with(
        priority: Int = 1000,
        block: suspend RuleContext.() -> Boolean,
    ) {
        items.add(
            Rule { bot, event, messageChain, user ->
                RuleContext(event, messageChain, user, bot).block()
            } to priority,
        )
    }
}

fun rule(block: RuleCollection.() -> Unit): RuleChain {
    val receiver = RuleCollection()
    receiver.block()
    val chain = RuleChain()
    chain.addRules(receiver.items)
    return chain
}
