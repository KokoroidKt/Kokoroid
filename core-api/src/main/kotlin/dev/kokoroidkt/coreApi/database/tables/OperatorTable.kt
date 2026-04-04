package dev.kokoroidkt.coreApi.database.tables

import org.jetbrains.exposed.v1.core.Table

object OperatorTable : Table("kokoroid_operator") {
    val userId = varchar("user_id", 128)
}
