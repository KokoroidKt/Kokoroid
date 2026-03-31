// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.permission

interface PermissionHolder {
    /**
     * 从一个权限节点路径寻找匹配的权限
     *
     * @param permissionNode 权限节点
     * @return 找到的匹配的权限
     */
    fun findPermission(
        namespace: String,
        permissionNode: String,
    ): GrantedPermission?

    fun addPermission(permission: GrantedPermission)

    /**
     * 以字符串形式为当前对象添加权限
     * 权限字符串格式：
     *   <namespace>|<permissionNode>|<extraDataJson>
     * 其中extraDataJson为JSON格式的字符串，用于扩展权限信息
     *
     * @param permission
     */
    fun addPermission(permission: String) {
        val items = permission.split(":")
        val namespace = items[0]
        val permissionNode = items[1]
        val extraDataJson = PermissionExtraData.fromJsonString(items[2])
        return addPermission(GrantedPermission(namespace, permissionNode, extraDataJson))
    }
}
