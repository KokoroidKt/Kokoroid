/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.rule

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.user.UserGroup
import kotlin.reflect.KParameter
import kotlin.reflect.full.callSuspendBy
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.starProjectedType

class RuleWrapper(
    private val rule: RuleFunction,
) : Rule {
    val arguments: List<KParameter> by lazy {
        if (!rule.isSuspend) throw IllegalArgumentException("Rule must be suspend function. caused by : ${rule.name}")
        rule
            .parameters
            .filter {
                it.type.isSubtypeOf(Event::class.starProjectedType) ||
                    it.type.isSubtypeOf(MessageChain::class.starProjectedType) ||
                    it.type.isSubtypeOf(UserGroup::class.starProjectedType) ||
                    it.type.isSubtypeOf(Bot::class.starProjectedType)
            }.toList()
    }

    override suspend fun check(
        bot: Bot?,
        event: Event,
        messageChain: MessageChain?,
        users: UserGroup?,
    ): Boolean {
        val arg =
            arguments
                .mapNotNull {
                    when {
                        it.type.isSupertypeOf(event::class.starProjectedType) -> it to event
                        users != null && it.type.isSupertypeOf(users::class.starProjectedType) -> it to users
                        messageChain != null && it.type.isSupertypeOf(messageChain::class.starProjectedType) -> it to messageChain
                        bot != null && it.type.isSupertypeOf(bot::class.starProjectedType) -> it to bot
                        else -> null
                    }
                }.toMap()
        return rule.callSuspendBy(arg)
    }
}
