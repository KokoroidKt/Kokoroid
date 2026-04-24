// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.transport.raw

/**
 * 驱动器收到的原始请求
 *
 * @property data 原始数据
 * @property attribute 附加属性，用于标识一些传输过程的信息，可以通过[AttrKey]查看一些内置的约定
 * @constructor Create empty Raw
 */
data class Raw(
    val data: Data,
    val attribute: Map<String, String>,
)
