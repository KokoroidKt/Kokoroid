/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.exceptions

import java.io.File

class LoadPluginFailedException(
    val msg: String,
    override val cause: Throwable? = null,
    val jarFile: File? = null,
) : CoreException() {
    override val message: String
        get() = "Plugin at ${jarFile?.absolutePath} was not loaded successfully: $msg"
}
