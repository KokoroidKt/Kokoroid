package dev.kokoroidkt.coreApi.permission

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PermissionTreeTest {
    @Test
    fun `test getAllPermissionString returns empty list for empty tree`() {
        val tree = PermissionTree()
        val result = tree.getAllPermissionString()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `test getAllPermissionString returns correct list after setting permissions`() {
        val tree = PermissionTree()
        tree.setPermission("user.edit.name", PermissionValue.ALLOW)
        tree.setPermission("user.edit.age", PermissionValue.DENY)
        val result = tree.getAllPermissionString()
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
}
