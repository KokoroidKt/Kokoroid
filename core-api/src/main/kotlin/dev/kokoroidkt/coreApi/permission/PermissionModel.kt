// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.permission

/**
 * 用于声明权限模型
 *
 * @constructor Create empty Permission model
 */
class PermissionModel {
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

    override fun toString(): String = "Permission=${toPermissionString()} (Checker=${checker.javaClass.name})}"

    fun toPermissionString(): String = "$namespace:$node:${data.toJsonString()}"

    fun verify(granted: GrantedPermission): Boolean {
        if (granted.namespace != namespace) return false
        val nodeListCopy = nodeList.toMutableList()
        TODO("我困死了，下次月假写")
    }
}
