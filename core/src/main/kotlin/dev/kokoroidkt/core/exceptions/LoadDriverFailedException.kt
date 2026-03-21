// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.exceptions

import java.io.File

class LoadDriverFailedException(
    val msg: String,
    override val cause: Throwable? = null,
    val jarFile: File? = null,
) : CoreException() {
    override val message: String
        get() = "Plugin at ${jarFile?.absolutePath} was not loaded successfully: $msg"
}
