package dev.kokoroidkt.coreApi.permission

/**
 * 权限树
 * 权限树采用**拒绝优先**策略，从根节点到最远叶子节点，只要有DENY，整个权限则为DENY
 * NOT_SET会被忽略, 继续往下寻找
 * 如果最后一个节点也是NOT_SET，则整个权限为NOT_SET
 *
 * 例子：对于Root.user.edit.name
 *  - Root(ALLOW).user(ALLOW).edit(ALLOW).name(ALLOW) -> ALLOW
 *  - Root(Allow).user(Allow).edit(DENY).name(Allow) -> DENY
 *  - Root(ALLOW).user(ALLOW).edit(ALLOW).name(DENY) -> DENY
 *  - Root(ALLOW).user(ALLOW).edit(NOT_SET).name(ALLOW) -> ALLOW
 *  - Root(ALLOW).user(ALLOW).edit(ALLOW).name(NOT_SET) -> NOT_SET
 *
 *  对于Root.user.edit
 *
 *  - Root(ALLOW).user(ALLOW).edit(ALLOW).name(ALLOW) -> ALLOW
 *  - Root(Allow).user(Allow).edit(DENY).name(Allow) -> DENY
 *  - Root(ALLOW).user(ALLOW).edit(ALLOW).name(DENY) -> ALLOW
 *  - Root(ALLOW).user(ALLOW).edit(NOT_SET).name(ALLOW) -> NOT_SET
 *  - Root(ALLOW).user(ALLOW).edit(ALLOW).name(NOT_SET) -> ALLOW
 *
 *  单星号 `*` 匹配同深度下的所有节点，例子:
 *  - Root(ALLOW).user(ALLOW).edit(ALLOW).*(ALLOW) -> 对Root.user.edit.name ALLOW, 对Root.user.edit.name.len 不一定（看len的权限）
 *  - Root(ALLOW).user(ALLOW).edit(ALLOW).*(DENY) -> 对Root.user.edit.name DENY, 对Root.user.edit.name.len 不一定（看len的权限）
 *
 *  双星号 `**` 递归匹配所有子节点，例子：
 *  - Root(ALLOW).user(ALLOW).edit(ALLOW).**(ALLOW) -> 对Root.user.edit.name ALLOW, 对Root.user.edit.name.len 也Allow
 *  - Root(ALLOW).user(ALLOW).edit(ALLOW).**(DENY) -> 对Root.user.edit.name DENY, 对Root.user.edit.name.len 也DENY
 *
 *  默认设置节点设置策略：
 *  如果中间节点不存在，则一路设置为NOT_SET
 *  修改只影响最远节点，最近节点全部不影响
 *  如果要允许用户使用子节点，
 *  例子：
 *   - Root.user<存在, DENY>.edit<不存在>.newItem<不存在>设置为ALLOW -> user(DENY).edit(NOT_SET).newItem(ALLOW)
 *   - Root.user<存在, DENY>.edit<存在, ALLOW>.newItem<不存在>设置为ALLOW -> user(DENY).edit(ALLOW).newItem(ALLOW)
 *   - Root.user<存在, ALLOW>.edit<存在, ALLOW>.newItem<不存在>设置为ALLOW -> user(ALLOW).edit(ALLOW).newItem(ALLOW)
 *
 */
class PermissionTree(
    val root: PermissionNode =
        PermissionNode(
            "",
        ),
) {
    fun getAllPermissionString(): List<PermissionData> =
        root.children.values.flatMap { child ->
            child
                .getAllPermission()
                .map { id -> PermissionData(id, getPermission(id)) }
        }

    /**
     * 检查权限
     */
    fun getPermission(permissionString: String): PermissionValue {
        var currentRoot = root
        val keys = permissionString.split(".")
        for (index in 0..<keys.size) {
            val key = keys[index]
            val child = currentRoot.children[key] ?: return PermissionValue.NOT_SET
            if (child.isWideMatch && keys.size - index == 2) {
                return child.permission
            } else if (child.isRecursiveMatch) {
                return child.permission
            }
            when (child.permission) {
                PermissionValue.DENY -> {
                    return PermissionValue.DENY
                }

                else -> {
                    currentRoot = child
                }
            }
        }

        return currentRoot.permission
    }

    /**
     * 设置一个节点
     * 设置策略：
     * 如果中间节点不存在，则一路设置为NOT_SET
     * 修改只影响最远节点，最近节点全部不影响
     * 例子：
     *  - user<存在, DENY>.edit<不存在>.newItem<不存在>设置为ALLOW -> user(DENY).edit(NOT_SET).newItem(ALLOW) -> DENY
     *  - user<存在, DENY>.edit<存在, ALLOW>.newItem<不存在>设置为ALLOW -> user(DENY).edit(ALLOW).newItem(ALLOW) -> DENY
     *
     *  如果doOverride = true, 则会重写路径上所有节点的权限
     *  例子
     *  - user<存在, DENY>.edit<不存在>.newItem<不存在>设置为ALLOW -> user(ALLOW).edit(ALLOW).newItem(ALLOW) -> ALLOW
     *  - user<存在, DENY>.edit<存在, ALLOW>.newItem<不存在>设置为DENY -> user(DENY).edit(DENY).newItem(DENY) -> DENY
     *
     * @param permissionString
     * @param value
     * @param doOverride 是否重写节点的权限
     */
    fun setPermission(
        permissionString: String,
        value: PermissionValue,
        doOverride: Boolean = false,
    ) {
        var currentRoot = root
        val keys = permissionString.split(".")
        for (index in 0..<keys.size) {
            val key = keys[index]
            if (key == "**" || key == "*") return
            val newNode = {
                PermissionNode(
                    key,
                    if (index == keys.size - 1 || keys.getOrNull(index + 1) == "**" ||
                        keys.getOrNull(index + 1) == "*"
                    ) {
                        value
                    } else {
                        PermissionValue.NOT_SET
                    },
                    isWideMatch = keys.getOrNull(index + 1) == "*",
                    isRecursiveMatch = keys.getOrNull(index + 1) == "**",
                )
            }
            currentRoot =
                if (keys.size - index > 1) {
                    val old = currentRoot.children.getOrPut(key, newNode)
                    if (doOverride) {
                        old.permission = value
                        currentRoot.children[key] = old
                    }
                    old
                } else {
                    currentRoot.children[key] = newNode()
                    currentRoot.children[key]!!
                }
            // if (key == "**") return
        }
    }
}
