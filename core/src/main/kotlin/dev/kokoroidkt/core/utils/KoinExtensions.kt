/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.core.utils

import org.koin.core.definition.KoinDefinition
import kotlin.reflect.KClass
import org.koin.dsl.binds as koinBinds

fun <T : Any> KoinDefinition<T>.binds(vararg classes: KClass<*>): KoinDefinition<T> {
    this.koinBinds(arrayOf(*classes))
    return this
}
