/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.driverApi.driver

import kotlinx.serialization.Serializable

@Serializable
data class DriverMeta(
    /**
     * 驱动器名称
     * adapter name
     */
    val name: String,
    /**
     * 版本
     * version
     */
    val version: String,
    /**
     * 主类完整包名
     * The main class of this driver.
     */
    val mainClass: String,
    /**
     * 驱动器作者，驱动器作者是一个数组，如果只有一个作者，就在数组里面写一个名字即可
     * The author of this driver, it is an array, if there is only one author, just write one name in the array
     */
    val authors: List<String>?,
    /**
     * 驱动器简介
     * description
     */
    val description: String?,
    /**
     * 支持网站
     * support website
     */
    val website: String?,
    /**
     * 优先级
     * The priority of this driver.
     */
    val priority: Int = 1000,
)
