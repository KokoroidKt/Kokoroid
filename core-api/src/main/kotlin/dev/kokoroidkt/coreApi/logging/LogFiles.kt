/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.coreApi.logging

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.zip.GZIPOutputStream
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.fileSize
import kotlin.io.path.isRegularFile

object LogFiles {
    private val archiveNameFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd-HH_mm_ss")

    /**
     * Minecraft 风格：
     * - 启动时：若 latest.log 存在且非空，则压缩为一个 .log.gz 归档
     * - 不生成额外的 run-xxx.log，只保留 latest.log + gz 归档
     */
    fun archiveLatestLogOnStartup(
        logDir: Path,
        latestFileName: String = "latest.log",
    ) {
        runCatching {
            logDir.createDirectories()

            val latest = logDir.resolve(latestFileName)
            if (!latest.exists() || !latest.isRegularFile()) return
            if (latest.fileSize() <= 0L) return

            val ts = LocalDateTime.now().format(archiveNameFormatter)
            val archivedGz = logDir.resolve("$ts.log.gz")

            // 防止极端情况下重名（同一秒多次启动）
            val target =
                if (!archivedGz.exists()) {
                    archivedGz
                } else {
                    logDir.resolve("$ts-${System.nanoTime()}.log.gz")
                }

            BufferedInputStream(Files.newInputStream(latest)).use { input ->
                GZIPOutputStream(BufferedOutputStream(Files.newOutputStream(target))).use { gz ->
                    input.copyTo(gz)
                }
            }

            // 压缩成功后删除旧 latest.log，让本次启动写出“全新 latest.log”
            Files.deleteIfExists(latest)
        }
    }
}
