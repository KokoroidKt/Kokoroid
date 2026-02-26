/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.exceptions

import java.io.File

class LoadAdapterFailedException(
    val msg: String,
    cause: Throwable? = null,
    val jarFile: File? = null,
) : CoreException(cause = cause) {
    override val message: String
        get() = "Plugin at ${jarFile?.absolutePath} was not loaded successfully: $msg"
}
