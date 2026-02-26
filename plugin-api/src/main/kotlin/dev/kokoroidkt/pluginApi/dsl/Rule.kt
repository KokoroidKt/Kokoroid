/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.dsl

import dev.kokoroidkt.pluginApi.rule.Rule
import dev.kokoroidkt.pluginApi.rule.RuleChain
import dev.kokoroidkt.pluginApi.rule.RuleContext
import dev.kokoroidkt.pluginApi.rule.RuleFunction
import dev.kokoroidkt.pluginApi.rule.RuleInfo
import dev.kokoroidkt.pluginApi.rule.RuleWrapper

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
