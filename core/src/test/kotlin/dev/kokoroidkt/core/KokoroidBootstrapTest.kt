// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core

import com.github.ajalt.clikt.core.main
import dev.kokoroidkt.core.logger.getLogger
import dev.kokoroidkt.core.utils.KokoroidVersion
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class KokoroidBootstrapTest {
    private val logger = getLogger("KokoroidBootstrapTest")

    @Test
    fun test_if_print_version_works_correctly() {
        val args = arrayOf("--version")
        val output = captureOutput { KokoroidBootstrap().main(args) }
        val expectedOutput = "Kokoroid Version ${KokoroidVersion.version} (Build #${KokoroidVersion.gitHash})"

        assertTrue(output.contains(expectedOutput))
    }

    // Utility function to capture system output during execution
    private fun captureOutput(block: () -> Unit): String {
        val originalOut = System.out
        val outputStream = java.io.ByteArrayOutputStream().also { System.setOut(java.io.PrintStream(it)) }
        try {
            block()
        } finally {
            System.setOut(originalOut)
        }
        return outputStream.toString()
    }
}
