/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.coreApi.exceptions

/**
 * Kokoroid相关异常基类
 * The basee exception of Kokoroid
 */
open class KokoroidException : Exception {
    constructor(message: String) : super(message)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String, cause: Throwable?) : super(message, cause)
}
