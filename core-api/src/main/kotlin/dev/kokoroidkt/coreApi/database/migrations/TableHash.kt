// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

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
        sha256Fingerprint(totalDDL).substring(0, 16)
    }
