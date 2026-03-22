package dev.kokoroidkt.coreApi.database.tables

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.json.json

object UserTable : Table("kokoroid_user") {
    val userId = varchar("userId", 128)
}
