// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import dev.kokoroidkt.core.logger.getLogger
import dev.kokoroidkt.core.runtime.KokoroidLauncher
import dev.kokoroidkt.core.utils.KokoroidVersion

class KokoroidBootstrap :
    CliktCommand(
        name = "Kokoroid",
    ) {
    val isPrintVersion by option("-v", "--version", help = "Print kokoroid version.").flag()
    val isDebug by option("-d", "--debug", help = "Print kokoroid version.").flag()
    val isValidationOnly by option(
        "-V",
        "--validation-only",
        help = "Only validate configuration & extension avalliable, do not start Kokoroid.",
    ).flag()

    val doMigration by option(
        "-m",
        "--migration",
        help = "Run database migration.",
    ).flag()

    val logger = getLogger("KokoroidBootstrap")

    override fun run() {
        if (isPrintVersion) {
            println("Kokoroid Version ${KokoroidVersion.version} (Build #${KokoroidVersion.gitHash})")
            return
        }

        var isConfirm = false
        if (doMigration) {
            logger.warn {
                "WARNING: You enabled migration flag, it will execute database migration, " +
                    "please BACKUP your database before proceeding."
            }
            println("Confirm? [Y]es/[N]o")
            val confirm = readlnOrNull()
            if (confirm == null) {
                isConfirm = false
                logger.warn { "stdin not exist, set NO automatically" }
            } else if (confirm.lowercase().startsWith("y")) {
                isConfirm = true
                logger.warn { "Kokoroid database will be automatically migrated" }
            } else {
                isConfirm = false
                logger.warn { "Migration option has been set to false" }
            }
        }

        KokoroidLauncher().launch(isValidationOnly, isDebug, doMigration && isConfirm)
    }
}

fun main(args: Array<String>) {
    KokoroidBootstrap().main(args)
}
