// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.database

import dev.kokoroidkt.coreApi.database.tables.UserPermissionTable
import org.jetbrains.exposed.v1.core.Table

val allTables: Array<out Table> = arrayOf(UserPermissionTable)
