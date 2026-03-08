package dev.kokoroid.simpleExtension

import dev.kokoroid.simpleExtension.utils.Util
import dev.kokoroidkt.adapterApi.adapter.Adapter
import dev.kokoroidkt.adapterApi.logger.getLogger
import dev.kokoroidkt.coreApi.bot.Bot
import dev.kokoroidkt.coreApi.event.Event
import dev.kokoroidkt.coreApi.message.MessageChain
import dev.kokoroidkt.coreApi.user.User
import dev.kokoroidkt.coreApi.user.UserContainer
import kotlinx.serialization.json.JsonElement

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
                data: JsonElement,
            ) {
            }

            override fun replyMessage(
                event: Event,
                message: MessageChain,
            ) {
            }

            override val botId: String
                get() = ""
        }

    override fun getBotList(): List<Bot> = listOf()

    override fun getUserContainer(): UserContainer =
        object : UserContainer {
            override fun getUserById(userId: String): User? = null

            override val size: Int = 0

            override fun isEmpty(): Boolean = true

            override fun contains(element: User): Boolean = false

            override fun iterator(): Iterator<User> = emptyList<User>().iterator()

            override fun containsAll(elements: Collection<User>): Boolean = false
        }
}
