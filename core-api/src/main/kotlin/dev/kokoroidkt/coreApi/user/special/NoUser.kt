// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.coreApi.user.special

import dev.kokoroidkt.coreApi.user.User
import dev.kokoroidkt.coreApi.user.Users

/**
 * 这是一个特殊的用户，代表无用户
 * 用于一些实际上没有办法开启对话的事件
 * 如：用户退出群聊，此时触发事件的是系统，且没办法形成上下文（用户退群自然永远不会有回复）
 *
 * @constructor Create empty No user
 */
object NoUser : User("dev.kokoroidkt.coreApi") {
    override val platfromUserId: String
        get() = "###NO-USER###"

    override val userId: String
        get() = platfromUserId

    val NO_USER_GROUP: Users = listOf(NoUser)
}
