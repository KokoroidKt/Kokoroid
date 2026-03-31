package dev.kokoroidkt.coreApi.user

import dev.kokoroidkt.coreApi.database.DatabaseManager
import dev.kokoroidkt.coreApi.database.tables.UserGroupPermissionTable
import dev.kokoroidkt.coreApi.database.tables.UserGroupTable
import dev.kokoroidkt.coreApi.database.tables.UserPermissionTable
import dev.kokoroidkt.coreApi.database.tables.UserPermissionTable.extra
import dev.kokoroidkt.coreApi.database.tables.UserPermissionTable.namespace
import dev.kokoroidkt.coreApi.database.tables.UserPermissionTable.permissionNode
import dev.kokoroidkt.coreApi.database.tables.UserPermissionTable.userId
import dev.kokoroidkt.coreApi.permission.GrantedPermission
import dev.kokoroidkt.coreApi.permission.PermissionExtraData
import dev.kokoroidkt.coreApi.permission.PermissionHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.upsert
import org.koin.java.KoinJavaComponent.getKoin
import java.util.UUID
import java.util.function.IntFunction
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

@OptIn(ExperimentalUuidApi::class)
open class UserGroup protected constructor(
    /**
     * 用户列表
     */
    private val userIds: List<String>,
    /**
     * 用户组名称
     */
    val name: String,
    /**
     * Kotlin UUID，使用Uuid V4
     * 使用Kotlin原生uuid api，但是他目前是实验性的
     * 如果担心他的API变动，请使用[javaUuid]
     */
    val uuid: Uuid,
) : PermissionHolder {
    override fun findPermission(
        namespace: String,
        permissionNode: String,
    ): GrantedPermission? {
        val databaseManager = getKoin().get<DatabaseManager>()
        val permissionColumn =
            databaseManager.transaction {
                UserGroupPermissionTable
                    .selectAll()
                    .where {
                        (UserGroupPermissionTable.groupUuid eq uuid) and
                            (UserGroupPermissionTable.permissionNode eq permissionNode) and
                            (UserGroupPermissionTable.namespace eq namespace)
                    }.firstOrNull()
            } ?: return null
        return GrantedPermission(
            permissionColumn[UserGroupPermissionTable.namespace],
            permissionColumn[UserGroupPermissionTable.permissionNode],
            PermissionExtraData.fromMap(permissionColumn[UserGroupPermissionTable.extra]),
        )
    }

    val users by lazy {
        userIds
            .mapNotNull {
                getKoin().get<UserFactoryManager>().createUser(it)
            }.toList()
    }

    /**
     * Java uuid
     * 如果你不想使用处于实验状态的Kotlin UUID，请使用此Java版本
     * 由Kotlin Uuid类转换而来，使用UUID V4
     */
    @OptIn(ExperimentalUuidApi::class)
    val javaUuid: UUID = uuid.toJavaUuid()

    companion object {
        /**
         * 同步的创建一个用户组
         * 如果用户组已经存在，返回null
         *
         * @param newName 用户组名
         * @param newUsers 用户组用户列表
         * @return
         */
        @OptIn(ExperimentalUuidApi::class)
        fun createSync(
            newName: String,
            newUsers: Users,
        ): UserGroup? {
            val databaseManager = getKoin().get<DatabaseManager>()
            val new = UserGroup(newUsers.map { it.userId }.toList(), newName, Uuid.random())
            val exist =
                databaseManager.transaction {
                    UserGroupTable.selectAll().where { UserGroupTable.name eq newName }.firstOrNull()
                }
            if (exist != null) {
                return null
            }
            databaseManager.transaction {
                UserGroupTable.insert { table ->
                    table[name] = new.name
                    table[uuid] = new.uuid
                    table[users] = new.users.map { it.userId }.toList()
                }
            }
            return new
        }

        /**
         * 创建一个用户组
         * 如果用户组已经存在，返回null
         *
         * @param name 用户组名
         * @param users 用户组用户列表
         * @return
         */
        suspend fun create(
            name: String,
            users: Users,
        ): UserGroup? {
            return withContext(Dispatchers.IO) {
                return@withContext createSync(name, users)
            }
        }

        @OptIn(ExperimentalUuidApi::class)
        @JvmName("createFromExistWithUsers")
        fun createFromExist(
            name: String,
            uuid: Uuid,
            users: Users,
        ): UserGroup =
            UserGroup(
                users.map { it.userId }.toList(),
                name,
                uuid,
            )

        @OptIn(ExperimentalUuidApi::class)
        @JvmName("createFromExistWithIds")
        fun createFromExist(
            name: String,
            uuid: Uuid,
            users: List<String>,
        ): UserGroup =
            UserGroup(
                users,
                name,
                uuid,
            )
    }

    override fun addPermission(permission: GrantedPermission) {
        val databaseManager = getKoin().get<DatabaseManager>()
        databaseManager.transaction {
            UserGroupPermissionTable.upsert {
                it[namespace] = permission.namespace
                it[permissionNode] = permission.permissionNode
                it[extra] = permission.extraData.data
                it[groupUuid] = uuid
            }
        }
    }
}
