// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.database.tables

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.json.json
import kotlin.uuid.ExperimentalUuidApi

object UserGroupPermissionTable : Table("kokoroid_permission") {
    @OptIn(ExperimentalUuidApi::class)
    val groupUuid = uuid("group_uuid")
    val namespace = varchar("namespace", 64)
    val permissionNode = varchar("permission_node", 256)

    val extra = json<Map<String, JsonElement>>("extra", Json)
}
