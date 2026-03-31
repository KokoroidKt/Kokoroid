package dev.kokoroidkt.coreApi.database.tables

import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.json.json
import kotlin.uuid.ExperimentalUuidApi

object UserGroupTable : Table("kokoroid_user_group") {
    @OptIn(ExperimentalUuidApi::class)
    val uuid = uuid("group_uuid")

    @OptIn(ExperimentalUuidApi::class)
    override val primaryKey = PrimaryKey(this.uuid)
    val name = varchar("group_name", 255).uniqueIndex()
    val users = json<List<String>>("users", Json)
}
