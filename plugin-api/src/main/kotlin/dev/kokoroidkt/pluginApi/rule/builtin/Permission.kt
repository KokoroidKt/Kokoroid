package dev.kokoroidkt.pluginApi.rule.builtin

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.permission.PermissionModel
import dev.kokoroidkt.coreApi.user.Users
import dev.kokoroidkt.pluginApi.rule.Rule

fun requirePermission(
    permissionModel: PermissionModel,
    mode: PermissionModel.VerifyMode = PermissionModel.VerifyMode.USER_ONLY,
): Rule =
    @Suppress("ktlint:standard:max-line-length")
    object : Rule {
        override suspend fun check(
            bot: Bot?,
            event: Event,
            messageChain: MessageChain?,
            users: Users?,
        ): Boolean {
            users ?: return false
            for (user in users) {
                val result = permissionModel.verify(user, mode)
                if (result) return true
            }
            return false
        }
    }

fun withoutPermission(
    permissionModel: PermissionModel,
    mode: PermissionModel.VerifyMode = PermissionModel.VerifyMode.USER_ONLY,
): Rule =
    {
        bot,
        event,
        messageChain,
        users,
        ->
        !requirePermission(permissionModel, mode).check(bot, event, messageChain, users)
    }
