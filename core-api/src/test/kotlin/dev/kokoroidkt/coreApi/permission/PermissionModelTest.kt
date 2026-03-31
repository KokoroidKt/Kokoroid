package dev.kokoroidkt.coreApi.permission

import dev.kokoroidkt.coreApi.user.User
import dev.kokoroidkt.coreApi.user.UserGroup
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class PermissionModelTest {
    private class TestPermissionHolder : PermissionHolder {
        private val permissions = mutableMapOf<String, GrantedPermission>()

        override fun findPermission(
            namespace: String,
            permissionNode: String,
        ): GrantedPermission? = permissions["$namespace:$permissionNode"]

        override fun addPermission(permission: GrantedPermission) {
            permissions["${permission.namespace}:${permission.permissionNode}"] = permission
        }
    }

    private class MockUser(
        override val platfromUserId: String,
        private val holder: TestPermissionHolder,
        private var _groups: List<UserGroup> = emptyList(),
    ) : User("test_adapter") {
        override val userId: String = "$platfromUserId@$adapterId"
        override val userGroups: List<UserGroup> get() = _groups

        fun setGroups(groups: List<UserGroup>) {
            _groups = groups
        }

        override fun findPermission(
            namespace: String,
            permissionNode: String,
        ): GrantedPermission? = holder.findPermission(namespace, permissionNode)

        override fun addPermission(permission: GrantedPermission) = holder.addPermission(permission)
    }

    private class MockGroup(
        name: String,
        private val holder: TestPermissionHolder,
    ) : UserGroup(emptyList(), name, Uuid.random()) {
        override fun findPermission(
            namespace: String,
            permissionNode: String,
        ): GrantedPermission? = holder.findPermission(namespace, permissionNode)
    }

    @Test
    fun `test PermissionModel creation`() {
        val extraData = PermissionExtraData.empty()
        val namespace = "test_namespace"
        val node = "test.node"
        val checker: PermissionChecker = { _ -> true }

        val model = PermissionModel.create(extraData, namespace, node, checker)

        assertEquals(extraData, model.data)
        assertEquals(namespace, model.namespace)
        assertEquals(node, model.node)
        assertEquals(checker, model.checker)
        assertEquals(listOf("test", "node"), model.nodeList)
    }

    @Test
    fun `test verify boundary cases`() {
        val namespace = "kokoroid"
        val model = PermissionModel.create(PermissionExtraData.empty(), namespace, "user.profile.edit")

        val holder = TestPermissionHolder()
        val user = MockUser("test_user", holder)

        // 1. 完全匹配
        holder.addPermission(GrantedPermission(namespace, "user.profile.edit", PermissionExtraData.empty()))
        assertTrue(model.verify(user), "Should verify true for exact match")

        // 2. 父节点匹配 (user.profile 为 true)
        val holder2 = TestPermissionHolder()
        val user2 = MockUser("test_user_2", holder2)
        holder2.addPermission(GrantedPermission(namespace, "user.profile", PermissionExtraData.empty()))
        assertTrue(model.verify(user2), "Should verify true for parent node match")

        // 3. 祖先节点匹配 (user 为 true)
        val holder3 = TestPermissionHolder()
        val user3 = MockUser("test_user_3", holder3)
        holder3.addPermission(GrantedPermission(namespace, "user", PermissionExtraData.empty()))
        assertTrue(model.verify(user3), "Should verify true for ancestor node match")

        // 4. 不匹配 (子节点或无关节点)
        val holder4 = TestPermissionHolder()
        val user4 = MockUser("test_user_4", holder4)
        holder4.addPermission(GrantedPermission(namespace, "user.profile.edit.something", PermissionExtraData.empty()))
        assertFalse(model.verify(user4), "Should verify false if only child node matches")

        // 5. Namespace 不匹配
        val holder5 = TestPermissionHolder()
        val user5 = MockUser("test_user_5", holder5)
        holder5.addPermission(GrantedPermission("other", "user.profile.edit", PermissionExtraData.empty()))
        assertFalse(model.verify(user5), "Should verify false if namespace does not match")
    }

    @Test
    fun `test verify modes`() {
        val namespace = "kokoroid"
        val node = "user.profile"
        val model = PermissionModel.create(PermissionExtraData.empty(), namespace, node)

        val userHolder = TestPermissionHolder()
        val groupHolder = TestPermissionHolder()
        val group = MockGroup("test_group", groupHolder)
        val user = MockUser("test_user", userHolder, listOf(group))

        val userPerm = GrantedPermission(namespace, node, PermissionExtraData.empty())
        val groupPerm = GrantedPermission(namespace, node, PermissionExtraData.empty())

        // USER_ONLY: User has perm, Group doesn't
        userHolder.addPermission(userPerm)
        assertTrue(model.verify(user, PermissionModel.VerifyMode.USER_ONLY), "USER_ONLY should be true when user has perm")

        // USER_ONLY: User doesn't have perm, Group has perm
        val userHolder2 = TestPermissionHolder()
        val user2 = MockUser("test_user_2", userHolder2, listOf(group))
        groupHolder.addPermission(groupPerm)
        assertFalse(model.verify(user2, PermissionModel.VerifyMode.USER_ONLY), "USER_ONLY should be false when only group has perm")

        // GROUP_ONLY: User has perm, Group doesn't
        val groupHolder3 = TestPermissionHolder()
        val group3 = MockGroup("test_group_3", groupHolder3)
        val userHolder3 = TestPermissionHolder()
        userHolder3.addPermission(userPerm)
        val user3 = MockUser("test_user_3", userHolder3, listOf(group3))
        assertFalse(model.verify(user3, PermissionModel.VerifyMode.GROUP_ONLY), "GROUP_ONLY should be false when only user has perm")

        // GROUP_ONLY: User doesn't have perm, Group has perm
        groupHolder3.addPermission(groupPerm)
        assertTrue(model.verify(user3, PermissionModel.VerifyMode.GROUP_ONLY), "GROUP_ONLY should be true when group has perm")

        // ALL: Either has perm
        assertTrue(model.verify(user, PermissionModel.VerifyMode.ALL), "ALL should be true when user has perm")
        assertTrue(model.verify(user2, PermissionModel.VerifyMode.ALL), "ALL should be true when group has perm")

        val userHolder4 = TestPermissionHolder()
        val groupHolder4 = TestPermissionHolder()
        val group4 = MockGroup("test_group_4", groupHolder4)
        val user4 = MockUser("test_user_4", userHolder4, listOf(group4))
        assertFalse(model.verify(user4, PermissionModel.VerifyMode.ALL), "ALL should be false when neither has perm")
    }

    @Test
    fun `test custom checker and extra data`() {
        val namespace = "kokoroid"
        val node = "user.profile"
        val checker: PermissionChecker = { extra ->
            val result = extra.getBoolean("is_admin")
            result is PermissionExtraDataResult.Success && result.value == true
        }
        val model = PermissionModel.create(PermissionExtraData.empty(), namespace, node, checker)

        val holder = TestPermissionHolder()
        val user = MockUser("test_user", holder)

        // Extra data matching checker
        val extraDataSuccess = PermissionExtraData.empty()
        extraDataSuccess.set("is_admin", true)
        holder.addPermission(GrantedPermission(namespace, node, extraDataSuccess))
        assertTrue(model.verify(user), "Should verify true when checker passes")

        // Extra data not matching checker
        val extraDataFail = PermissionExtraData.empty()
        extraDataFail.set("is_admin", false)
        val holder2 = TestPermissionHolder()
        val user2 = MockUser("test_user_2", holder2)
        holder2.addPermission(GrantedPermission(namespace, node, extraDataFail))
        assertFalse(model.verify(user2), "Should verify false when checker fails")
    }

    @Test
    fun `test Permission string conversion`() {
        val extraData = PermissionExtraData.empty()
        extraData.set("key", "value")
        val granted = GrantedPermission("ns", "node", extraData)

        val permStr = granted.toPermissionString()
        val fromStr = GrantedPermission.fromPermissionString(permStr)

        assertEquals(granted.namespace, fromStr.namespace)
        assertEquals(granted.permissionNode, fromStr.permissionNode)
        assertEquals(granted.extraData.toJsonString(), fromStr.extraData.toJsonString())

        val result = fromStr.extraData.getString("key")
        assertTrue(result is PermissionExtraDataResult.Success)
        assertEquals("value", result.value)
    }
}
