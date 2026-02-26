/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core

import ch.qos.logback.classic.Level
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import dev.kokoroidkt.core.constants.ExitStatus
import dev.kokoroidkt.core.di.allModules
import dev.kokoroidkt.core.runtime.KokoroidLauncher
import dev.kokoroidkt.core.runtime.status.InternalStatus
import dev.kokoroidkt.core.runtime.status.RuntimeStatus
import dev.kokoroidkt.core.utils.KokoroidVersion
import dev.kokoroidkt.coreApi.exceptions.CriticalException
import dev.kokoroidkt.coreApi.logging.LogFiles
import dev.kokoroidkt.coreApi.logging.LogLevelManager
import logger.getLogger
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
        try {
            // Initlazing

            if (isPrintVersion) {
                println("Kokoroid Version ${KokoroidVersion.version} (Build #${KokoroidVersion.gitHash})")
                return
            }

            LogLevelManager.setLevel(Level.INFO)
            if (isDebug) {
                LogLevelManager.setLevel(Level.DEBUG)
            }

            println(
                """
                               _                         _      _ 
                  /\ /\  ___  | | __  ___   _ __   ___  (_)  __| |
                 / //_/ / _ \ | |/ / / _ \ | '__| / _ \ | | / _` |
                / __ \ | (_) ||   < | (_) || |   | (_) || || (_| |
                \/  \/  \___/ |_|\_\ \___/ |_|    \___/ |_| \__,_|                                                            
                """.trimIndent(),
            )
            LogFiles.archiveLatestLogOnStartup(Paths.get("./kokoroid/logs"))
            logger.info { "Kokoroid Version ${KokoroidVersion.version} (Build #${KokoroidVersion.gitHash})" }
            logger.info { "「ちょー高尚な理由で 目指すは　ひとりぼっち産業革命」" }
            logger.info { "Kokoroid Starting....." }
            initKoin()
            try {
                getKoin().get<RuntimeStatus>().status = InternalStatus.Initializing()
                getKoin().get<KokoroidLauncher>().launch(isValidationOnly)
            } catch (e: CriticalException) {
                logger.error(e) { "Kokoroid Bootstrap Failed! Because：${e.javaClass.name}: ${e.message}" }
                exitProcess(ExitStatus.CRITICAL_ERROR_EXIT)
            }
        } catch (e: Exception) {
            logger.error(e) { "Kokoroid Initialized Failed! Because：${e.javaClass.name}: ${e.message}" }
            logger.error { "please save latest.log and report this to kokoroid issue" }
        }
    }

    private fun initKoin() {
        startKoin {
            logger.debug { "init koin" }
            modules(allModules)
        }
    }
}

fun main(args: Array<String>) {
    KokoroidBootstrap().main(args)
}
