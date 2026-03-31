// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.database.tables

import org.jetbrains.exposed.v1.core.Table

object MigrationTable : Table("kokoroid_migrations") {
    val key = varchar("key", 32)
    val value = varchar("value", 64)
    // override val primaryKey = PrimaryKey(key, name = "key")
}
