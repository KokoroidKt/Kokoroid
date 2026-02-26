/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.pluginApi.session.container

fun interface SessionContainerFactory {
    fun createSessionContainer(): SessionContainer
}
