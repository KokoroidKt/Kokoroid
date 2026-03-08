/*
 * Copyright (c) 2026 moran0710 and Kokoroid contributors
 * MIT License
 */

package dev.kokoroidkt.adapterApi.adapter

import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.user.User
import dev.kokoroidkt.coreApi.user.UserContainer

interface Adapter {
    /**
     * 适配器生命周期方法
     * 在适配器加载时调用
     * Adapter lifecycle method
     * call when adapter be loaded
     */
    fun onLoad()

    /**
     * 适配器生命周期方法
     * 在所有Plugin加载好后调用
     */
    fun onStart()

    /**
     * 适配器生命周期方法
     * 在适配器停止时调用
     *
     */
    fun onStop()

    /**
     * 适配器生命周期方法
     * 在适配器卸载时调用
     * Adapter lifecycle method
     * call when adapter be unloaded
     */
    fun onUnload()

    /**
     * 获取一个Bot
     */
    fun getBot(botId: String): Bot

    /**
     * 获取所有Bot列表
     */
    fun getBotList(): List<Bot>

    /**
     * Get user container
     *
     * @return
     */
    fun getUserContainer(): UserContainer
}
