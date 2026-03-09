package dev.kokoroidkt.coreApi.permission

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PermissionTreeTest {
    @Test
    fun `test getAllPermissionString returns empty list for empty tree`() {
        val tree = PermissionTree()
        val result = tree.getAllPermissionData()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `test getAllPermissionString returns correct list after setting permissions`() {
        val tree = PermissionTree()
        tree.setPermission("user.edit.name", PermissionValue.ALLOW)
        tree.setPermission("user.edit.age", PermissionValue.DENY)
        val result = tree.getAllPermissionData()
        assertEquals(2, result.size)
        assertTrue(result.any { it.permissionString == "user.edit.name" && it.permission == PermissionValue.ALLOW })
        assertTrue(result.any { it.permissionString == "user.edit.age" && it.permission == PermissionValue.DENY })
    }

    @Test
    fun `test getPermission returns NOT_SET for non-existent permission`() {
        val tree = PermissionTree()
        val result = tree.getPermission("user.edit.name")
        assertEquals(PermissionValue.NOT_SET, result)
    }

    @Test
    fun `test getPermission returns ALLOW for explicitly allowed permission`() {
        val tree = PermissionTree()
        tree.setPermission("user.edit.name", PermissionValue.ALLOW)
        val result = tree.getPermission("user.edit.name")
        assertEquals(PermissionValue.ALLOW, result)
    }

    @Test
    fun `test getPermission returns DENY for explicitly denied permission`() {
        val tree = PermissionTree()
        tree.setPermission("user.edit.name", PermissionValue.DENY)
        val result = tree.getPermission("user.edit.name")
        assertEquals(PermissionValue.DENY, result)
    }

    @Test
    fun `test getPermission returns DENY when parent is DENY even if child is ALLOW`() {
        val tree = PermissionTree()
        tree.setPermission("user.edit", PermissionValue.DENY)
        tree.setPermission("user.edit.name", PermissionValue.ALLOW)
        val result = tree.getPermission("user.edit.name")
        assertEquals(PermissionValue.DENY, result)
    }

    @Test
    fun `test getPermission returns ALLOW when parent is ALLOW and child is NOT_SET`() {
        val tree = PermissionTree()
        tree.setPermission("user.edit", PermissionValue.ALLOW)
        tree.setPermission("user.edit.name", PermissionValue.NOT_SET)
        val result = tree.getPermission("user.edit.name")
        assertEquals(PermissionValue.NOT_SET, result)
    }

    @Test
    fun `test getPermission with wildcard star matches any node at same depth`() {
        val tree = PermissionTree()
        tree.setPermission("user.edit.*", PermissionValue.ALLOW)
        val result1 = tree.getPermission("user.edit.name")
        assertEquals(PermissionValue.ALLOW, result1)
        val result2 = tree.getPermission("user.edit.age")
        assertEquals(PermissionValue.ALLOW, result2)
    }

    @Test
    fun `test getPermission with wildcard star does not match deeper nodes`() {
        val tree = PermissionTree()
        tree.setPermission("user.edit.*", PermissionValue.ALLOW)
        val result = tree.getPermission("user.edit.name.length")
        assertEquals(PermissionValue.NOT_SET, result)
    }

    @Test
    fun `test getPermission with recursive double star matches all subnodes`() {
        val tree = PermissionTree()
        tree.setPermission("user.edit.**", PermissionValue.ALLOW)
        val result1 = tree.getPermission("user.edit.name")
        assertEquals(PermissionValue.ALLOW, result1)
        val result2 = tree.getPermission("user.edit.name.length")
        assertEquals(PermissionValue.ALLOW, result2)
        val result3 = tree.getPermission("user.edit.age")
        assertEquals(PermissionValue.ALLOW, result3)
    }

    @Test
    fun `test getPermission with recursive double star denies all subnodes when set to DENY`() {
        val tree = PermissionTree()
        tree.setPermission("user.edit.**", PermissionValue.DENY)
        val result1 = tree.getPermission("user.edit.name")
        assertEquals(PermissionValue.DENY, result1)
        val result2 = tree.getPermission("user.edit.name.length")
        assertEquals(PermissionValue.DENY, result2)
    }

    @Test
    fun `test setPermission creates intermediate nodes with NOT_SET by default`() {
        val tree = PermissionTree()
        tree.setPermission("user.edit.name", PermissionValue.ALLOW)
        val result = tree.getPermission("user.edit")
        assertEquals(PermissionValue.NOT_SET, result)
    }

    @Test
    fun `test setPermission with double star stops further processing`() {
        val tree = PermissionTree()
        tree.setPermission("user.edit.**.name", PermissionValue.ALLOW)
        val result = tree.getPermission("user.edit.name")
        assertEquals(PermissionValue.ALLOW, result)
    }

    @Test
    fun `test getPermission returns DENY when wildcard star is DENY`() {
        val tree = PermissionTree()
        tree.setPermission("user.edit.*", PermissionValue.DENY)
        val result = tree.getPermission("user.edit.name")
        assertEquals(PermissionValue.DENY, result)
    }

    @Test
    fun `test getPermission returns NOT_SET when path partially exists but final node missing`() {
        val tree = PermissionTree()
        tree.setPermission("user.edit", PermissionValue.ALLOW)
        val result = tree.getPermission("user.edit.name")
        assertEquals(PermissionValue.NOT_SET, result)
    }

    @Test
    fun `test setPermission with doOverride=true overrides all nodes in path to ALLOW`() {
        val tree = PermissionTree()
        // 先设置一个DENY节点
        tree.setPermission("user.edit", PermissionValue.DENY)
        // 使用doOverride设置子节点为ALLOW，应该重写所有路径节点
        tree.setPermission("user.edit.newItem", PermissionValue.ALLOW, doOverride = true)

        // 验证所有节点都被重写为ALLOW
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user"))
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user.edit"))
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user.edit.newItem"))
    }

    @Test
    fun `test setPermission with doOverride=true overrides all nodes in path to DENY`() {
        val tree = PermissionTree()
        // 先设置一个ALLOW节点
        tree.setPermission("user.edit", PermissionValue.ALLOW)
        // 使用doOverride设置子节点为DENY，应该重写所有路径节点
        tree.setPermission("user.edit.newItem", PermissionValue.DENY, doOverride = true)

        // 验证所有节点都被重写为DENY
        assertEquals(PermissionValue.DENY, tree.getPermission("user"))
        assertEquals(PermissionValue.DENY, tree.getPermission("user.edit"))
        assertEquals(PermissionValue.DENY, tree.getPermission("user.edit.newItem"))
    }

    @Test
    fun `test setPermission with doOverride=true creates intermediate nodes with same value`() {
        val tree = PermissionTree()
        // 设置一个不存在的路径，使用doOverride
        tree.setPermission("user.edit.newItem", PermissionValue.ALLOW, doOverride = true)

        // 验证所有节点都被设置为ALLOW
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user"))
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user.edit"))
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user.edit.newItem"))
    }

    @Test
    fun `test setPermission with doOverride=false does not override existing nodes`() {
        val tree = PermissionTree()
        // 先设置一个DENY节点
        tree.setPermission("user.edit", PermissionValue.DENY)
        // 使用默认doOverride=false设置子节点为ALLOW
        tree.setPermission("user.edit.newItem", PermissionValue.ALLOW)

        // 验证父节点保持DENY
        assertEquals(PermissionValue.DENY, tree.getPermission("user.edit"))
        // 最终权限应该是DENY（拒绝优先策略）
        assertEquals(PermissionValue.DENY, tree.getPermission("user.edit.newItem"))
    }

    @Test
    fun `test setPermission with doOverride=true overrides existing intermediate nodes`() {
        val tree = PermissionTree()
        // 设置一个复杂的权限结构
        tree.setPermission("user.edit", PermissionValue.DENY)
        tree.setPermission("user.edit.name", PermissionValue.ALLOW)
        tree.setPermission("user.edit.age", PermissionValue.ALLOW)

        // 使用doOverride重写整个路径
        tree.setPermission("user.edit.newItem", PermissionValue.ALLOW, doOverride = true)

        // 验证所有节点都被重写为ALLOW
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user"))
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user.edit"))
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user.edit.name"))
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user.edit.age"))
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user.edit.newItem"))
    }

    @Test
    fun `test setPermission with doOverride=true and wildcard star`() {
        val tree = PermissionTree()
        // 使用doOverride设置带通配符的权限
        tree.setPermission("user.edit.*", PermissionValue.ALLOW, doOverride = true)

        // 验证路径节点被正确设置
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user"))
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user.edit"))
        // 通配符节点应该被设置为ALLOW
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user.edit.name"))
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user.edit.age"))
    }

    @Test
    fun `test setPermission with doOverride=true and recursive double star`() {
        val tree = PermissionTree()
        // 使用doOverride设置递归通配符
        tree.setPermission("user.edit.**", PermissionValue.DENY, doOverride = true)

        // 验证路径节点被正确设置
        assertEquals(PermissionValue.DENY, tree.getPermission("user"))
        assertEquals(PermissionValue.DENY, tree.getPermission("user.edit"))
        // 递归通配符应该影响所有子节点
        assertEquals(PermissionValue.DENY, tree.getPermission("user.edit.name"))
        assertEquals(PermissionValue.DENY, tree.getPermission("user.edit.name.length"))
        assertEquals(PermissionValue.DENY, tree.getPermission("user.edit.age"))
    }

    @Test
    fun `test setPermission with doOverride=true on existing tree with mixed permissions`() {
        val tree = PermissionTree()
        // 创建混合权限的树
        tree.setPermission("user", PermissionValue.ALLOW)
        tree.setPermission("user.edit", PermissionValue.DENY)
        tree.setPermission("user.view", PermissionValue.ALLOW)
        tree.setPermission("user.view.name", PermissionValue.ALLOW)

        // 使用doOverride重写edit分支
        tree.setPermission("user.edit.newItem", PermissionValue.ALLOW, doOverride = true)

        // 验证edit分支被重写
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user.edit"))
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user.edit.newItem"))

        // 验证其他分支不受影响
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user.view"))
        assertEquals(PermissionValue.ALLOW, tree.getPermission("user.view.name"))
    }
}
