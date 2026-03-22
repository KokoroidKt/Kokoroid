// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.database.migrations

import dev.kokoroidkt.coreApi.database.allTables
import dev.kokoroidkt.coreApi.database.migrations.MigrationResult
import dev.kokoroidkt.coreApi.database.migrations.trySyncDB
import dev.kokoroidkt.coreApi.database.tables.MigrationTable
import dev.kokoroidkt.coreApi.database.tables.PermissionTable
import dev.kokoroidkt.coreApi.database.tables.UserTable
import dev.kokoroidkt.coreApi.logging.KokoroidLogger
import org.jetbrains.exposed.v1.core.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.v1.core.StdOutSqlLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.exists
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tests for the trySyncDB function in the MigrationKt class.
 * These tests ensure that database migrations are handled properly.
 */
class MigrationKtTest {
    companion object {
        @BeforeAll
        @JvmStatic
        fun `init database connection with h2`() {
            Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        }
    }

    @Test
    fun `test when no previous version exists then database is initialized`() {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.drop(*allTables)
            // Clear the MigrationTable to mimic no previous version entry
            SchemaUtils.create(MigrationTable)
            MigrationTable.deleteAll()
        }

        val result = trySyncDB()

        transaction {
            addLogger(StdOutSqlLogger)
            // Verify that all tables have been created
            allTables.forEach { assertTrue(it.exists()) }
        }

        assertEquals(MigrationResult.CreateNew, result)
    }

    @Test
    fun `test when previous version matches current version then no changes`() {
        transaction {
            SchemaUtils.drop(*allTables)
            SchemaUtils.drop(MigrationTable)
        }
        val result = trySyncDB()
        val result2 = trySyncDB()

        assertEquals(MigrationResult.NoChange, result2)
    }

    @Test
    fun `test when previous version does not match current version then migration script is generated`() {
        val oldHash = "oldVersionHash123"

        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.drop(*allTables)
            SchemaUtils.create(MigrationTable)
            MigrationTable.deleteAll()
            MigrationTable.insert {
                it[MigrationTable.key] = MIGRATION_VERSION_KEY
                it[MigrationTable.value] = oldHash
            }
        }

        val result = trySyncDB()

        if (result is MigrationResult.NotMatch) {
            assertEquals(oldHash, result.oldHash)
        } else {
            throw AssertionError("Expected result to be MigrationResult.NotMatch, acuall: ${result::class}")
        }
    }
}
