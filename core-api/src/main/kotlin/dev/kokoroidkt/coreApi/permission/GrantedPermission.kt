package dev.kokoroidkt.coreApi.permission

import dev.kokoroidkt.coreApi.database.tables.PermissionTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

data class GrantedPermission(
    val namespace: String,
    val permissionNode: String,
    val extraData: PermissionExtraData,
) {
    fun toPermissionString(): String = "$namespace:$permissionNode:${extraData.toJsonString()}"

    companion object {
        @JvmStatic
        fun fromPermissionString(permissionString: String): GrantedPermission {
            val (namespace, permissionNode, extraDataJson) = permissionString.split(":")
            val extraData = PermissionExtraData.fromJsonString(extraDataJson)
            return GrantedPermission(namespace, permissionNode, extraData)
        }
    }

    fun saveToDbSync() {
        transaction {
            PermissionTable.insert {
                it[namespace] = this@GrantedPermission.namespace
                it[permissionNode] = this@GrantedPermission.permissionNode
                it[extra] = this@GrantedPermission.extraData.data
            }
        }
    }

    suspend fun saveToDb() {
        withContext(Dispatchers.IO) {
            saveToDbSync()
        }
    }
}
