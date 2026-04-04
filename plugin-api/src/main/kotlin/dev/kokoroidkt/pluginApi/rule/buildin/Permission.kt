package dev.kokoroidkt.pluginApi.rule.buildin

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.permission.PermissionModel
import dev.kokoroidkt.coreApi.user.Users
import dev.kokoroidkt.pluginApi.rule.Rule

/**
 * 内建rule， 要求至少有一个用户拥有某个权限
 * 若用户拥有权限，则返回true
 *
 * @param permissionModel 要求的权限模型
 * @param mode 权限验证模式
 * @param requireExact 是否要求精确权限，如果为true，则OP用户也要求持有所要求的权限
 * @return
 */
fun requirePermission(
    permissionModel: PermissionModel,
    mode: PermissionModel.VerifyMode = PermissionModel.VerifyMode.USER_ONLY,
    requireExact: Boolean = false,
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
            if (!requireExact && users.any { it.isOp }) return true
            for (user in users) {
                val result = permissionModel.verify(user, mode)
                if (result) return true
            }
            return false
        }
    }

/**
 * 内建rule， 要求至少有一个用户没有某个权限
 * 其实就是[requirePermission]的反面
 * 若用户没有权限，则返回true
 *
 * @param permissionModel 要求的权限模型
 * @param mode 权限验证模式
 * @param requireExact 是否要求精确权限，如果为true，则OP用户也要求持有所要求的权限
 * @return
 */
fun withoutPermission(
    permissionModel: PermissionModel,
    mode: PermissionModel.VerifyMode = PermissionModel.VerifyMode.USER_ONLY,
    requireExact: Boolean = false,
): Rule =
    {
        bot,
        event,
        messageChain,
        users,
        ->
        !requirePermission(permissionModel, mode, requireExact).check(bot, event, messageChain, users)
    }

/**
 * 内建权限，要求至少有一个用户必须是OP
 *
 */
fun requireOp(): Rule =
    {
        bot,
        event,
        messageChain,
        users,
        ->
        users?.any { it.isOp } ?: false
    }
