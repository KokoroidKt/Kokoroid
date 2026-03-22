package dev.kokoroidkt.coreApi.database

import dev.kokoroidkt.coreApi.database.tables.PermissionTable
import dev.kokoroidkt.coreApi.database.tables.UserTable
import org.jetbrains.exposed.v1.core.Table

val allTables: Array<out Table> = arrayOf(UserTable, PermissionTable)
