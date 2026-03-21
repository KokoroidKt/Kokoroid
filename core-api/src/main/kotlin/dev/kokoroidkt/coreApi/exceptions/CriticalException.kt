// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.coreApi.exceptions

/**
 * 致命异常
 * 遇到此异常时，Kokoroid应该原地崩溃并退出程序
 * Critical Exception
 * If this exceotion been thrown, Kokoroid MUST crash and exit
 */
open class CriticalException : KokoroidException {
    constructor(message: String) : super(message)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String, cause: Throwable?) : super(message, cause)
}
