package dev.kokoroidkt.pluginApi.dsl

import dev.kokoroidkt.pluginApi.conversation.command.CommandItem
import dev.kokoroidkt.pluginApi.conversation.command.CommandProcessor
import dev.kokoroidkt.pluginApi.rule.RuleChain
import kotlin.reflect.KFunction

class CommandBuilder(
    val keyword: String,
    val prefix: Char,
) {
    private var rules: RuleChain = RuleChain()
    private val children = mutableMapOf<String, CommandItem>()
    private var kFunction: KFunction<*>? = null

    fun setProcessor(func: KFunction<*>) {
        kFunction = func
    }

    fun setProcessor(block: CommandBuilder.() -> KFunction<*>) {
        kFunction = block()
    }

    fun addRule(block: RuleCollection.() -> Unit) {
        rules += rule(block)
    }

    fun child(
        childKeyword: String,
        block: CommandBuilder.() -> Unit,
    ) {
        children[childKeyword] = CommandBuilder(childKeyword, prefix).apply(block).build().root
    }

    fun build(): CommandProcessor =
        CommandProcessor(
            prefix = prefix,
            root =
                CommandItem(
                    children,
                    keyword,
                    kFunction!!,
                    children.isNotEmpty(),
                    rules,
                ),
        )
}

fun command(
    keyword: String,
    prefix: Char = '/',
    block: CommandBuilder.() -> Unit,
): CommandProcessor = CommandBuilder(keyword, prefix).apply(block).build()
