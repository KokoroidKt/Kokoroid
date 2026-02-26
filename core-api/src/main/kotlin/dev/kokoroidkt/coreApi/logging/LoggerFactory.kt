/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.coreApi.logging

import kotlin.reflect.KClass

typealias LoggerFactory = (prefix: String, clazz: KClass<*>) -> KokoroidLogger
