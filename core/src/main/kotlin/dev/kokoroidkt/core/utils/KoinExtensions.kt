// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.core.utils

import org.koin.core.definition.KoinDefinition
import kotlin.reflect.KClass
import org.koin.dsl.binds as koinBinds

fun <T : Any> KoinDefinition<T>.binds(vararg classes: KClass<*>): KoinDefinition<T> {
    this.koinBinds(arrayOf(*classes))
    return this
}
