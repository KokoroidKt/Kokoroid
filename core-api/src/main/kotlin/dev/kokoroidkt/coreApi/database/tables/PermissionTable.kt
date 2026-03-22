package dev.kokoroidkt.coreApi.database.tables

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.json.json

object PermissionTable : Table("kokoroid_permission") {
    val userId = varchar("userId", 128)
    val namespace = varchar("namespace", 64)
    val permissionNode = varchar("permissionNode", 256)
    val extra = json<Map<String, JsonElement>>("extra", Json)
}
