// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.permission

import dev.kokoroidkt.coreApi.database.DatabaseManager
import dev.kokoroidkt.coreApi.database.tables.UserPermissionTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.jdbc.insert
import org.koin.java.KoinJavaComponent.getKoin

data class GrantedPermission(
    val namespace: String,
    val permissionNode: String,
    val extraData: PermissionExtraData,
) {
    val databaseManager: DatabaseManager by lazy { getKoin().get<DatabaseManager>() }

    fun toPermissionString(): String = "$namespace|$permissionNode|${extraData.toJsonString()}"

    companion object {
        @JvmStatic
        fun fromPermissionString(permissionString: String): GrantedPermission {
            val (namespace, permissionNode, extraDataJson) = permissionString.split("|")
            val extraData = PermissionExtraData.fromJsonString(extraDataJson)
            return GrantedPermission(namespace, permissionNode, extraData)
        }
    }

    fun saveToDbSync() {
        databaseManager.transaction {
            UserPermissionTable.insert {
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
