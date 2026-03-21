// SPDX-FileCopyrightText: 2026 Kokoroid Contributors

// SPDX-FileContributor: moran0710
//
// SPDX-License-Identifier: LGPL-2.1-or-later

package dev.kokoroidkt.pluginApi.plugin

interface Plugin {
    /**
     * 插件生命周期方法
     * 在插件被加载时调用
     * 此方法同步调用
     * Plugin Lifecycle Method
     * Called when the plugin is loaded.
     * This method is synchronous
     */
    fun onLoad()

    /**
     * 插件生命周期方法
     * 在插件被启用时调用
     * 此方法同步调用
     * Plugin Lifecycle Method
     * Called when the plugin is enabled.
     * This method is synchronous
     */
    fun onEnable()

    /**
     * 插件生命周期方法
     * 在插件被禁用时调用
     * 此方法同步调用
     * Plugin Lifecycle Method
     * Called when the plugin is disabled.
     * This method is synchronous
     */
    fun onDisable()

    /**
     * 插件生命周期方法
     * 在插件被卸载时调用
     * 此方法同步调用
     * Plugin Lifecycle Method
     * Called when the plugin is unloaded.
     * This method is synchronous
     */
    fun onUnload()
}
