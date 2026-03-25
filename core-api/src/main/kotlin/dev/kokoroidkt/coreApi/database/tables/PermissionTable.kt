// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.database.tables

import dev.kokoroidkt.coreApi.permission.PermissionType
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.json.json

object PermissionTable : Table("kokoroid_permission") {
    val userId = varchar("userId", 128)
    val namespace = varchar("namespace", 64)
    val permissionNode = varchar("permission_node", 256)
    val patternType = enumeration<PermissionType>("permission_type")
    val extra = json<Map<String, JsonElement>>("extra", Json)
}
