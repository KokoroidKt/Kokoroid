// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.coreApi.user

import dev.kokoroidkt.coreApi.database.DatabaseManager
import dev.kokoroidkt.coreApi.database.tables.OperatorTable
import dev.kokoroidkt.coreApi.database.tables.UserGroupTable
import dev.kokoroidkt.coreApi.database.tables.UserPermissionTable
import dev.kokoroidkt.coreApi.permission.GrantedPermission
import dev.kokoroidkt.coreApi.permission.PermissionExtraData
import dev.kokoroidkt.coreApi.permission.PermissionHolder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.upsert
import org.jetbrains.exposed.v1.json.contains
import org.koin.java.KoinJavaComponent.getKoin
import kotlin.uuid.ExperimentalUuidApi

abstract class User(
    val adapterId: String,
) : PermissionHolder {
    /*
     */
    abstract val platfromUserId: String

    val isOp: Boolean by lazy {
        getKoin().get<DatabaseManager>().transaction {
            return@transaction OperatorTable
                .selectAll()
                .where { OperatorTable.userId eq userId }
                .count() > 0
        }
    }

    open val userId get() = "$platfromUserId@$adapterId"

    open val userGroups: List<UserGroup> by lazy {
        getKoin().get<DatabaseManager>().transaction {
            return@transaction UserGroupTable
                .selectAll()
                .where { UserGroupTable.users.contains(userId) }
                .map { row ->
                    @OptIn(ExperimentalUuidApi::class)
                    UserGroup.createFromExist(
                        row[UserGroupTable.name],
                        row[UserGroupTable.uuid],
                        row[UserGroupTable.users],
                    )
                }.toList()
        }
    }

    /**
     * 只要两个用户的[User.userId]是一样的，我们就认为他们是同一个人
     * 实现此类时需要调用超类的equals方法
     *
     * @param other
     * @return
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false
        return userId == other.userId
    }

    override fun hashCode(): Int = userId.hashCode()

    /**
     * 查找用户本人的完整权限
     *
     * @param permissionNode 某个权限节点，例如user.profile.edit，此处不应该添加**任何**通配符
     * @return
     */
    override fun findPermission(
        namespace: String,
        permissionNode: String,
    ): GrantedPermission? {
        val databaseManager = getKoin().get<DatabaseManager>()
        val permissionColumn =
            databaseManager.transaction {
                UserPermissionTable
                    .selectAll()
                    .where {
                        (UserPermissionTable.userId eq userId) and
                            (UserPermissionTable.permissionNode eq permissionNode) and
                            (UserPermissionTable.namespace eq namespace)
                    }.firstOrNull()
            } ?: return null
        return GrantedPermission(
            permissionColumn[UserPermissionTable.namespace],
            permissionColumn[UserPermissionTable.permissionNode],
            PermissionExtraData.fromMap(permissionColumn[UserPermissionTable.extra]),
        )
    }

    override fun addPermission(permission: GrantedPermission) {
        val databaseManager = getKoin().get<DatabaseManager>()
        databaseManager.transaction {
            UserPermissionTable.upsert {
                it[namespace] = permission.namespace
                it[permissionNode] = permission.permissionNode
                it[extra] = permission.extraData.data
                it[userId] = userId
            }
        }
    }
}
