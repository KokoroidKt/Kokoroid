// SPDX-FileCopyrightText: 2026 Kokoroid Contributors
// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.simpleExtension.config

import dev.kokoroidkt.coreApi.annotation.WithComment
import kotlinx.serialization.Serializable

@Serializable
@WithComment("Hello World")
data class MockConfig(
    val foo: String,
    @WithComment("Hello World")
    val bar: Int,
)
