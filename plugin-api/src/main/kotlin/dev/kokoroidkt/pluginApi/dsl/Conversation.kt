package dev.kokoroidkt.pluginApi.dsl

import dev.kokoroidkt.pluginApi.conversation.Processor
import dev.kokoroidkt.pluginApi.rule.Rule
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

    fun build(): Processor = Processor(kFunction!!, rules)
}

fun conversation(block: ConversationBuilder.() -> Unit): Processor = ConversationBuilder().apply(block).build()
