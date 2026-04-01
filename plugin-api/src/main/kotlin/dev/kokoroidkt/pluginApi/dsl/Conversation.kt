// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.dsl

import dev.kokoroidkt.pluginApi.Processable
import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.rule.RuleChain
import kotlin.reflect.KFunction

class ConversationBuilder {
    private var kFunction: KFunction<*>? = null
    private var rules: RuleChain = RuleChain()

    fun setProcessor(func: KFunction<*>) {
        kFunction = func
    }

    fun setProcessor(block: ConversationBuilder.() -> KFunction<*>) {
        kFunction = block()
    }

    fun addRule(block: RuleCollection.() -> Unit) {
        rules += rule(block)
    }

    fun build(): Processable = Processor(kFunction!!, rules)
}

fun conversation(block: ConversationBuilder.() -> Unit): Processable = ConversationBuilder().apply(block).build()
