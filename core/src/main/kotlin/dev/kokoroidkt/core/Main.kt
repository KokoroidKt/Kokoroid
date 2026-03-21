// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core

import ch.qos.logback.classic.Level
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import dev.kokoroidkt.core.constants.ExitStatus
import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.core.logger.getLogger
import dev.kokoroidkt.core.runtime.KokoroidLauncher
import dev.kokoroidkt.core.runtime.state.InternalState
import dev.kokoroidkt.core.runtime.state.RuntimeState
import dev.kokoroidkt.core.utils.KokoroidVersion
import dev.kokoroidkt.coreApi.exceptions.CriticalException
import dev.kokoroidkt.coreApi.logging.LogFiles
import dev.kokoroidkt.coreApi.logging.LogLevelManager
import org.koin.core.context.GlobalContext.startKoin
import org.koin.java.KoinJavaComponent.getKoin
import java.nio.file.Paths
import kotlin.system.exitProcess

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

    val logger = getLogger("KokoroidBootstrap")

    override fun run() {
        if (isPrintVersion) {
            println("Kokoroid Version ${KokoroidVersion.version} (Build #${KokoroidVersion.gitHash})")
            return
        }

        KokoroidLauncher().launch(isValidationOnly, isDebug)
    }
}

fun main(args: Array<String>) {
    KokoroidBootstrap().main(args)
}
