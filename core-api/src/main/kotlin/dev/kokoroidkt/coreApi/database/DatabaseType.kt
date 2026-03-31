// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1

package dev.kokoroidkt.coreApi.database

enum class DatabaseType(
    val jdbcClassName: String,
) {
    MYSQL("com.mysql.jdbc.Driver"),
    POSTGRESQL("org.postgresql.Driver"),
    H2("org.h2.Driver"),
    SQLITE("org.sqlite.JDBC"),
}
