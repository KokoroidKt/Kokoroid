package dev.kokoroidkt.coreApi.database.migrations

import dev.kokoroidkt.coreApi.database.allTables
import dev.kokoroidkt.coreApi.utils.sha256Fingerprint
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

fun computeTableHash(): String =
    transaction {
        val totalDDL =
            buildString {
                allTables.forEach { table ->
                    table.ddl.forEach { currentDDL ->
                        append(currentDDL)
                    }
                }
            }
        sha256Fingerprint(totalDDL)
    }
