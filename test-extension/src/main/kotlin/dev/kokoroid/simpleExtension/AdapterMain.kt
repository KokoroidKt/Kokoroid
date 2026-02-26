package dev.kokoroid.simpleExtension

import dev.kokoroid.simpleExtension.utils.Util
import dev.kokoroidkt.adapterApi.adapter.Adapter
import dev.kokoroidkt.adapterApi.logger.getLogger
import dev.kokoroidkt.coreApi.bot.Bot

class AdapterMain : Adapter {
    override fun onLoad() {
        getLogger().info { "Adapter Main Loaded and ${Util("AdapterMain").sayHi()}" }
    }

    override fun onStart() {
        getLogger().info { "Adapter Main started and ${Util("AdapterMain").sayHi()}" }
    }

    override fun onStop() {
        getLogger().info { "Adapter Main stopped and ${Util("AdapterMain").sayHi()}" }
    }

    override fun onUnload() {
        getLogger().info { "Adapter Main unLoaded and ${Util("AdapterMain").sayHi()}" }
    }

    override fun getBot(botId: String): Bot =
        object : Bot {
            override fun callApi(
                apiEndpoint: String,
                data: Any,
            ) {
            }

            override val botId: String
                get() = ""
        }

    override fun getBotList(): List<Bot> = listOf()
}
