package dev.kokoroid.transport.connection

interface Heartbeatable {
    val delayMillisecond: Long

    fun heartbeat()
}
