// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.permission

import dev.kokoroidkt.coreApi.user.User

/**
 * 用于声明权限模型
 *
 * @constructor Create an empty Permission model
 */
open class PermissionModel {
    val data: PermissionExtraData
    val namespace: String
    val node: String

    val checker: PermissionChecker

    val nodeList by lazy { node.split(".") }

    private constructor(
        data: PermissionExtraData,
        namescape: String,
        node: String,
        checker: PermissionChecker,
    ) {
        this.data = data
        this.namespace = namescape
        this.node = node
        this.checker = checker
    }

    companion object {
        fun create(
            data: PermissionExtraData,
            namescape: String,
            node: String,
            checker: PermissionChecker = { _ -> true },
        ): PermissionModel = PermissionModel(data, namescape, node, checker)
    }

    open fun check(extra: PermissionExtraData): Boolean = checker.invoke(extra)

    override fun toString(): String = "Permission=${toPermissionString()} (Checker=${checker.javaClass.name})}"

    fun toPermissionString(): String = "$namespace|$node|${data.toJsonString()}"

    fun toGrantedPermission(): GrantedPermission = GrantedPermission(namespace, node, data)

    enum class VerifyMode {
        USER_ONLY,
        GROUP_ONLY,
        ALL,
    }

    /**
     * 验证权限
     * Kokoroid的权限使用权限树+继承的方式
     * 例如：如果user.profile为true，若namespace一致则：
     *  - user.profile为true
     *  - user.profile.edit为true
     *  - user.profile.read为true
     *  - user为false
     *
     *  如果模式为[VerifyMode.USER_ONLY]，则只验证用户自己持有的权限（默认为此模式）
     *  如果模式为[VerifyMode.GROUP_ONLY]，则只验证用户所在的用户组有没有这个权限
     *  如果模式为[VerifyMode.ALL]，则先验证用户自己是否持有，然后验证用户所在的用户组有没有这个权限，一旦有一个匹配上则返回true
     *
     *  对于每一条权限，需要保证以下三点通过才算验证成功：
     *      1. namespace和node匹配
     *      2. 记录为true
     *      3. 对extraData的检查通过（通过传入的checker lambda，或者复写[check]方法）
     *
     *  关于checker：
     *      checker默认永远返回true，除非传入自定义checker
     *
     * @param user
     * @param mode
     * @return
     */
    fun verify(
        user: User,
        mode: VerifyMode = VerifyMode.USER_ONLY,
    ): Boolean {
        fun findGrantedPermission(holder: PermissionHolder): Boolean {
            for (i in nodeList.size downTo 1) {
                val currentNode = nodeList.subList(0, i).joinToString(".")
                val findedGrantedPermission = holder.findPermission(namespace, currentNode) ?: continue
                val result = check(findedGrantedPermission.extraData)
                if (result) return true // 匹配到就是对
            }
            return false // 一直没匹配到就是错
        }

        if (mode == VerifyMode.USER_ONLY || mode == VerifyMode.ALL) {
            if (findGrantedPermission(user)) return true
        }
        if (mode == VerifyMode.GROUP_ONLY || mode == VerifyMode.ALL) {
            for (group in user.userGroups) {
                if (findGrantedPermission(group)) return true
            }
        }
        return false
    }
}
