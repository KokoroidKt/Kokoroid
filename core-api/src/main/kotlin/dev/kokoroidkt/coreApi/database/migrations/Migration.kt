// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.database.migrations

import dev.kokoroidkt.coreApi.database.allTables
import dev.kokoroidkt.coreApi.database.tables.MigrationTable
import org.jetbrains.exposed.v1.core.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.statements.insertValue
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.exists
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import kotlin.io.path.Path

const val MIGRATION_VERSION_KEY = "migration_version"

sealed class MigrationResult {
    object CreateNew : MigrationResult()

    object NoChange : MigrationResult()

    class NotMatch(
        val oldHash: String,
        val newHash: String,
        val migrationScriptFilename: String,
    ) : MigrationResult()
}

@OptIn(ExperimentalDatabaseMigrationApi::class)
fun trySyncDB(): MigrationResult {
    val oldVersion =
        transaction {
            if (!MigrationTable.exists()) {
                null
            } else {
                MigrationTable
                    .selectAll()
                    .where { MigrationTable.key eq MIGRATION_VERSION_KEY }
                    .singleOrNull()
            }
        }
    if (oldVersion == null) {
        transaction {
            allTables.forEach {
                SchemaUtils.create(it)
            }
            SchemaUtils.create(MigrationTable)
            MigrationTable.insert {
                it[key] = MIGRATION_VERSION_KEY
                it[value] = computeTableHash()
            }
        }
        return MigrationResult.CreateNew
    } else {
        val latestHash = computeTableHash()
        val oldHash = oldVersion[MigrationTable.value]
        if (oldHash == latestHash) {
            return MigrationResult.NoChange
        }
        val scriptName = "migration_${oldHash}_to_$latestHash"
        val path = "kokoroid/migration"
        Path(path).toFile().mkdirs()
        transaction {
            MigrationUtils.generateMigrationScript(
                *allTables,
                scriptName = scriptName,
                scriptDirectory = path,
            )
        }
        return MigrationResult.NotMatch(oldHash, latestHash, "./$path/$scriptName")
    }
}
